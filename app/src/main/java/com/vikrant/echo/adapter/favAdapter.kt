package com.vikrant.echo.adapter

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.vikrant.echo.R
import com.vikrant.echo.Songs
import com.vikrant.echo.fragments.SongPlay

class favAdapter(songDetail: ArrayList<Songs>, _context: Context) :
    RecyclerView.Adapter<favAdapter.innerclass>() {
    var songList1: ArrayList<Songs>? = null
    var mcontext: Context? = null

    init {
        this.songList1 = songDetail
        this.mcontext = _context
    }
    override fun onBindViewHolder(holder: innerclass, p1: Int) {
        var songObject = songList1?.get(p1)
        if (songObject?.SongTitle=="<unknown>")
        {
            holder?.tracktitle?.text = "unknown"
        }
        else
        {
            holder?.tracktitle?.text = songObject?.SongTitle
        }
        if (holder?.trackArtist?.text == "<unknown>")
        {
            holder?.trackArtist?.text = "unknown"
        }
        else
        {
            holder?.trackArtist?.text = songObject?.Artist
        }

        holder?.xcontext?.setOnClickListener({
            var songAdapter = SongPlay()
            var args = Bundle()
            args.putString("Artist", songObject?.Artist)
            args.putInt("SongId", songObject?.SongId?.toInt() as Int)
            args.putString("SongTitle", songObject?.SongTitle)
            args.putString("path", songObject?.songData)
            args.putInt("Position", p1 )
            args.putParcelableArrayList("songData", songList1)
            songAdapter.arguments = args
            (mcontext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.detailFragment, songAdapter)
                .addToBackStack("songPlayFragmet")
                .commit()
        })
          }



    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): innerclass {
        var itemview = LayoutInflater.from(p0?.context)
            .inflate(R.layout.row_mainscreen_adapter, p0, false)
        var returnThis = innerclass(itemview)
        return (returnThis)
    }

    override fun getItemCount(): Int {
        if (songList1 == null) {
            return 0
        } else {
            return (songList1 as ArrayList<Songs>).size
        }
    }


    class innerclass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tracktitle: TextView? = null
        var trackArtist: TextView? = null
        var xcontext: RelativeLayout? = null

        init {
            tracktitle = itemView?.findViewById(R.id.tracktitle) as TextView
            trackArtist = itemView?.findViewById(R.id.trackArtist) as TextView
            xcontext = itemView?.findViewById(R.id.content_row) as RelativeLayout
        }
    }
}