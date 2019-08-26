package com.zeroq.daudi_3_native.ui.average_prices

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseActivity
import com.zeroq.daudi_3_native.data.models.AveragePriceModel
import com.zeroq.daudi_3_native.data.models.OmcModel
import com.zeroq.daudi_3_native.data.models.UserModel
import com.zeroq.daudi_3_native.ui.dialogs.AverageDialogFragment
import com.zeroq.daudi_3_native.ui.dialogs.data.AverageDialogEvent
import kotlinx.android.synthetic.main.activity_average_price.*
import kotlinx.android.synthetic.main.pms_average_card.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.toast
import timber.log.Timber

class AveragePriceActivity : BaseActivity() {


    lateinit var viewModel: AverageViewModel
    private var omcs: List<OmcModel>? = null
    private var userModel: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_average_price)

        viewModel = getViewModel(AverageViewModel::class.java)

        setupToolbar()

        pmsAverageParent.setOnClickListener {
            toggleSlide(pmsPriceList)
        }

        viewModel.getOmcs().observe(this, Observer {
            if (it.isSuccessful) {
                omcs = it.data()
            } else {
                omcs = null
                Timber.e(it.error())
            }
        })

        viewModel.getUser().observe(this, Observer {
            if (it.isSuccessful) {
                userModel = it.data()

                viewModel.setDeportId(userModel?.config?.depotdata?.depotid)
            } else {
                userModel = null
                Timber.e(it.error())
            }
        })

        populateFuelView()

        addFuel.setOnClickListener {
            if (!omcs.isNullOrEmpty()) {
                val dialog = AverageDialogFragment(omcs!!)
                dialog.show(supportFragmentManager, "average")

                dialog.averageEvent.subscribe {
                    if (userModel != null) {
                        submitFuelPrices(it)
                    } else {
                        toast("user data is not yet fetched")
                    }
                }

            } else {
                toast("No Omcs vailable, wait and then try again")
            }
        }


    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Average Prices"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun toggleSlide(view: View) {
        if (view.visibility == View.VISIBLE) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    }

    private fun submitFuelPrices(data: AverageDialogEvent) {
        viewModel.postOmcAveragePrice(data).observe(this, Observer {
            if (it.isSuccessful) {
                toast("success")
            } else {
                toast("An error occurred while posting fuel information")
                Timber.e(it.error())
            }
        })
    }


    private fun populateFuelView() {
        viewModel.getTodayPrices().observe(this, Observer {
            if (it.isSuccessful) {
                val pmsPrices: ArrayList<AveragePriceModel> = ArrayList()
                val agoPrices: ArrayList<AveragePriceModel> = ArrayList()
                val ikPrices: ArrayList<AveragePriceModel> = ArrayList()

                it.data()?.forEach { avgModel ->
                    when (avgModel.fueltytype) {
                        "pms" -> {
                            pmsPrices.add(avgModel)
                        }

                        "ago" -> {
                            agoPrices.add(avgModel)
                        }

                        "ik" -> {
                            ikPrices.add(avgModel)
                        }
                    }
                }

            } else {
                Timber.e(it.error())
            }
        })
    }
}
