package com.vikrant.echo.fragments


import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*
import com.vikrant.echo.R
import com.vikrant.echo.Songs
import com.vikrant.echo.adapter.favAdapter
import com.vikrant.echo.echoDatabases.Echodata
import com.vikrant.echo.fragments.SongPlay.*
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
class Favourite : Fragment() {
    var myActivity: Activity? = null
    var recycler: RecyclerView? = null
    var songTitle: TextView? = null
    var playpause: ImageButton? = null
    var nowPlayBottombar: RelativeLayout? = null
    var nofav: TextView? = null
    var echodata: Echodata? =null
    var refreshList: ArrayList<Songs>? = null
    var getListFromDataBase: ArrayList<Songs>? = null
   var favadapter:favAdapter?=null
    object favstat {
        var mediaPlayer: MediaPlayer? = null
    }

    var trackPosition: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_favourite, container, false)
        activity?.title = "Favourite"
        recycler = view?.findViewById(R.id.recycleFav)
        songTitle = view?.findViewById(R.id.songTitlefav)
        playpause = view?.findViewById(R.id.playbuttonFAV)
        nowPlayBottombar = view?.findViewById(R.id.hiddenBarFavScreen)
        nofav = view?.findViewById(R.id.nofavoutite)

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        echodata= Echodata(myActivity)
       Display_fav_bysearching()
        bottomBarSetup()

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.main_screen_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        var item=menu?.findItem(R.id.Sort_icon)
        item?.isVisible=false
        var item2=menu?.findItem(R.id.menuBar)
        item2?.isVisible=false
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
            songTitle?.setText(statfavUse.songHelper?.songtitle)
            statfavUse. mediaPlayer?.setOnCompletionListener(MediaPlayer.OnCompletionListener ({

                statfavUse.statisticd.onSongComplete()
                songTitle?.setText(statfavUse.songHelper?.songtitle)
            }))
            if (statfavUse.mediaPlayer?.isPlaying as Boolean) {
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
            favstat.mediaPlayer=statfavUse.mediaPlayer
            var Adaptersong = SongPlay()
            var args = Bundle()
            args.putString("Artist", statfavUse.songHelper?.songArtist)
            args.putInt("SongID", statfavUse.songHelper?.songID?.toInt() as Int)
            args.putString("SongTitle", statfavUse.songHelper?.songtitle)
            args.putString("path", statfavUse.songHelper?.songPath)
            args.putInt("Position", statfavUse.songHelper?.currentPosition as Int)
            args.putParcelableArrayList("songData", statfavUse.fetchSong)
            args.putString("bottombar", "Success")
            Adaptersong.arguments = args
            fragmentManager?.beginTransaction()?.replace(R.id.detailFragment, Adaptersong)
                ?.addToBackStack("SongPlay")
                ?.commit()

        })
        playpause?.setOnClickListener({
            if (statfavUse.mediaPlayer?.isPlaying as Boolean) {
                playpause?.setBackgroundResource(R.drawable.play_icon)

                trackPosition= statfavUse.mediaPlayer?.currentPosition   as Int
                statfavUse.mediaPlayer?.pause()
            } else {
                playpause?.setBackgroundResource(R.drawable.pause_icon)
                statfavUse.mediaPlayer?.seekTo(trackPosition)
                statfavUse.mediaPlayer?.start()
            }
        })
    }

    fun Display_fav_bysearching() {
        if ((echodata?.checkSize() as Int) > 0) {
            getListFromDataBase = echodata?.quarydbList()
            var fetchsongData = getSongSPhone()


            if (getListFromDataBase != null && fetchsongData != null) {
                Toast.makeText(myActivity,"this is calling",Toast.LENGTH_SHORT).show()
                for (i in 0..(fetchsongData?.size) - 1) {
                    for (j in 0..((getListFromDataBase)?.size as Int) - 1) {
                        if ((getListFromDataBase?.get(j)?.SongId) === (fetchsongData.get(i)?.SongId)) {
                            refreshList?.add(fetchsongData[j])
                            Toast.makeText(myActivity,"refreslist is creating",Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            } else {

            }
            if (refreshList == null) {
                recycler?.visibility = View.INVISIBLE
                nofav?.visibility = View.VISIBLE
            } else {
                 favadapter=favAdapter(refreshList as ArrayList<Songs>,myActivity as Context)
                favadapter?.notifyDataSetChanged()
                recycler?.layoutManager=LinearLayoutManager(myActivity)
                recycler?.itemAnimator=DefaultItemAnimator()
                recycler?.adapter=favadapter
                recycler?.hasFixedSize()


            }

        } else {
            recycler?.visibility = View.INVISIBLE
            nofav?.visibility = View.VISIBLE
        }

    }
}
