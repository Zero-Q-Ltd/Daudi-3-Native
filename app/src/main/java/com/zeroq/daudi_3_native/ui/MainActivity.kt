package com.zeroq.daudi_3_native.ui

import android.content.Context
import android.os.Bundle
import com.zeroq.daudi_3_native.R
import android.content.Intent
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zeroq.daudi_3_native.commons.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.toolbar


class MainActivity : BaseActivity() {

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbar()
        setBottomNavigation()

    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)

        supportActionBar!!.title = "Morning"
    }

    private fun setBottomNavigation() {
        // bottom navigation
        bottom_nav.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_processing -> {
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_queued -> {

                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_loading -> {
                    return@OnNavigationItemSelectedListener true
                }
            }
            return@OnNavigationItemSelectedListener false
        })
    }
}
