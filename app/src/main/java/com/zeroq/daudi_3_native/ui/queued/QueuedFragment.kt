package com.zeroq.daudi_3_native.ui.queued


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.adapters.QueuedTrucksAdapter
import com.zeroq.daudi_3_native.commons.BaseFragment
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.events.QueueingEvent
import com.zeroq.daudi_3_native.ui.dialogs.TimeDialogFragment
import com.zeroq.daudi_3_native.utils.ActivityUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_processing.*
import kotlinx.android.synthetic.main.fragment_queued.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

class QueuedFragment : BaseFragment() {

    private lateinit var adapter: QueuedTrucksAdapter
    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    lateinit var queuedViewModel: QueuedViewModel

    @Inject
    lateinit var activityUtil: ActivityUtil

    private var _TAG: String = "QueuedFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_queued, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        queuedViewModel = getViewModel(QueuedViewModel::class.java)

        queuedViewModel.getUser().observe(this, Observer {
            if (it.isSuccessful) {
                val user = it.data()
                queuedViewModel.setDepoId(user?.config?.depotdata?.depotid!!)
            } else {
                Timber.e(it.error()!!)
            }
        })

        initRecyclerView()
        activityUtil.showProgress(spin_kit_q, true)
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: QueueingEvent) {

        activityUtil.showProgress(spin_kit_q, false)

        if (event.error == null) {

            if (event.trucks.isNullOrEmpty()) {
                adapter.clear()
                activityUtil.showTextViewState(
                    empty_view_q, true, "No trucks are in Queueing",
                    resources.getColor(R.color.colorPrimaryText)
                )

            } else {
                activityUtil.showTextViewState(
                    empty_view_q, false, null, null
                )
                adapter.replaceTrucks(event.trucks)
            }
        } else {
            adapter.clear()
            activityUtil.showTextViewState(
                empty_view_q, true,
                "Something went wrong please, close the application to see if the issue wll be solved",
                resources.getColor(R.color.pms)
            )
        }
    }

    private fun initRecyclerView() {
        adapter = QueuedTrucksAdapter(activityUtil)

        queueing_view.layoutManager = LinearLayoutManager(activity)
        queueing_view.adapter = adapter
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


    private fun consumeEvents() {
        val expireClick =
            adapter.expireTvClick.observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    expireTimePicker(it.truck)
                }


        val bodyClick = adapter.cardBodyClick.observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                pushToLoadingDialog(it.truck)
            }


        compositeDisposable.add(expireClick)
        compositeDisposable.add(bodyClick)
    }


    var expireSub: Disposable? = null

    private fun expireTimePicker(truck: TruckModel) {
        expireSub?.dispose()
        expireSub = null

        val expireDialog = TimeDialogFragment("Enter Additional Time", truck)
        expireSub = expireDialog.timeEvent.subscribe { results ->

            queuedViewModel.updateExpire(results.truck.Id!!, results.minutes.toLong())
                .observe(this, Observer { state ->
                    if (!state.isSuccessful) {
                        Toast.makeText(activity, "sorry an error occurred", Toast.LENGTH_SHORT).show()
                        Timber.e(state.error())
                    }
                })
        }

        expireDialog.show(fragmentManager!!, _TAG)
    }


    var LoadingSub: Disposable? = null
    private fun pushToLoadingDialog(truck: TruckModel) {
        LoadingSub?.dispose()
        LoadingSub = null


        val toLoadingDialog = TimeDialogFragment("Enter Loading Time", truck)
        expireSub = toLoadingDialog.timeEvent.subscribe { results ->

            queuedViewModel.pushToLoading(results.truck.Id!!, results.minutes.toLong())
                .observe(this, Observer { state ->
                    if (!state.isSuccessful) {
                        Toast.makeText(activity, "sorry an error occurred", Toast.LENGTH_SHORT).show()
                        Timber.e(state.error())
                    }
                })
        }

        toLoadingDialog.show(fragmentManager!!, _TAG)

    }

}
