package com.vikrant.echo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.widget.Toast

class splashScreen : AppCompatActivity() {
    var permissisonString = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,

        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.MODIFY_AUDIO_SETTINGS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        if (!haspermit(this@splashScreen, *permissisonString)) {
            ActivityCompat.requestPermissions(this@splashScreen, permissisonString, 132)
        } else {
            grant()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            132 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                ) {
                    grant()
                } else {
                    Toast.makeText(this@splashScreen, "Please Give All Permission", Toast.LENGTH_SHORT).show()
                    this.finish()
                    return
                }


            }
            else -> {
                Toast.makeText(this@splashScreen, "Something is wrong", Toast.LENGTH_SHORT).show()
                this.finish()
                return
            }

        }
    }

    fun grant() {
        Handler().postDelayed({
            var startAct = Intent(this@splashScreen, MainActivity::class.java)
            startActivity(startAct)
        }, 1000)
    }

    fun haspermit(context: Context, vararg permits: String): Boolean {
        var allPermission: Boolean = true
        for (permit in permits) {
            var res = context.checkCallingOrSelfPermission(permit)
            if (res != PackageManager.PERMISSION_GRANTED) {
                allPermission = false
            }

        }
        return allPermission

    }

}
