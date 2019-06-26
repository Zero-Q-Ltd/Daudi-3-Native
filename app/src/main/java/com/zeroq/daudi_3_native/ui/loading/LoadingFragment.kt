package com.zeroq.daudi_3_native.ui.loading


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.adapters.LoadingTrucksAdapter
import com.zeroq.daudi_3_native.commons.BaseFragment
import com.zeroq.daudi_3_native.events.LoadingEvent
import kotlinx.android.synthetic.main.fragment_loading.*
import kotlinx.android.synthetic.main.fragment_processing.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoadingFragment : BaseFragment() {


    private lateinit var adapter: LoadingTrucksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop()
    }
}
