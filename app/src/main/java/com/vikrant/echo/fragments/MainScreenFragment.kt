package com.vikrant.echo.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.vikrant.echo.R
import com.vikrant.echo.Songs
import com.vikrant.echo.adapter.mainScreenAdapter
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MainScreenFragment : Fragment() {
    var nowPlayBottombar: RelativeLayout? = null
    var playpause: ImageButton? = null
    var songTitle: TextView? = null
    var noSong: RelativeLayout? = null
    var recycleview: RecyclerView? = null
    var visibleLayout: RelativeLayout? = null
    var myActivity: Activity? = null
    var mainScreenAdapter: mainScreenAdapter? = null
    var songDetail: ArrayList<Songs>? = null
    var trackPosition: Int = 0
    object statifide{
        var mediaPlayer:MediaPlayer?=null
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_screen, container, false)
        activity?.title = "All Songs"
        playpause = view?.findViewById(R.id.playbutton)
        songTitle = view?.findViewById(R.id.songTitlemain)
        noSong = view?.findViewById(R.id.noSongsMainScreen)
        recycleview = view?.findViewById(R.id.Mainrecyclerview)
        visibleLayout = view?.findViewById(R.id.visible)
        setHasOptionsMenu(true)
        nowPlayBottombar = view?.findViewById(R.id.hiddenBarMainScreen)
        songDetail = getSongSPhone()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var pref = myActivity?.getSharedPreferences("ActionSort", Context.MODE_PRIVATE)
        var Ascending = pref?.getString("Ascending", "true")
        var actondata = pref?.getString("actondata", "false")
        songDetail = getSongSPhone()
        mainScreenAdapter = mainScreenAdapter(songDetail as ArrayList<Songs>, myActivity as Context)
        var layoutManager = LinearLayoutManager(myActivity)
        recycleview?.layoutManager = layoutManager
        recycleview?.itemAnimator = DefaultItemAnimator()
        recycleview?.adapter = mainScreenAdapter
        if (songDetail == null) {
            visibleLayout?.visibility = View.INVISIBLE
            noSong?.visibility = View.VISIBLE
        }
        if (songDetail != null) {
            if (Ascending!!.equals("true", ignoreCase = true)) {
                Collections.sort(songDetail, Songs.statified.nameComparator)
                mainScreenAdapter?.notifyDataSetChanged()
            } else if (actondata.equals("true", true)) {
                Collections.sort(songDetail, Songs.statified.dateComparator)
                mainScreenAdapter?.notifyDataSetChanged()
            }
        }
        bottomBarSetup()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main_screen_menu, menu)
        return

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val switcher = item?.itemId
        if (switcher == R.id.Ascending) {
            var editor = myActivity?.getSharedPreferences("ActionSort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("Ascending", "true")
            editor?.putString("actiondata", "false")
            if (songDetail != null) {
                Collections.sort(songDetail, Songs.statified.nameComparator)
            } else if (switcher == R.id.actionSort) {
                var pref2 = myActivity?.getSharedPreferences("ActionSort", Context.MODE_PRIVATE)?.edit()
                pref2?.putString("Ascending", "false")
                pref2?.putString("actondata", "true")
                pref2?.apply()
                if (songDetail != null) {
                    Collections.sort(songDetail, Songs.statified.dateComparator)
                }

            }
            mainScreenAdapter?.notifyDataSetChanged()
            return false

        }
        return super.onOptionsItemSelected(item)
    }

    fun getSongSPhone(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCurser = contentResolver?.query(songUri, null, null, null, null)
        if (songCurser != null && songCurser.moveToFirst()) {
            var phoneSongID = songCurser.getColumnIndex(MediaStore.Audio.Media._ID)
            var phoneSongTitle = songCurser.getColumnIndex(MediaStore.Audio.Media.TITLE)
            var phoneSongArtist = songCurser.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            var phoneSongData = songCurser.getColumnIndex(MediaStore.Audio.Media.DATA)
            var phoneSongDateAdded = songCurser.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCurser.moveToNext()) {
                var currentSongID = songCurser.getLong(phoneSongID)
                var currentSongTirtle = songCurser.getString(phoneSongTitle)
                var currentSongArtist = songCurser.getString(phoneSongArtist)
                var currentSongData = songCurser.getString(phoneSongData)
                var currentSongDateAdded = songCurser.getLong(phoneSongDateAdded)

                arrayList.add(
                    Songs(
                        currentSongID, currentSongTirtle,
                        currentSongArtist, currentSongData, currentSongDateAdded
                    )
                )
            }
        }


        return arrayList
    }

    fun bottomBarSetup() {
        try {
            bottombarClickListner()
            songTitle?.setText(SongPlay.statfavUse.songHelper?.songtitle)
            SongPlay.statfavUse.mediaPlayer?.setOnCompletionListener({
                songTitle?.setText(SongPlay.statfavUse.songHelper?.songtitle)
                SongPlay.statfavUse.statisticd.onSongComplete()
            })
            if (SongPlay.statfavUse.mediaPlayer?.isPlaying as Boolean) {
                nowPlayBottombar?.visibility = View.VISIBLE
                playpause?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                nowPlayBottombar?.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun bottombarClickListner() {
        nowPlayBottombar?.setOnClickListener({
          Favourite.favstat.mediaPlayer=SongPlay.statfavUse.mediaPlayer
            var Adaptersong = SongPlay()
            var args = Bundle()
            args.putString("Artist", SongPlay.statfavUse.songHelper?.songArtist)
            args.putInt("SongID", SongPlay.statfavUse.songHelper?.songID?.toInt() as Int)
            args.putString("SongTitle", SongPlay.statfavUse.songHelper?.songtitle)
            args.putString("path", SongPlay.statfavUse.songHelper?.songPath)
            args.putInt("Position", SongPlay.statfavUse.songHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData", SongPlay.statfavUse.fetchSong)
            args.putString("bottombar", "Success")
            Adaptersong.arguments = args
            fragmentManager?.beginTransaction()?.replace(R.id.detailFragment, Adaptersong)
                ?.addToBackStack("SongPlay")
                ?.commit()

        })
        playpause?.setOnClickListener({
            if (SongPlay.statfavUse.mediaPlayer?.isPlaying as Boolean) {
                playpause?.setBackgroundResource(R.drawable.play_icon)
                SongPlay.statfavUse.mediaPlayer?.pause()
            } else {
                playpause?.setBackgroundResource(R.drawable.pause_icon)
                SongPlay.statfavUse.mediaPlayer?.seekTo(trackPosition)
                SongPlay.statfavUse.mediaPlayer?.start()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        playpause?.setBackgroundResource(R.drawable.play_icon)

    }

}
