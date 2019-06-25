package com.zeroq.daudi_3_native.ui.processing


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.adapters.TrucksAdapter
import com.zeroq.daudi_3_native.commons.BaseFragment
import com.zeroq.daudi_3_native.commons.OnItemClickedListener
import com.zeroq.daudi_3_native.commons.OnItemLongClickedListener
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.events.ProcessingEvent
import kotlinx.android.synthetic.main.fragment_processing.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class ProcessingFragment : BaseFragment(), OnItemClickedListener<TruckModel>, OnItemLongClickedListener<TruckModel> {


    private lateinit var adapter: TrucksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_processing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: ProcessingEvent) {
        if (event.error == null) adapter.replaceTrucks(event.trucks!!)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop()
    }

    private fun initRecyclerView() {
        adapter = TrucksAdapter()

        adapter.setClickListener(this)
        adapter.setLongClickListener(this)

        processing_view.layoutManager = LinearLayoutManager(activity)
        processing_view.adapter = adapter
    }

    override fun onClicked(item: TruckModel) {
        Timber.d("Magic is happening right now")
    }


    override fun longClicked(item: TruckModel) {
        Timber.d("long clicked")
    }
}
