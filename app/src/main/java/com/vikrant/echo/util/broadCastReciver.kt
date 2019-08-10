package com.vikrant.echo.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.vikrant.echo.MainActivity.Statistis.notification
import com.vikrant.echo.R
import com.vikrant.echo.fragments.SongPlay
import com.vikrant.echo.songHelper
import kotlinx.android.synthetic.main.fragment_main_screen.*
import java.lang.Exception

class broadCastReciver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
                if (SongPlay.statfavUse.mediaPlayer?.isPlaying as Boolean) {
                    SongPlay.statfavUse.mediaPlayer?.pause()
                    SongPlay.statfavUse.playButton?.setBackgroundResource(R.drawable.play_icon)
                } else {

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var tm: TelephonyManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        when (tm?.callState) {

            TelephonyManager.CALL_STATE_RINGING -> {
                try {
                    notification?.cancel(1234)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    if (SongPlay.statfavUse.mediaPlayer?.isPlaying as Boolean) {
                        SongPlay.statfavUse.mediaPlayer?.pause()
                        SongPlay.statfavUse.playButton?.setBackgroundResource(R.drawable.play_icon)
                    } else {

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else -> {

            }
        }
    }
}