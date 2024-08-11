package com.example.securedatasavedemo

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

class DetectDeviceActivity : AppCompatActivity() {

    var tvMessage : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_detect_device)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvMessage = findViewById<TextView>(R.id.tvMessage)

        detectEmulatorDevice()

        detectRootDevice()

    }

    private fun detectEmulatorDevice() {
        //tvMessage?.text = "ki hoyechhe"
        /*if (BuildConfig.BUILD_TYPE.equals("debug", true)) {
            return
        }*/

        val result = (Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                && Build.FINGERPRINT.endsWith(":user/release-keys")
                && Build.MANUFACTURER == "Google" && Build.PRODUCT.startsWith("sdk_gphone") && Build.BRAND == "google"
                && Build.MODEL.startsWith("sdk_gphone"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HOST == "Build2" //MSI App Player
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT == "google_sdk"
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator")

        if (result) {
            finish()
        }
    }
    private fun isDeviceRooted(): Boolean {
        val superuserApk = File("/system/app/Supperuser.apk")
        val suBinary = File("/system/bin/su")

        return superuserApk.exists() || suBinary.exists()
    }

    private fun detectRootDevice() {
        //tvMessage?.text = "${tvMessage?.text.toString()}, ki hoise bujtesina "

        val isRoot = isDeviceRooted()

        Log.d("device","$isRoot")

        var isRooted = false
        val packageName = "stericson.busybox"
        var process: Process? = null

        val pm = this.packageManager

        try{
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
                isRooted = true
            if(!isRooted){
                process = Runtime.getRuntime().exec("su")
                isRooted = true
            }

        }catch (ex:Exception){
            ex.printStackTrace()
        }
        finally {
            if(process!=null){
                try {
                    process.destroy()
                }catch (ex:Exception){ex.printStackTrace()}
            }
        }

    }


}