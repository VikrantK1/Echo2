package com.vikrant.echo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.vikrant.echo.MainActivity
import com.vikrant.echo.R
import com.vikrant.echo.fragments.AboutUs
import com.vikrant.echo.fragments.Favourite
import com.vikrant.echo.fragments.MainScreenFragment
import com.vikrant.echo.fragments.Setting

class nav_Adapter(_contentList: ArrayList<String>, _contentImage: IntArray, _context: Context) :
    RecyclerView.Adapter<nav_Adapter.innerClass>() {
    var contentlist: ArrayList<String>? = null
    var contentImage: IntArray? = null
    var mcontext: Context? = null

    init {
        this.contentlist = _contentList
        this.contentImage = _contentImage
        this.mcontext = _context
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): innerClass {

        var itemview = LayoutInflater.from(p0?.context)
            .inflate(R.layout.row_nav_drawer, p0, false)
        var returnThis = innerClass(itemview)
        return returnThis
    }

    override fun getItemCount(): Int {
        return (contentlist as ArrayList<String>).size
    }

    override fun onBindViewHolder(holder: innerClass, p1: Int) {
        holder?.iconImage?.setBackgroundResource(contentImage?.get(p1) as Int)
        holder?.text?.setText(contentlist?.get(p1))
        holder?.contextView?.setOnClickListener({
            if (p1 == 0) {
                var mainScreenFragment = MainScreenFragment()
                (mcontext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.detailFragment, mainScreenFragment)
                    .commit()
            } else if (p1 == 1) {
                var favourite = Favourite()
                (mcontext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.detailFragment, favourite)
                    .commit()
            } else if (p1 == 2) {
                var setting = Setting()
                (mcontext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.detailFragment, setting)
                    .commit()
            } else if (p1 == 3) {
                var aboutUs = AboutUs()
                (mcontext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.detailFragment, aboutUs)
                    .commit()
            }
            MainActivity.Statistis.drawer?.closeDrawers()
        })

    }

    class innerClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iconImage: ImageView? = null
        var text: TextView? = null
        var contextView: RelativeLayout? = null

        init {
            iconImage = itemView?.findViewById(R.id.nav_icon)
            text = itemView?.findViewById(R.id.nav_title)
            contextView = itemView?.findViewById(R.id.item_content_holder)
        }

    }
}