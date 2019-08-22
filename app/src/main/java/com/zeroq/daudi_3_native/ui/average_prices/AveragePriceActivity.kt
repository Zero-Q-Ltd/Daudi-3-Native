package com.zeroq.daudi_3_native.ui.average_prices

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zeroq.daudi_3_native.R
import kotlinx.android.synthetic.main.pms_average_card.*
import kotlinx.android.synthetic.main.toolbar.*

class AveragePriceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_average_price)

        setupToolbar()

        pmsAverageParent.setOnClickListener {
            toggleSlide(pmsPriceList)
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
