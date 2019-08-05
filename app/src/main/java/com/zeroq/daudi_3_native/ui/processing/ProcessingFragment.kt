package com.zeroq.daudi_3_native.ui.processing


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.adapters.ProcessingTrucksAdapter
import com.zeroq.daudi_3_native.commons.BaseFragment
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.events.ProcessingEvent
import com.zeroq.daudi_3_native.ui.dialogs.TimeDialogFragment
import com.zeroq.daudi_3_native.ui.truck_detail.TruckDetailActivity
import com.zeroq.daudi_3_native.utils.ActivityUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_processing.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

class ProcessingFragment : BaseFragment() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var activityUtil: ActivityUtil

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
                Timber.e(it.error()!!)
            }
        })

        /*
        * start dialog
        * */

        initRecyclerView()
        activityUtil.showProgress(spin_kit, true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: ProcessingEvent) {

        activityUtil.showProgress(spin_kit, false)

        if (event.error == null) {

            if (event.trucks.isNullOrEmpty()) {
                activityUtil.showTextViewState(
                    empty_view, true, "No trucks are in Processing",
                    resources.getColor(R.color.colorPrimaryText)
                )
            } else {
                activityUtil.showTextViewState(
                    empty_view, false, null, null
                )
                adapter.replaceTrucks(event.trucks)
            }
        } else {
            activityUtil.showTextViewState(
                empty_view, true,
                "Something went wrong please, close the application to see if the issue wll be solved",
                resources.getColor(R.color.pms)
            )
        }
    }

    private fun initRecyclerView() {
        adapter = ProcessingTrucksAdapter()

        processing_view.layoutManager = LinearLayoutManager(activity)
        processing_view.adapter = adapter
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
                val printed = it?.truck?.isprinted

                if (printed!!) {
                    queueTruckDialog(it.truck)
                } else {
                    startTruckDetailActivity(it.truck.Id)
                }
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
            processingViewModel.updateExpire(truck, it.minutes.toLong()).observe(this, Observer { state ->
                if (!state.isSuccessful) {
                    Toast.makeText(activity, "sorry an error occurred", Toast.LENGTH_SHORT).show()
                    Timber.e(state.error())
                }
            })
        }

        expireDialog.show(fragmentManager!!, _TAG)
    }


    var queueSub: Disposable? = null
    private fun queueTruckDialog(truck: TruckModel) {
        queueSub?.dispose()
        queueSub = null

        val queueDialog = TimeDialogFragment("Enter Queueing Time", truck)
        queueSub = queueDialog.timeEvent.subscribe {
            processingViewModel.moveToQueuing(it.truck.Id!!, it.minutes.toLong())
                .observe(this, Observer { result ->
                    if (result.isSuccessful) {
                        Toast.makeText(activity, "Truck moved to processing", Toast.LENGTH_SHORT).show()
                    } else {
                        Timber.e(result.error())
                    }
                })
        }

        queueDialog.show(fragmentManager!!, _TAG)
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

    private fun startTruckDetailActivity(truckId: String?) {
        val intent = Intent(activity, TruckDetailActivity::class.java)
        intent.putExtra("TRUCK_ID", truckId)
        startActivity(intent)
        // animate
        activity!!.overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
    }
}
