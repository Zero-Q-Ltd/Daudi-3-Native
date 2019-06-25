package com.zeroq.daudi_3_native.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.OnItemClickedListener
import com.zeroq.daudi_3_native.commons.OnItemLongClickedListener
import com.zeroq.daudi_3_native.data.models.TruckModel

class TrucksAdapter : RecyclerView.Adapter<TrucksAdapter.TruckViewHolder>() {

    lateinit var onClickListener: OnItemClickedListener<TruckModel>
    lateinit var onLongClickListener: OnItemLongClickedListener<TruckModel>


    private val trucksList = ArrayList<TruckModel>()

    fun setClickListener(listener: OnItemClickedListener<TruckModel>) {
        onClickListener = listener
    }

    fun setLongClickListener(listener: OnItemLongClickedListener<TruckModel>) {
        onLongClickListener = listener
    }


    fun replaceTrucks(trucks: List<TruckModel>) {
        if (trucksList.size > 0) trucksList.clear()

        trucksList.addAll(trucks)
        this.notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TruckViewHolder {
        val inflatedView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.truck_card_layout, parent, false)

        return TruckViewHolder(inflatedView)
    }

    override fun getItemCount() = trucksList.size

    override fun onBindViewHolder(holder: TruckViewHolder, position: Int) {
        holder.bindPhoto(trucksList[position])
    }


    class TruckViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun bindPhoto(truck: TruckModel) {

        }
    }

}