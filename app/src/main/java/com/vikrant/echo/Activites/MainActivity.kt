package com.vikrant.echo

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.support.v4.widget.DrawerLayout
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.view.Menu
import com.vikrant.echo.MainActivity.Statistis.drawer
import com.vikrant.echo.MainActivity.Statistis.notification
import com.vikrant.echo.MainActivity.Statistis.notificationBuilder
import com.vikrant.echo.adapter.nav_Adapter
import com.vikrant.echo.fragments.MainScreenFragment
import com.vikrant.echo.fragments.SongPlay
import java.lang.Exception
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    var navigationDrawerIconList: ArrayList<String> = arrayListOf()
    var imageIconDrawer: IntArray = intArrayOf(
        R.drawable.navigation_allsongs,
        R.drawable.navigation_favorites,
        R.drawable.navigation_settings,
        R.drawable.navigation_aboutus

    )

    object Statistis {
        var notificationBuilder: Notification? = null
        var notification: NotificationManager? = null
        var drawer: DrawerLayout? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        navigationDrawerIconList.add("All Songs")
        navigationDrawerIconList.add("Favourite")
        navigationDrawerIconList.add("Setting")
        navigationDrawerIconList.add("About US")

        var toggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer?.addDrawerListener(toggle)
        toggle.syncState()
        var mainScreenFragment = MainScreenFragment()
        this.supportFragmentManager.beginTransaction()
            .add(R.id.detailFragment, mainScreenFragment, "MainFragment")
            .commit()
        var adapter2 = nav_Adapter(navigationDrawerIconList, imageIconDrawer, this)
        adapter2.notifyDataSetChanged()
        var recyclerView = findViewById<RecyclerView>(R.id.navRecyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.adapter = adapter2
        recyclerView?.setHasFixedSize(true)
        var intent = Intent(this@MainActivity, MainActivity::class.java)
        var pintent = PendingIntent.getActivity(this@MainActivity, System.currentTimeMillis().toInt(), intent, 0)
        notificationBuilder = Notification.Builder(this)
            .setContentTitle("Song is Playing in BackGround..")
            .setSmallIcon(R.drawable.echo_logo)
            .setContentIntent(pintent)
            .setOngoing(true)
            .setAutoCancel(true)
            .build()
        notification = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStart() {
        super.onStart()
        try {
            notification?.cancel(1234)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            if (SongPlay.statfavUse.mediaPlayer?.isPlaying as Boolean) {
                notification?.notify(1234, notificationBuilder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            notification?.cancel(1234)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}