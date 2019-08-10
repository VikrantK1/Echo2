package com.vikrant.echo.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.*
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.vikrant.echo.MainActivity
import com.vikrant.echo.R
import com.vikrant.echo.Songs
import com.vikrant.echo.echoDatabases.Echodata
import com.vikrant.echo.fragments.SongPlay.statfavUse.StartTime
import com.vikrant.echo.fragments.SongPlay.statfavUse.artist
import com.vikrant.echo.fragments.SongPlay.statfavUse.audioVisulaisation
import com.vikrant.echo.fragments.SongPlay.statfavUse.currentPosition
import com.vikrant.echo.fragments.SongPlay.statfavUse.endTime
import com.vikrant.echo.fragments.SongPlay.statfavUse.fav
import com.vikrant.echo.fragments.SongPlay.statfavUse.favcontent
import com.vikrant.echo.fragments.SongPlay.statfavUse.fetchSong
import com.vikrant.echo.fragments.SongPlay.statfavUse.glView
import com.vikrant.echo.fragments.SongPlay.statfavUse.mSenserManager
import com.vikrant.echo.fragments.SongPlay.statfavUse.mediaPlayer
import com.vikrant.echo.fragments.SongPlay.statfavUse.msenserListner
import com.vikrant.echo.fragments.SongPlay.statfavUse.my_Pref_Service
import com.vikrant.echo.fragments.SongPlay.statfavUse.mysongActivity
import com.vikrant.echo.fragments.SongPlay.statfavUse.playButton
import com.vikrant.echo.fragments.SongPlay.statfavUse.playNext
import com.vikrant.echo.fragments.SongPlay.statfavUse.previouButton
import com.vikrant.echo.fragments.SongPlay.statfavUse.processInformation
import com.vikrant.echo.fragments.SongPlay.statfavUse.repeatButton
import com.vikrant.echo.fragments.SongPlay.statfavUse.seekBar
import com.vikrant.echo.fragments.SongPlay.statfavUse.shuffleButton
import com.vikrant.echo.fragments.SongPlay.statfavUse.statisticd.onSongComplete
import com.vikrant.echo.fragments.SongPlay.statfavUse.title
import com.vikrant.echo.fragments.SongPlay.statfavUse.updateSongtime
import com.vikrant.echo.fragments.SongPlay.statfavUse.updateText
import com.vikrant.echo.songHelper
import kotlinx.android.synthetic.main.fragment_main_screen.*
import kotlinx.android.synthetic.main.fragment_song_play.*
import kotlinx.android.synthetic.main.row_mainscreen_adapter.*
import java.lang.Exception
import java.sql.Time
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.random.nextInt


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SongPlay : Fragment() {
    object statfavUse {
        var mysongActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var StartTime: TextView? = null
        var endTime: TextView? = null
        var playButton: ImageButton? = null
        var previouButton: ImageButton? = null
        var nextButton: ImageButton? = null
        var repeatButton: ImageButton? = null
        var shuffleButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var title: TextView? = null
        var artist: TextView? = null
        var songHelper: songHelper? = null
        var currentPosition: Int? = 0
        var fetchSong: ArrayList<Songs>? = null
        var audioVisulaisation: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var fav: ImageButton? = null
        var favcontent: Echodata? = null
        var mSenserManager: SensorManager? = null
        var msenserListner: SensorEventListener? = null

        var my_Pref_Service = "ShakeFeature"
        var updateSongtime = object : Runnable {
            override fun run() {
                val getcurrent = mediaPlayer?.currentPosition
                StartTime?.setText(
                    String.format(
                        "%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getcurrent?.toLong() as Long) -
                                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long))
                    )
                )
                seekBar?.setProgress(getcurrent as Int)
                Handler().postDelayed(this, 1000)
            }

        }

        object statisticd {
            var my_pref_Shuffle = "Shuffle feature"
            var my_pref_repeat = "Repeat feature"

            fun onSongComplete() {
                if (songHelper?.isShuffle as Boolean) {
                    playNext("PlayNextLikeNormalShuffle")
                    songHelper?.isPlay = true
                } else {
                    if (songHelper?.isRepeat as Boolean) {
                        songHelper?.isPlay = true
                        var nextSongs = currentPosition?.let { fetchSong?.get(it) }
                        songHelper?.currentPosition = currentPosition
                        songHelper?.songPath = nextSongs?.songData
                        songHelper?.songtitle = nextSongs?.SongTitle
                        songHelper?.songArtist = nextSongs?.Artist
                        songHelper?.songID = nextSongs?.SongId as Long
                        updateText(
                            songHelper?.songtitle as String,
                            songHelper?.songArtist as String
                        )
                        mediaPlayer?.reset()
                        try {
                            mediaPlayer?.setDataSource(mysongActivity, Uri.parse(songHelper?.songPath))
                            mediaPlayer?.prepare()
                            mediaPlayer?.start()
                            processInformation(mediaPlayer as MediaPlayer)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    } else {
                        playNext("PlayNextNormal")
                        songHelper?.isPlay = true
                    }
                }
                if (favcontent?.checkIdExist(songHelper?.songID?.toInt() as Int) as Boolean) {
                    fav?.setImageDrawable(ContextCompat.getDrawable(mysongActivity as Context, R.drawable.favorite_on))

                } else {
                    fav?.setImageDrawable(ContextCompat.getDrawable(mysongActivity as Context, R.drawable.favorite_off))

                }
            }
        }

        fun playNext(check: String) {
            if (check.equals("PlayNextNormal", true)) {
                currentPosition = currentPosition!! + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                var radomobj = java.util.Random()
                var randomPosition = radomobj.nextInt(fetchSong?.size?.plus(1) as Int)
                currentPosition = randomPosition
            }
            if (currentPosition == fetchSong?.size) {
                currentPosition = 0
            }
            songHelper?.isRepeat = false
            var nextSong = currentPosition?.let { fetchSong?.get(it) }
            songHelper?.songtitle = nextSong?.SongTitle
            songHelper?.songPath = nextSong?.songData
            songHelper?.songID = nextSong!!.SongId
            updateText(songHelper?.songtitle as String, songHelper?.songArtist as String)
            mediaPlayer?.reset()
            try {
                mediaPlayer?.setDataSource(mysongActivity, Uri.parse(songHelper?.songPath))
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                processInformation(mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (favcontent?.checkIdExist(songHelper?.songID?.toInt() as Int) as Boolean) {
                fav?.setImageDrawable(ContextCompat.getDrawable(mysongActivity as Context, R.drawable.favorite_on))

            } else {
                fav?.setImageDrawable(ContextCompat.getDrawable(mysongActivity as Context, R.drawable.favorite_off))

            }


        }

        fun updateText(SongTitle: String, SongArtist: String) {
            if (SongTitle.equals("<unknown>", true)) {
                title?.setText("Unknown")

            } else {
                title?.setText(SongTitle)
            }
            if (SongArtist.equals("<unknown>", true)) {
                artist?.setText("unknown")
            } else {

                artist?.setText(SongArtist)
            }
        }

        fun processInformation(mediaPlayer: MediaPlayer) {
            try {
                val finalTime = mediaPlayer.duration
                val startTime = mediaPlayer.currentPosition
                seekBar?.max = finalTime
                StartTime?.setText(
                    String.format(
                        "%d: %d",
                        TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                        TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                startTime?.toLong()
                            )
                        )
                    )
                )
                endTime?.setText(
                    String.format(
                        "%d :%d",
                        TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                finalTime?.toLong()
                            )
                        )
                    )
                )

                seekBar?.setProgress(startTime)


            } catch (e: Exception) {
                e.printStackTrace()
            }

            Handler().postDelayed(updateSongtime, 1000)

        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_song_play, container, false)
        activity?.title = "Now Playing"
        StartTime = view?.findViewById(R.id.starttime)
        endTime = view?.findViewById(R.id.endtime)
        playButton = view?.findViewById(R.id.playbutton)
        previouButton = view?.findViewById(R.id.previousbutton)
        statfavUse.nextButton = view?.findViewById(R.id.nextButton)
        repeatButton = view?.findViewById(R.id.RepeatButton)
        fav = view?.findViewById(R.id.favbutton)
        fav?.alpha = 0.8f
        setHasOptionsMenu(true)
        shuffleButton = view?.findViewById(R.id.Shuffle)
        seekBar = view?.findViewById(R.id.seekbar)
        title = view?.findViewById(R.id.songTitle)
        artist = view?.findViewById(R.id.artistPlay)
        glView = view?.findViewById(R.id.visualizer_view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioVisulaisation = glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mysongActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        mysongActivity = activity
    }

    override fun onPause() {
        super.onPause()
        audioVisulaisation?.onPause()
        mSenserManager?.unregisterListener(msenserListner)
    }

    override fun onResume() {
        super.onResume()
        audioVisulaisation?.onResume()
        mSenserManager?.registerListener(
            msenserListner, mSenserManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSenserManager = mysongActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccleration = 0.0f
        mAcclerationCurrent = SensorManager.GRAVITY_EARTH
        macclerationLast = SensorManager.GRAVITY_EARTH
        bindsackListner()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_bar, menu)

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        var itembar = menu?.findItem(R.id.menuBar)
        itembar?.isVisible = true
        var itembar2 = menu?.findItem(R.id.actionSort)
        itembar2?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menuBar -> {
                mysongActivity?.onBackPressed()
                return false
            }

        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var path: String? = null
        var songTitle: String? = null
        var songArtist: String? = null

        favcontent = Echodata(mysongActivity)

        var songid: Long = 0
        statfavUse.songHelper = songHelper()
        statfavUse.songHelper?.isPlay = true
        statfavUse.songHelper?.isRepeat = false
        statfavUse.songHelper?.isShuffle = false
        try {
            path = arguments?.getString("path")
            songTitle = arguments?.getString("SongTitle")
            songArtist = arguments?.getString("Artist")
            songid = arguments?.getInt("SongId")!!.toLong()
            currentPosition = arguments?.getInt("Position")
            fetchSong = arguments?.getParcelableArrayList("songData")
            statfavUse.songHelper?.songPath = path
            statfavUse.songHelper?.songtitle = songTitle
            statfavUse.songHelper?.songArtist = songArtist
            statfavUse.songHelper?.songID = songid
            statfavUse.songHelper?.currentPosition = currentPosition
            updateText(statfavUse.songHelper?.songtitle as String, statfavUse.songHelper?.songArtist as String)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var fromfavBottomBar = arguments?.get("bottombar") as? String
        if (fromfavBottomBar != null) {
            mediaPlayer = Favourite.favstat.mediaPlayer
        } else {

            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                mediaPlayer?.setDataSource(mysongActivity, Uri.parse(path))
                mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaPlayer?.start()
        }
        audioVisulaisation?.linkTo(0)
        processInformation(mediaPlayer as MediaPlayer)
        if (statfavUse.songHelper?.isPlay as Boolean) {
            playButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playButton?.setBackgroundResource(R.drawable.play_icon)
        }

        clickListner()

        audioVisulaisation?.linkTo(mediaPlayer)
        var shufflePref =
            mysongActivity?.getSharedPreferences(statfavUse.statisticd?.my_pref_Shuffle, Context.MODE_PRIVATE)
        var loopPref = mysongActivity?.getSharedPreferences(statfavUse.statisticd?.my_pref_repeat, Context.MODE_PRIVATE)
        var isShuffleAllow = shufflePref?.getBoolean("feature", false)

        if (isShuffleAllow as Boolean) {
            statfavUse.songHelper?.isShuffle = true
            statfavUse.songHelper?.isRepeat = false
            shuffleButton?.setBackgroundResource(R.drawable.shuffle_icon)
            repeatButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            statfavUse.songHelper?.isShuffle = false
            shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)

        }
        var isRepeatAllow = shufflePref?.getBoolean("feature", false)
        if (isRepeatAllow as Boolean) {
            statfavUse.songHelper?.isRepeat = true
            statfavUse.songHelper?.isShuffle = false
            shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            repeatButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            statfavUse.songHelper?.isRepeat = false
            repeatButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        if (favcontent?.checkIdExist(statfavUse.songHelper?.songID?.toInt() as Int) as Boolean) {
            fav?.setImageDrawable(ContextCompat.getDrawable(mysongActivity as Context, R.drawable.favorite_on))

        } else {
            fav?.setImageDrawable(ContextCompat.getDrawable(mysongActivity as Context, R.drawable.favorite_off))

        }
        mediaPlayer?.setOnCompletionListener(MediaPlayer.OnCompletionListener({
            Toast.makeText(mysongActivity, "Song is completed", Toast.LENGTH_SHORT).show()
            onSongComplete()
        }))
    }


    fun clickListner() {
        fav?.setOnClickListener({

            if (favcontent?.checkIdExist(statfavUse.songHelper?.songID?.toInt() as Int) as Boolean) {
                fav?.setImageDrawable(ContextCompat.getDrawable(mysongActivity as Context, R.drawable.favorite_off))
                favcontent?.delete(statfavUse.songHelper?.songID?.toInt() as Int)
                Toast.makeText(mysongActivity, "delete from Favourite", Toast.LENGTH_SHORT).show()
            } else {
                fav?.setImageDrawable(ContextCompat.getDrawable(mysongActivity as Context, R.drawable.favorite_on))
                favcontent?.storeAsFav(
                    statfavUse.songHelper?.songID?.toInt(),
                    statfavUse.songHelper?.songtitle,
                    statfavUse.songHelper?.songArtist,
                    statfavUse.songHelper?.songPath
                )
                Toast.makeText(mysongActivity, "add to Favourite", Toast.LENGTH_SHORT).show()
            }


        })
        playButton?.setOnClickListener({
            if (mediaPlayer?.isPlaying as Boolean) {
                mediaPlayer?.pause()
                statfavUse.songHelper?.isPlay = false
                playButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                mediaPlayer?.start()
                statfavUse.songHelper?.isPlay = true
                playButton?.setBackgroundResource(R.drawable.pause_icon)
            }

        })
        nextButton?.setOnClickListener({
            statfavUse.songHelper?.isPlay = true
            playButton?.setBackgroundResource(
                R.drawable.pause_icon
            )
            if (statfavUse.songHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
            } else {
                playNext("PlayNextNormal")
            }
        })
        previouButton?.setOnClickListener({
            statfavUse.songHelper?.isPlay = true
            if (statfavUse.songHelper?.isRepeat as Boolean) {
                repeatButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        })
        repeatButton?.setOnClickListener({
            var editorShuffle =
                mysongActivity?.getSharedPreferences(statfavUse.statisticd.my_pref_Shuffle, Context.MODE_PRIVATE)
                    ?.edit()
            var editorLoop =
                mysongActivity?.getSharedPreferences(statfavUse.statisticd.my_pref_repeat, Context.MODE_PRIVATE)?.edit()
            if (statfavUse.songHelper?.isRepeat as Boolean) {
                statfavUse.songHelper?.isRepeat = false
                repeatButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()

            } else {
                statfavUse.songHelper?.isRepeat = true
                statfavUse.songHelper?.isShuffle = false
                repeatButton?.setBackgroundResource(R.drawable.loop_icon)
                shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            }
        })
        shuffleButton?.setOnClickListener({
            var edeater_shuffle =
                mysongActivity?.getSharedPreferences(statfavUse.statisticd.my_pref_Shuffle, Context.MODE_PRIVATE)
                    ?.edit()
            var editerRepeat =
                mysongActivity?.getSharedPreferences(statfavUse.statisticd.my_pref_repeat, Context.MODE_PRIVATE)?.edit()
            if (statfavUse.songHelper?.isShuffle as Boolean) {
                statfavUse.songHelper?.isShuffle = false
                shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                edeater_shuffle?.putBoolean("feature", false)
                edeater_shuffle?.apply()
            } else {
                statfavUse.songHelper?.isShuffle = true
                statfavUse.songHelper?.isRepeat = false
                repeatButton?.setBackgroundResource(R.drawable.loop_white_icon)
                shuffleButton?.setBackgroundResource(R.drawable.shuffle_icon)
                edeater_shuffle?.putBoolean("feature", true)
                edeater_shuffle?.apply()
                editerRepeat?.putBoolean("feature", false)
                editerRepeat?.apply()
            }
        })
    }

    fun playPrevious() {
        currentPosition = currentPosition?.minus(1)
        if (currentPosition == -1) {
            currentPosition = 0
        }

        if (statfavUse.songHelper?.isPlay as Boolean) {
            playButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playButton?.setBackgroundResource(R.drawable.play_icon)
        }

        statfavUse.songHelper?.isRepeat = false
        var nextSong = currentPosition?.let { fetchSong?.get(it) }
        statfavUse.songHelper?.songtitle = nextSong?.SongTitle
        statfavUse.songHelper?.songPath = nextSong?.songData
        statfavUse.songHelper?.songID = nextSong?.SongId as Long
        updateText(statfavUse.songHelper?.songtitle as String, statfavUse.songHelper?.songArtist as String)
        mediaPlayer?.reset()
        try {
            mediaPlayer?.setDataSource(mysongActivity, Uri.parse(statfavUse.songHelper?.songPath))
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            processInformation(mediaPlayer as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (favcontent?.checkIdExist(statfavUse.songHelper?.songID?.toInt() as Int) as Boolean) {
            fav?.setImageDrawable(ContextCompat.getDrawable(mysongActivity as Context, R.drawable.favorite_on))

        } else {
            fav?.setImageDrawable(ContextCompat.getDrawable(mysongActivity as Context, R.drawable.favorite_off))

        }

    }

    var mAccleration: Float = 0f
    var mAcclerationCurrent: Float = 0f
    var macclerationLast: Float = 0f
    fun bindsackListner() {
        msenserListner = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent) {
                var x = event.values[0]
                var y = event.values[1]
                var z = event.values[2]
                macclerationLast = mAcclerationCurrent
                mAcclerationCurrent = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                var delta = mAcclerationCurrent - macclerationLast
                mAccleration = mAccleration * 0.9f + delta
                if (mAccleration > 12) {
                    val pref = mysongActivity?.getSharedPreferences(my_Pref_Service, Context.MODE_PRIVATE)
                    var isAllowed = pref?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        playNext("PlayNextNormal")
                    }

                }

            }

        }


    }


}
