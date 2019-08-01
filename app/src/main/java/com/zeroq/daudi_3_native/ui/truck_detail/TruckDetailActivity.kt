package com.zeroq.daudi_3_native.ui.truck_detail

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.os.Vibrator
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseActivity
import com.zeroq.daudi_3_native.data.models.Batches
import com.zeroq.daudi_3_native.data.models.Compartment
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.data.models.UserModel
import com.zeroq.daudi_3_native.ui.printing.PrintingActivity
import com.zeroq.daudi_3_native.utils.ImageUtil
import kotlinx.android.synthetic.main.activity_truck_detail.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import net.glxn.qrgen.android.QRCode
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class TruckDetailActivity : BaseActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var imageUtil: ImageUtil

    lateinit var truckDetailViewModel: TruckDetailViewModel

    private val _fuelTypeList = ArrayList<String>()


    // set data to compartments
    private lateinit var viewComp: List<EditText>

    private lateinit var btnComp: List<AppCompatButton>
    private lateinit var _topInputs: List<EditText>

    private lateinit var _user: UserModel
    private var DepotTruck: TruckModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_truck_detail)


        /**
         * hide keyboad
         * */
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        viewComp = listOf(
            et_c1_qty, et_c2_qty,
            et_c3_qty, et_c4_qty, et_c5_qty, et_c6_qty, et_c7_qty
        )

        btnComp = listOf(
            et_c1_type, et_c2_type,
            et_c3_type, et_c4_type, et_c5_type, et_c6_type, et_c7_type
        )

        _topInputs = listOf(et_driver_name, et_driver_id, et_driver_plate)

        /*
        * set  the viewModel
        * */
        truckDetailViewModel = getViewModel(TruckDetailViewModel::class.java)
        truckDetailViewModel.setTruckId(intent.getStringExtra("TRUCK_ID"))

        truckDetailViewModel.getUser().observe(this, Observer {
            if (it.isSuccessful) {
                _user = it.data()!!
                truckDetailViewModel.setDepotId(_user.config?.depotdata?.depotid!!)
            } else {
                Timber.e(it.error()!!)
            }
        })


        truckDetailViewModel.getTruck().observe(this, Observer {
            if (it.isSuccessful) {
                DepotTruck = it.data()
                initialTruckValues(it.data()!!)
            } else {
                Timber.e(it.error()!!)
            }
        })




        initToolbar()
        topInputs()
        compartimentsButtonOps()
        compartmentsInputs()

    }

    private fun initialTruckValues(truck: TruckModel) {

        _fuelTypeList.clear()
        _fuelTypeList.add("EMPTY")

        if (truck.fuel?.pms?.qty != 0) _fuelTypeList.add("PMS")
        if (truck.fuel?.ago?.qty != 0) _fuelTypeList.add("AGO")
        if (truck.fuel?.ik?.qty != 0) _fuelTypeList.add("IK")

        tv_truck_id.text = truck.truckId!!
        tv_customer_value.text = truck.company?.name
        et_driver_name.setText(truck.drivername)
        et_driver_id.setText(truck.driverid)
        et_driver_plate.setText(truck.numberplate)

        // fuel
        tv_pms.text = "PMS [ " + truck.fuel?.pms?.qty + " ]"
        tv_ago.text = "AGO [ " + truck.fuel?.ago?.qty + " ]"
        tv_ik.text = "IK      [ " + truck.fuel?.ik?.qty + " ]"


        // fuel entries

        tv_pms_entry.text = getBatchName(truck.fuel?.pms!!)
        tv_ago_entry.text = getBatchName(truck.fuel?.ago!!)
        tv_ik_entry.text = getBatchName(truck.fuel?.ik!!)




        truck.compartments?.forEachIndexed { index, compartment ->
            if (compartment.qty != null && compartment.qty != 0) {
                btnComp[index].text = compartment.fueltype?.toUpperCase()
                viewComp[index].setText(compartment.qty!!.toString())
            } else {
                btnComp[index].text = "EMPTY"
                viewComp[index].text = null
                viewComp[index].hint = "Empty Comp"
                viewComp[index].isEnabled = false
            }
        }

        tv_auth_by_value.text = truck.stagedata!!["1"]?.user?.name

        // display name
        tv_confirmed_by_value.text = firebaseAuth.currentUser?.displayName


        /**
         * current data, will change it later to be regenerated before printing
         * */
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm aaa")
        tv_today_date.text = sdf.format(Date()).toUpperCase()

        /**
         * create qr
         * */


        val depotUrl =
            "https://us-central1-emkaybeta.cloudfunctions.net/truckDetail?D=${_user.config?.depotdata?.depotid}&T=${truck.truckId}"

        val dimensions = imageUtil.dpToPx(this, 150)

        val thread = Thread(Runnable {
            val myBitmap = QRCode.from(depotUrl)
                .withSize(dimensions, dimensions)
                .bitmap()

            runOnUiThread {
                qr.setImageBitmap(myBitmap)
            }
        })

        thread.start()

        btnPrint.setOnClickListener {
            validateAndPost()
        }

    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
    }


    private fun topInputs() {


        _topInputs.forEach {
            it.filters = arrayOf<InputFilter>(InputFilter.AllCaps())

            it.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    if (s.isNullOrEmpty()) {
                        it.error = "This field cant be blank"
                    } else {
                        it.error = null
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s.isNullOrEmpty()) {
                        it.error = "This field cant be blank"
                    } else {
                        it.error = null
                    }
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        it.error = "This field cant be blank"
                    } else {
                        it.error = null
                    }
                }
            })
        }
    }


    private fun compartimentsButtonOps() {
        /**
         * make sure the compartment values tally with the given fuel
         * */

        btnComp.forEachIndexed { i, btn ->
            btn.setOnClickListener {
                var index = _fuelTypeList.indexOf(btn.text)
                index++


                if (index > (_fuelTypeList.size - 1)) {
                    index = 0

                    viewComp[i].text = null
                    viewComp[i].hint = "Empty Comp"
                    viewComp[i].error = null

                    /**
                     * disable
                     * */
                    viewComp[i].isEnabled = false
                } else {
                    viewComp[i].hint = "Enter Amount"
                    viewComp[i].isEnabled = true
                }

                btn.text = _fuelTypeList[index]
            }
        }
    }

    private fun compartmentsInputs() {

        viewComp.forEach {
            it.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (it.isEnabled && (s.isNullOrEmpty() || s.toString().toInt() < 500)) {
                        it.error = "Should contain more than 500L"
                    } else {
                        if (it.isEnabled && (s.isNullOrEmpty() || s.toString().toInt() > 30000)) {
                            it.error = "Can't carry more than 30000L"
                        } else {
                            it.error = null
                        }
                    }
                }
            })
        }
    }


    private fun getBatchName(batches: Batches): String? {
        val temp = if (batches.batches!!["1"]?.qty != 0) {
            batches.batches!!["1"]?.Name
        } else {
            batches.batches!!["0"]?.Name
        }

        return temp ?: "****************"
    }

    private fun validateAndPost() {
        /**
         * check top inputs
         * */
        var inputErrors = false

        _topInputs.forEach {
            if (it.text.toString().isNullOrEmpty()) {
                inputErrors = true

                it.error = "This field cant be blank"
            }
        }


        /**
         * check if compartments are fine
         *
         * local fuel inputs
         * */

        var pmsLocal = DepotTruck?.fuel?.pms?.qty!!
        var agoLocal = DepotTruck?.fuel?.ago?.qty!!
        var ikLocal = DepotTruck?.fuel?.ik?.qty!!


        // from buttons
        btnComp.forEachIndexed { index, appCompatButton ->
            when (appCompatButton.text) {
                "PMS" ->
                    if (!viewComp[index].text.isNullOrEmpty()) {
                        pmsLocal -= viewComp[index].text.toString().toInt()
                    } else {
                        inputErrors = true
                        viewComp[index].error = "Field can't be empty"
                    }


                "AGO" ->
                    if (!viewComp[index].text.isNullOrEmpty()) {
                        agoLocal -= viewComp[index].text.toString().toInt()
                    } else {
                        inputErrors = true
                        viewComp[index].error = "Field can't be empty"
                    }

                "IK" ->

                    if (!viewComp[index].text.isNullOrEmpty()) {
                        ikLocal -= viewComp[index].text.toString().toInt()
                    } else {
                        inputErrors = true
                        viewComp[index].error = "Field can't be empty"
                    }
            }
        }


        // check if the local fuel is zero and no error
        if (pmsLocal == 0 && agoLocal == 0 && ikLocal == 0 && !inputErrors) {
            pushToServer()
        } else {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                vibrator.run { vibrate(500) } // for 500 ms

                Toast.makeText(this, "Make sure you have no errors", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun pushToServer() {
        val compList = ArrayList<Compartment>()

        btnComp.forEachIndexed { index, appCompatButton ->

            val btnValue: String = appCompatButton.text.toString()

            when (btnValue) {
                "EMPTY" ->
                    compList.add(Compartment(null, null))

                else ->
                    compList.add(
                        Compartment(
                            btnValue.toLowerCase(),
                            viewComp[index].text.toString().toInt()
                        )
                    )
            }
        }

        val driverName = et_driver_name.text.toString().toUpperCase()
        val driverId = et_driver_id.text.toString().toUpperCase()
        val numberPlate = et_driver_plate.text.toString().toUpperCase()

        truckDetailViewModel.updateTruckComAndDriver(
            _user.config?.depotdata?.depotid!!, DepotTruck?.Id!!,
            compList, driverId, driverName, numberPlate
        ).observe(this, Observer {

            if (it.isSuccessful) {
                // create an image to print
                cleanPageForPrinting()
            } else {
                Snackbar.make(layout_constraint, "An error occurred", Snackbar.LENGTH_SHORT).show()
                Timber.e(it.error()!!)
            }
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
    }


    private fun cleanPageForPrinting() {
        hideButton(false)
        disableViews(layout_constraint)

        /**
         * Take screenshot now
         * */

        if (takeandSaveScreenShot()) {
            hideButton(true)
            PrintingActivity.startPrintingActivity(
                this,
                _user.config?.depotdata?.depotid!!, DepotTruck?.Id!!
            )
        } else {
            /**
             * An error occured
             * */
            Toast.makeText(this, "Sorry an error occurred", Toast.LENGTH_SHORT).show()
            hideButton(true)
        }

    }

    private fun disableViews(layout: ViewGroup) {
        layout.isEnabled = false

        for (i in 0 until layout.childCount) {
            var child: View = layout.getChildAt(i)

            if (child is ViewGroup) {
                disableViews(child)
            } else {
                if (child is EditText || child is AppCompatButton) {
                    child.isEnabled = false
                }
            }
        }
    }


    private fun hideButton(visible: Boolean) {
        if (visible) {
            btnPrint.visibility = View.VISIBLE
        } else {
            btnPrint.visibility = View.GONE
        }
    }

    private fun takeandSaveScreenShot(): Boolean {
        val u = content_scroll as View
        val z = content_scroll

        val totalHeight = z.getChildAt(0).height
        val totalWidth = z.getChildAt(0).width

        val newHeight = totalHeight * 537 / totalWidth

        val b = getBitmapFromView(u, totalHeight, totalWidth)
        return savescreenshot(b, 537, newHeight)
    }

    private fun getBitmapFromView(view: View, totalHeight: Int, totalWidth: Int): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background

        if (bgDrawable != null)
            bgDrawable.draw(canvas)
        else
            canvas.drawColor(Color.WHITE)

        view.draw(canvas)
        return returnedBitmap
    }

    private fun savescreenshot(bm: Bitmap, newWidth: Int, newHeight: Int): Boolean {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        bm.recycle()

        val file_path = Environment.getExternalStorageDirectory().absolutePath + "/Emkaynow"
        val dir = File(file_path)


        if (!dir.exists()) {
            dir.mkdirs()
        }
        val image = File(dir, "0.png")
        var out: FileOutputStream? = null

        try {
            out = FileOutputStream(image)
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // bmp is your Bitmap instance
            return true
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

}
