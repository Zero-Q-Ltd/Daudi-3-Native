package com.zeroq.daudi_3_native.ui

import android.content.Context
import android.os.Bundle
import com.zeroq.daudi_3_native.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.lifecycle.Observer
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.zeroq.daudi_3_native.commons.BaseActivity
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.events.LoadingEvent
import com.zeroq.daudi_3_native.events.ProcessingEvent
import com.zeroq.daudi_3_native.events.QueueingEvent
import com.zeroq.daudi_3_native.ui.main.MainViewModel
import com.zeroq.daudi_3_native.utils.ImageUtil
import com.zeroq.daudi_3_native.viewmodel.EventsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject


class MainActivity : BaseActivity() {


    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var authUI: AuthUI

    @Inject
    lateinit var imageUtil: ImageUtil

    @Inject
    lateinit var eventBus: EventBus


    lateinit var actionBar: ActionBar

    lateinit var mainViewModel: MainViewModel
    lateinit var eventViewModel: EventsViewModel

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = getViewModel(MainViewModel::class.java)
        eventViewModel = getViewModel(EventsViewModel::class.java)

        setToolbar()

        if (savedInstanceState == null)
            setupBottomNavigationBar()

        operations()

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.logout -> {
                loggedOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.mainNavFragment).navigateUp()

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Morning"

        actionBar = supportActionBar as ActionBar


        Glide.with(this)
            .asBitmap()
            .load(firebaseAuth.currentUser?.photoUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val dst = imageUtil.dpToPx(this@MainActivity, 28)
                    val resizedBit: Bitmap = Bitmap.createScaledBitmap(resource, dst, dst, false)

                    val d = RoundedBitmapDrawableFactory.create(resources, resizedBit)
                    setLogo(d)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    setLogo(getDrawable(R.drawable.ic_profile))
                }
            })

    }

    private fun setupBottomNavigationBar() {
        val navController = findNavController(this, R.id.mainNavFragment)
        bottom_nav.setupWithNavController(navController)
    }

    private fun setLogo(d: Drawable) {
        actionBar.setLogo(d)
        actionBar.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)
    }

    private fun operations() {

        eventViewModel.setProcessingEvent(TruckModel())

        mainViewModel.getUser().observe(this, Observer {
            if (it.isSuccessful) {
                var userData = it.data()
                mainViewModel.setDeportId(userData?.config?.depotdata?.depotid)
            } else {
                Timber.e("Sorry an error occurred")
            }
        })

        // get update
        mainViewModel.getTrucks().observe(this, Observer {
            if (it.isSuccessful) {
                var processingL = ArrayList<TruckModel>()
                var loadingL = ArrayList<TruckModel>()
                val queueingL = ArrayList<TruckModel>()

                it.data()?.forEach { truckModel ->
                    when (truckModel.stage) {
                        1 -> {
                            processingL.add(truckModel)
                        }

                        2 -> {
                            queueingL.add(truckModel)
                        }

                        3 -> {
                            loadingL.add(truckModel)
                        }
                    }
                }

                eventBus.postSticky(ProcessingEvent(processingL, null))
                eventBus.postSticky(QueueingEvent(queueingL, null))
                eventBus.postSticky(LoadingEvent(loadingL, null))

            } else {
                eventBus.postSticky(ProcessingEvent(null, it.error()))
                eventBus.postSticky(QueueingEvent(null, it.error()))
                eventBus.postSticky(LoadingEvent(null, it.error()))
            }
        })
    }

    private fun loggedOut() {
        authUI.signOut(this)
    }
}
