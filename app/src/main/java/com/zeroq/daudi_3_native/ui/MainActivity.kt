package com.zeroq.daudi_3_native.ui

import android.content.Context
import android.os.Bundle
import com.zeroq.daudi_3_native.R
import android.content.Intent
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
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

        if (savedInstanceState == null)
            setupBottomNavigationBar()

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }


    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.mainNavFragment).navigateUp()

    private fun setToolbar() {
        setSupportActionBar(toolbar)

        supportActionBar!!.title = "Morning"
    }

    private fun setupBottomNavigationBar() {
        val navController = findNavController(this, R.id.mainNavFragment)
        bottom_nav.setupWithNavController(navController)
    }
}
