package com.zeroq.daudi_3_native.ui.queued


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.adapters.QueuedTrucksAdapter
import com.zeroq.daudi_3_native.commons.BaseFragment
import com.zeroq.daudi_3_native.events.ProcessingEvent
import com.zeroq.daudi_3_native.events.QueueingEvent
import kotlinx.android.synthetic.main.fragment_queued.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class QueuedFragment : BaseFragment() {

    private lateinit var adapter: QueuedTrucksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_queued, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: QueueingEvent) {
        if (event.error == null) adapter.replaceTrucks(event.trucks!!)
    }

    private fun initRecyclerView() {
        adapter = QueuedTrucksAdapter()

        queueing_view.layoutManager = LinearLayoutManager(activity)
        queueing_view.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop()
    }

}
