package com.zeroq.daudi_3_native.ui.loading


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.adapters.LoadingTrucksAdapter
import com.zeroq.daudi_3_native.commons.BaseFragment
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.events.LoadingEvent
import com.zeroq.daudi_3_native.ui.dialogs.LoadingDialogFragment
import com.zeroq.daudi_3_native.ui.dialogs.TimeDialogFragment
import com.zeroq.daudi_3_native.ui.loading_order.LoadingOrderActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_loading.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class LoadingFragment : BaseFragment() {


    private lateinit var adapter: LoadingTrucksAdapter
    private var _TAG: String = "LoadingFragment"

    lateinit var viewModel: LoadingViewModel
    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel(LoadingViewModel::class.java)

        viewModel.getUser().observe(this, Observer {
            if (it.isSuccessful) {
                val user = it.data()
                viewModel.setDepoId(user?.config?.depotdata?.depotid!!)
            } else {
                Timber.e(it.error()!!)
            }
        })

        initRecyclerView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: LoadingEvent) {
        if (event.error == null) adapter.replaceTrucks(event.trucks!!)
    }

    private fun initRecyclerView() {
        adapter = LoadingTrucksAdapter()

        loading_view.layoutManager = LinearLayoutManager(activity)
        loading_view.adapter = adapter
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        consumeEvents()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop()
    }


    private fun consumeEvents() {
        val clickSub: Disposable = adapter.expireTvClick
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                expireTimePicker(it.truck)
            }

        val cardBodyClick: Disposable = adapter.cardBodyClick
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                sealForm(it.truck)
            }

        compositeDisposable.add(clickSub)
        compositeDisposable.add(cardBodyClick)
    }

    var expireSub: Disposable? = null
    private fun expireTimePicker(truck: TruckModel) {
        expireSub?.dispose()
        expireSub = null

        val expireDialog = TimeDialogFragment("Enter Additional Time", truck)
        expireSub = expireDialog.timeEvent.subscribe {
            viewModel.updateExpire(it.truck.Id!!, it.minutes.toLong())
                .observe(this, Observer { result ->
                    if (!result.isSuccessful) {
                        Toast.makeText(
                            activity,
                            "Error occurred when adding expire", Toast.LENGTH_SHORT
                        ).show()

                        Timber.e(result.error())
                    }
                })
        }

        expireDialog.show(fragmentManager!!, _TAG)
    }

    var sealSub: Disposable? = null
    private fun sealForm(truck: TruckModel) {
        sealSub?.dispose()
        sealSub = null

        if (truck?.stagedata?.get("4")?.data == null) {
            val sealDialog = LoadingDialogFragment(truck)
            sealSub = sealDialog.loadingEvent.subscribe {
                sealDialog.dismiss() // hide dialog

                viewModel.updateSeals(truck.Id!!, it).observe(this, Observer { result ->
                    if (result.isSuccessful) {
                        startLoadingOrderActivity(truck.Id!!)
                    } else {
                        Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                        Timber.e(result.error())
                    }
                })
            }

            sealDialog.show(fragmentManager!!, _TAG)
        } else {
            startLoadingOrderActivity(truck.Id!!)
        }
    }


    private fun startLoadingOrderActivity(idTruck: String) {
        val intent = Intent(activity, LoadingOrderActivity::class.java)
        intent.putExtra(LoadingOrderActivity.ID_TRUCK_EXTRA, idTruck)

        startActivity(intent)

    }
}
