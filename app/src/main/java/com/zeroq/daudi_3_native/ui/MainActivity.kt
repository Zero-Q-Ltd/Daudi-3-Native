package com.zeroq.daudi_3_native.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.enums.Display
import com.github.javiersantos.appupdater.enums.Duration
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.broadcasts.TruckExpireBroadCast
import com.zeroq.daudi_3_native.commons.BaseActivity
import com.zeroq.daudi_3_native.data.models.DepotModel
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.events.LoadingEvent
import com.zeroq.daudi_3_native.events.ProcessingEvent
import com.zeroq.daudi_3_native.events.QueueingEvent
import com.zeroq.daudi_3_native.ui.average_prices.AveragePriceActivity
import com.zeroq.daudi_3_native.ui.dialogs.ProfileDialogFragment
import com.zeroq.daudi_3_native.ui.main.MainViewModel
import com.zeroq.daudi_3_native.utils.ImageUtil
import com.zeroq.daudi_3_native.utils.TruckNotification
import com.zeroq.daudi_3_native.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class MainActivity : BaseActivity() {


    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var authUI: AuthUI

    @Inject
    lateinit var imageUtil: ImageUtil

    @Inject
    lateinit var eventBus: EventBus

    @Inject
    lateinit var truckNotification: TruckNotification

    @Inject
    lateinit var utils: Utils


    private lateinit var actionBar: ActionBar

    private lateinit var mainViewModel: MainViewModel

    private var depot: DepotModel? = null

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = getViewModel(MainViewModel::class.java)

        setToolbar()


        if (savedInstanceState == null)
            setupBottomNavigationBar()

        checkAppUpdate()
        operations()

        // set token to server
        postTokenToServer()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
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

            android.R.id.home -> {
                if (depot != null) {
                    val dialog = ProfileDialogFragment(
                        firebaseAuth.currentUser!!,
                        depot!!
                    )
                    dialog.show(supportFragmentManager, "PROFILE")
                } else {
                    toast("Check if the depot is set")
                }
            }

            R.id.depo_average -> {
                startActivity(Intent(this, AveragePriceActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.mainNavFragment).navigateUp()

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Emkay"


        actionBar = supportActionBar as ActionBar

        // default, to avoid, funny animation
        setLogo(getDrawable(R.drawable.ic_profile)!!)


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
                    setLogo(getDrawable(R.drawable.ic_profile)!!)
                }
            })
    }

    private fun setupBottomNavigationBar() {
        val navController = findNavController(this, R.id.mainNavFragment)
        bottom_nav.setupWithNavController(navController)
    }

    private fun setLogo(d: Drawable) {
//        actionBar.setLogo(d)
        actionBar.setHomeAsUpIndicator(d)
//        actionBar.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    private fun operations() {

        mainViewModel.getUser().observe(this, Observer {
            if (it.isSuccessful) {
                val userData = it.data()
                mainViewModel.setDeportId(userData?.config?.depotid.toString())
            } else {
                Timber.e(it.error())
            }
        })

        mainViewModel.getDepot().observe(this, Observer {
            if (it.isSuccessful) {
                depot = it.data()

                depot?.Name?.let { name ->
                    supportActionBar?.subtitle = name
                }
            } else {
                depot = null
                Timber.e(it.error())
            }
        })

        // get update
        mainViewModel.getTrucks().observe(this, Observer {
            if (it.isSuccessful) {
                val processingL = ArrayList<TruckModel>()
                val loadingL = ArrayList<TruckModel>()
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

                addReminder(it.data()!!)

                val sortedProcessing =
                    processingL.sortedBy { truck ->
                        sortStage(truck, "1")
                    }


                val sortedQueueing =
                    queueingL.sortedBy { truck ->
                        sortStage(truck, "2")
                    }

                val sortedLoading =
                    loadingL.sortedBy { truck ->
                        sortStage(truck, "3")
                    }


                eventBus.postSticky(ProcessingEvent(sortedProcessing, null))
                eventBus.postSticky(QueueingEvent(sortedQueueing, null))
                eventBus.postSticky(LoadingEvent(sortedLoading, null))

                /*
                * set the badges on navbar
                * **/
                if (processingL.size > 0) {
                    bottom_nav.getOrCreateBadge(R.id.processing)?.number = processingL.size
                } else {
                    bottom_nav.removeBadge(R.id.processing)
                }

                if (queueingL.size > 0) {
                    bottom_nav.getOrCreateBadge(R.id.queued)?.number = queueingL.size
                } else {
                    bottom_nav.removeBadge(R.id.queued)
                }

                if (loadingL.size > 0) {
                    bottom_nav.getOrCreateBadge(R.id.loading)?.number = loadingL.size
                } else {
                    bottom_nav.removeBadge(R.id.loading)
                }

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

    private fun sortStage(truck1: TruckModel, stage: String): Long {
        val exLength = truck1.stagedata!![stage]?.data?.expiry?.size!!.minus(1)
        return truck1.stagedata!![stage]?.data?.expiry!![exLength].timestamp!!.time
    }


    private fun showNetworkState(show: Boolean, msg: String?) {
        if (show) {
            internet_error.visibility = View.VISIBLE
            state_message.text = msg
        } else {
            internet_error.visibility = View.GONE
        }
    }


    private fun internetEvent() {

        val net = ReactiveNetwork
            .observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it) {
                    showNetworkState(false, null)
                } else {
                    showNetworkState(true, "Please check your internet connection")
                }
            }

        compositeDisposable.add(net)
    }


    private fun addReminder(trucks: List<TruckModel>) {
        // cancel the existing alarms

        trucks.forEach { truck ->

            val requestCode = utils.stripNonDigits(truck.truckId!!)




            if (truck.stage == 4) {
                /**
                 * cancel the alarm that are not interested by it alarm
                 * TODO: cancel when exiting this here is just useless
                 * */

                truckNotification.cancelReminder(
                    this,
                    TruckExpireBroadCast::class.java,
                    requestCode
                )
            }


            val stagePair = when (truck.stage) {
                1 ->
                    Pair("Processing", truck.stagedata?.get("1")?.data?.expiry?.get(0)?.timestamp!!)
                2 ->
                    Pair("Queued", truck.stagedata?.get("2")?.data?.expiry?.get(0)?.timestamp!!)

                3 ->

                    Pair("Loading", truck.stagedata?.get("3")?.data?.expiry?.get(0)?.timestamp!!)
                else ->
                    Pair("Unknow", Date())
            }

            val title = "Truck ${truck.truckId}"
            val content = "In ${stagePair.first} has expired"

            // time difference and then get hours min and sec


            truckNotification.setReminder(
                this,
                TruckExpireBroadCast::class.java,
                stagePair.second,
                title,
                content,
                requestCode
            )
        }
    }

    fun postTokenToServer() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Timber.e(task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                mainViewModel.postToken(token!!).observe(this, Observer {
                    if (!it.isSuccessful) {
                        Timber.e(it.error())
                    }
                })

                Timber.d(token)
            })

    }

    private fun checkAppUpdate() {
        val updater = AppUpdater(this)
            .setUpdateFrom(UpdateFrom.JSON)
            .setDisplay(Display.SNACKBAR)
            .setUpdateJSON("https://www.dropbox.com/s/x8173qba1rsdhi7/update.json?dl=1")
            .setTitleOnUpdateAvailable("Update available")
            .setContentOnUpdateAvailable("You should update.")
            .setDuration(Duration.INDEFINITE)
            .setCancelable(false)

        updater.start()
    }

    override fun onStart() {
        super.onStart()
        internetEvent()
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }
}
