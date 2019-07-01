package com.zeroq.daudi_3_native.adapters

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.data.models.TruckModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.floor


class ProcessingTrucksAdapter : RecyclerView.Adapter<ProcessingTrucksAdapter.TruckViewHolder>() {


    private val trucksList = ArrayList<TruckModel>()
    private lateinit var context: Context


    fun replaceTrucks(trucks: List<TruckModel>) {
        if (trucksList.size > 0) trucksList.clear()

        trucksList.addAll(trucks)
        this.notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TruckViewHolder {
        val inflatedView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.truck_card_layout, parent, false)

        context = parent.context

        return TruckViewHolder(inflatedView)
    }

    override fun getItemCount() = trucksList.size

    override fun onBindViewHolder(holder: TruckViewHolder, position: Int) {
        var truck = trucksList[position]
        holder.bindPhoto(truck, context)

        if (holder.timerSubscription != null) {
            holder.timerSubscription?.dispose()
            holder.timerSubscription = null
        }

        // set timer
        var stageTime = truck.stagedata!!["1"]?.data?.expiry!![0].timestamp!!.time
        val currentTime = Calendar.getInstance().time.time

        val diffTime = floor(stageTime.minus(currentTime).toDouble() / 1000).toLong()

        if (diffTime > 0) {
            holder.bottomLinearBar?.setBackgroundResource(R.color.colorPrimary)
            holder.expireTruckIndicator?.text = ""

            val observable = Observable.interval(1, TimeUnit.SECONDS)
            holder.timerSubscription =
                observable
                    .take(diffTime)
                    .map { (diffTime - 1) - it }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it > 0) {
                            holder.expireTruckIndicator?.text =
                                DateUtils.formatElapsedTime(it).format("HH:mm:ss")
                        } else {
                            holder.expireTruckIndicator?.text = "Expired"
                        }
                    },
                        {
                            Timber.e(it)
                        })

        } else {
            holder.bottomLinearBar?.setBackgroundResource(R.color.pms)
            holder.expireTruckIndicator?.text = "Expired"
        }

        /**
         * Times added
         * */
        holder.timesTruckAddedView?.setOnClickListener {
            holder.truckAddedSubscription?.dispose()
            holder.truckAddedSubscription = null


            holder.timesTruckAddedView?.text = truck.stagedata!!["1"]?.data?.expiry?.size.toString()

            holder.truckAddedSubscription = Observable.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    holder.timesTruckAddedView?.text = "Times Added"
                }
        }

        /**
         * trucks ahead
         * */

        holder.trucksAheadView?.setOnClickListener {
            val trucksAhead: Int = trucksList.slice(0 until position).size
            holder.trucksAheadView?.text = trucksAhead.toString()

            holder.truckaheadSubscription?.dispose()
            holder.truckaheadSubscription = null

            holder.truckaheadSubscription =
                Observable.timer(2, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        holder.trucksAheadView?.text = "Trucks Ahead"
                    }
        }
    }


    class TruckViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var _orderNumber: TextView? = null
        private var numberPlate: TextView? = null

        private var _companyName: TextView? = null
        private var _driverName: TextView? = null
        private var _phoneNumber: TextView? = null

        private var _pmsTotal: TextView? = null
        private var _agoTotal: TextView? = null
        private var _ikTotal: TextView? = null

        private var comp0FuelType: ImageView? = null
        private var comp0FuelQuantity: TextView? = null

        private var comp1FuelType: ImageView? = null
        private var comp1FuelQuantity: TextView? = null

        private var comp2FuelType: ImageView? = null
        private var comp2FuelQuantity: TextView? = null

        private var comp3FuelType: ImageView? = null
        private var comp3FuelQuantity: TextView? = null

        private var comp4FuelType: ImageView? = null
        private var comp4FuelQuantity: TextView? = null

        private var comp5FuelType: ImageView? = null
        private var comp5FuelQuantity: TextView? = null

        private var comp6FuelType: ImageView? = null
        private var comp6FuelQuantity: TextView? = null


        var expireTruckIndicator: TextView? = null
        var bottomLinearBar: LinearLayout? = null

        // trucks ahead
        var trucksAheadView: TextView? = null
        var timesTruckAddedView: TextView? = null

        private var varibles = ArrayList<CompVariable>()

        /**
         * timerSubscription for timer
         * */
        var timerSubscription: Disposable? = null
        var truckaheadSubscription: Disposable? = null
        var truckAddedSubscription: Disposable? = null


        init {

            _orderNumber = v.findViewById(R.id.tv_order_number)
            numberPlate = v.findViewById(R.id.tv_number_plate)

            _companyName = v.findViewById(R.id.tv_company)
            _driverName = v.findViewById(R.id.tv_driver)
            _phoneNumber = v.findViewById(R.id.tv_phone)

            _pmsTotal = v.findViewById(R.id.tv_pms)
            _agoTotal = v.findViewById(R.id.tv_ago)
            _ikTotal = v.findViewById(R.id.tv_ik)

            comp0FuelType = v.findViewById(R.id.iv_c0)
            comp0FuelQuantity = v.findViewById(R.id.tv_c0)
            varibles.add(CompVariable(comp0FuelType, comp0FuelQuantity))

            comp1FuelType = v.findViewById(R.id.iv_c1)
            comp1FuelQuantity = v.findViewById(R.id.tv_c1)
            varibles.add(CompVariable(comp1FuelType, comp1FuelQuantity))

            comp2FuelType = v.findViewById(R.id.iv_c2)
            comp2FuelQuantity = v.findViewById(R.id.tv_c2)
            varibles.add(CompVariable(comp2FuelType, comp2FuelQuantity))

            comp3FuelType = v.findViewById(R.id.iv_c3)
            comp3FuelQuantity = v.findViewById(R.id.tv_c3)
            varibles.add(CompVariable(comp3FuelType, comp3FuelQuantity))

            comp4FuelType = v.findViewById(R.id.iv_c4)
            comp4FuelQuantity = v.findViewById(R.id.tv_c4)
            varibles.add(CompVariable(comp4FuelType, comp4FuelQuantity))

            comp5FuelType = v.findViewById(R.id.iv_c5)
            comp5FuelQuantity = v.findViewById(R.id.tv_c5)
            varibles.add(CompVariable(comp5FuelType, comp5FuelQuantity))

            comp6FuelType = v.findViewById(R.id.iv_c6)
            comp6FuelQuantity = v.findViewById(R.id.tv_c6)
            varibles.add(CompVariable(comp6FuelType, comp6FuelQuantity))

            expireTruckIndicator = v.findViewById(R.id.tv_expire)

            bottomLinearBar = v.findViewById(R.id.l_bottom)
            trucksAheadView = v.findViewById(R.id.tv_trucks_ahead)
            timesTruckAddedView = v.findViewById(R.id.tv_times_added)

        }

        fun bindPhoto(truck: TruckModel, context: Context) {
            _orderNumber?.text = truck.truckId
            numberPlate?.text = truck.numberplate
            _companyName?.text = truck.company?.name
            _driverName?.text = truck.drivername
            _phoneNumber?.text = truck.company?.phone

            _pmsTotal?.text = truck.fuel?.pms?.qty.toString()
            _agoTotal?.text = truck.fuel?.ago?.qty.toString()
            _ikTotal?.text = truck.fuel?.ik?.qty.toString()

            truck.compartments?.forEachIndexed { index, compartment ->
                setCompValues(index, compartment.fueltype, compartment.qty, context)
            }

        }

        private fun setCompValues(index: Int, fuelType: String?, quantity: Int?, context: Context) {
            when (fuelType) {
                "pms" -> {
                    varibles[index].fuelQuantity?.text = quantity.toString()

                    varibles[index].fuelType?.setColorFilter(
                        ContextCompat.getColor(context, R.color.pms),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }

                "ago" -> {
                    varibles[index].fuelQuantity?.text = quantity.toString()

                    varibles[index].fuelType?.setColorFilter(
                        ContextCompat.getColor(context, R.color.ago),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )

                }

                "ik" -> {
                    varibles[index].fuelQuantity?.text = quantity.toString()

                    varibles[index].fuelType?.setColorFilter(
                        ContextCompat.getColor(context, R.color.ik),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            }
        }

    }

    data class CompVariable(var fuelType: ImageView?, var fuelQuantity: TextView?)

}