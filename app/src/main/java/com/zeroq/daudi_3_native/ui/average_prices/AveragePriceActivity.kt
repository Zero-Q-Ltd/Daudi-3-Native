package com.zeroq.daudi_3_native.ui.average_prices

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseActivity
import com.zeroq.daudi_3_native.data.models.OmcModel
import com.zeroq.daudi_3_native.ui.dialogs.AverageDialogFragment
import kotlinx.android.synthetic.main.activity_average_price.*
import kotlinx.android.synthetic.main.pms_average_card.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.toast
import timber.log.Timber

class AveragePriceActivity : BaseActivity() {


    lateinit var viewModel: AverageViewModel
    private var omcs: List<OmcModel>? = null

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

        addFuel.setOnClickListener {
            if (!omcs.isNullOrEmpty()) {
                val dialog = AverageDialogFragment(omcs!!)
                dialog.show(supportFragmentManager, "average")
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
}
