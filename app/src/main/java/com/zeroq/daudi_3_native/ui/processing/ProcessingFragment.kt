package com.zeroq.daudi_3_native.ui.processing


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.adapters.ProcessingTrucksAdapter
import com.zeroq.daudi_3_native.commons.BaseFragment
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.events.ProcessingEvent
import com.zeroq.daudi_3_native.ui.dialogs.TimeDialogFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_processing.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class ProcessingFragment : BaseFragment() {


    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var adapter: ProcessingTrucksAdapter
    private var _TAG: String = "ProcessingFragment"

    private lateinit var processingViewModel: ProcessingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_processing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        processingViewModel = getViewModel(ProcessingViewModel::class.java)

        processingViewModel.getUser().observe(this, Observer {
            if (it.isSuccessful) {
                val user = it.data()
                processingViewModel.setDepoId(user?.config?.depotdata?.depotid!!)
            } else {
                Timber.e(it.error())
            }
        })

        /*
        * start dialog
        * */

        initRecyclerView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: ProcessingEvent) {
        if (event.error == null) adapter.replaceTrucks(event.trucks!!)
    }

    private fun initRecyclerView() {
        adapter = ProcessingTrucksAdapter()

        processing_view.layoutManager = LinearLayoutManager(activity)
        processing_view.adapter = adapter
    }

    private fun consumeEvents() {
        var clickSub: Disposable = adapter.expireTvClick
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                expireTimePicker(it.truck)
            }

        compositeDisposable.add(clickSub)
    }


    var expireSub: Disposable? = null

    fun expireTimePicker(truck: TruckModel) {
        expireSub?.dispose()
        expireSub = null

        val expireDialog = TimeDialogFragment("Enter Additional Time", truck)
        expireSub = expireDialog.timeEvent.subscribe {
            processingViewModel.updateExpire(truck, it.minutes.toLong()).observe(this, Observer { state ->
                if (!state.isSuccessful) {
                    Toast.makeText(activity, "sorry an error occurred", Toast.LENGTH_SHORT).show()
                    Timber.e(state.error())
                }
            })
        }

        expireDialog.show(fragmentManager!!, _TAG)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        consumeEvents()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        compositeDisposable.clear()
        super.onStop()
    }
}
