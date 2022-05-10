package com.adl.aplikasitim

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.adl.aplikasitim.Utils.toMusicTime
import com.adl.aplikasitim.audiovolume.AudioVolumeObserver
import com.adl.aplikasitim.audiovolume.OnAudioVolumeChangedListener
import com.adl.aplikasitim.databinding.PlaymusicBinding
import com.adl.aplikasitim.models.Music
import com.adl.aplikasitim.pref.ProgressPrefs
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class PlayMusicActivity : DataBindingActivity(),OnAudioVolumeChangedListener {

    companion object{
        const val KEY_SONGS = "key_songs"
        const val KEY_POSITION = "key_position"
    }
    private lateinit var playSongBinding: PlaymusicBinding
    private var position = 0
    private var songs: MutableList<Music>? = null
    private var musicPlayer: MediaPlayer? = null
    private lateinit var handler: Handler
    private var currentUser: FirebaseUser? = null
    private lateinit var databaseMyTracks: DatabaseReference
    private var isMyTrack = false

    private lateinit var seekBar: SeekBar
    private lateinit var imgMusicNote: ImageView

    private var audioManager: AudioManager? = null
    private var sBarProgress = 0
    private var audioVolumeObserver: AudioVolumeObserver? = null
    private var musicNoteDrawable: Drawable? = null
    private var musicOffDrawable: Drawable? = null
    private val preferences = ProgressPrefs()



    private val eventListenerCheckMyTracks = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val song = songs?.get(position)
            if (snapshot.value != null){
                for (snap in snapshot.children){
                    if (snap.key == song?.keySong){
                        isMyTrack = true
                        checkLikeButton()
                        break
                    }else{
                        isMyTrack = false
                        checkLikeButton()
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("PlaySongActivity", "[onCancelled] ${error.message}")
        }
    }

    private val eventListenerAddMyTrack = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val data = snapshot.value
            if (data != null){
                val song = songs?.get(position)
                databaseMyTracks
                    .child(currentUser?.uid.toString())
                    .child("my_tracks")
                    .child(song?.keySong.toString())
                    .setValue(song)

                isMyTrack = true
                checkLikeButton()
            }else{
                val song = songs?.get(position)
                databaseMyTracks
                    .child(currentUser?.uid.toString())
                    .child("my_tracks")
                    .child(song?.keySong.toString())
                    .setValue(song)

                isMyTrack = true
                checkLikeButton()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("PlaySongActivity", "[onCancelled] ${error.message}")
        }
    }

    private fun checkLikeButton() {
        if (isMyTrack){
            playSongBinding.btnFav.setImageResource(R.drawable.ic_baseline_favorite_24)
            playSongBinding.btnFav.setColorFilter(ContextCompat.getColor(this, android.R.color.white))
        }else{
            playSongBinding.btnFav.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            playSongBinding.btnFav.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playSongBinding = PlaymusicBinding.inflate(layoutInflater)
        setContentView(playSongBinding.root)
        volumeControlStream = AudioManager.STREAM_MUSIC

        setViews()

        init()
        getData()
        onClick()
    }

    private fun setViews() {
        seekBar = playSongBinding.seekBar
        imgMusicNote = playSongBinding.imgMusicNote
        setSeekBar()
        setDrawables()
        setViewListeners()
    }

    private fun setSeekBar() {
        seekBar.secondaryProgress = 100
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        seekBar.max = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val volume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        seekBar.progress = volume
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sBarProgress = progress
                handleIcon(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, sBarProgress, AudioManager.FLAG_PLAY_SOUND)
            }
        })
    }

    private fun setViewListeners() {
        imgMusicNote.setOnClickListener {
            if (imgMusicNote.drawable == musicNoteDrawable) {
                preferences.seekBarProgress = sBarProgress
                seekBar.progress = 0
                sBarProgress = 0
                audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, sBarProgress, AudioManager.FLAG_PLAY_SOUND)
                imgMusicNote.setImageDrawable(musicOffDrawable)
            } else {
                sBarProgress = preferences.seekBarProgress
                audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, sBarProgress, AudioManager.FLAG_PLAY_SOUND)
                seekBar.progress = sBarProgress
                imgMusicNote.setImageDrawable(musicNoteDrawable)
            }
        }
    }


    private fun setDrawables() {
        musicNoteDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_music_note_white_24)
        musicOffDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_music_off_white_24)
    }

    override fun onPause() {
        super.onPause()
        musicPlayer?.pause()
        audioVolumeObserver?.unregister()
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayer?.stop()
        musicPlayer = null
    }

    override fun onResume() {
        super.onResume()
        if (musicPlayer != null && !musicPlayer?.isPlaying!!){
            musicPlayer?.start()
        }
        if (audioManager != null) {
            val currentVol = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
            seekBar.progress = currentVol
            handleIcon(currentVol)
        }
        if (audioVolumeObserver == null) {
            audioVolumeObserver = AudioVolumeObserver(this)
        }
        audioVolumeObserver?.register(AudioManager.STREAM_MUSIC, this)
    }

    private fun handleIcon(currentVol: Int) {
        if (currentVol == 0) {
        imgMusicNote.setImageDrawable(musicOffDrawable)
    } else {
        imgMusicNote.setImageDrawable(musicNoteDrawable)
    }
    }

    private fun getData() {
        //Get data from intent
        if (intent != null){
            songs = intent.getParcelableArrayListExtra(KEY_SONGS)
            position = intent.getIntExtra(KEY_POSITION, 0)
            songs?.let {
                val song = it[position]
                initView(song)
            }
        }

        //Get data My Tracks
        checkMyTrack()
    }

    private fun checkMyTrack() {
        databaseMyTracks
            .child(currentUser?.uid.toString())
            .child("my_tracks")
            .addListenerForSingleValueEvent(eventListenerCheckMyTracks)
    }

    private fun initView(song: Music) {
        checkButtonSong()
        playSongBinding.tvNameSong.text = song.nameSong
        playSongBinding.tvArtistName.text = song.artistSong
        Glide.with(this)
            .load(song.imageSong)
            .placeholder(android.R.color.darker_gray)
            .into(playSongBinding.imagePlaySong)

        playSong(song)
    }

    private fun checkButtonSong() {
        if (position == 0){
            playSongBinding.btnPrevMusic.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
            playSongBinding.btnPrevMusic.isEnabled = false
        }else if (position > 0 && position < songs?.size?.minus(1)!!){
            playSongBinding.btnPrevMusic.setColorFilter(ContextCompat.getColor(this, android.R.color.white))
            playSongBinding.btnPrevMusic.isEnabled = true

            playSongBinding.btnNextMusic.setColorFilter(ContextCompat.getColor(this, android.R.color.white))
            playSongBinding.btnNextMusic.isEnabled = true
        }else{
            playSongBinding.btnNextMusic.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
            playSongBinding.btnNextMusic.isEnabled = false
        }
    }

    private fun playSong(song: Music) {
        try {
            musicPlayer?.setDataSource(song.uriSong)
            musicPlayer?.setOnPreparedListener{
                it.start()
                playSongBinding.mntEnd.text = it?.duration?.toMusicTime()
                playSongBinding.progressBar4.max = it?.duration!!
                checkMusicButton()
            }

            musicPlayer?.prepareAsync()

            handler.postDelayed(object : Runnable {
                override fun run() {
                    try {
                        playSongBinding.progressBar4.progress = musicPlayer?.currentPosition!!
                        handler.postDelayed(this, 1000)
                    } catch (e: Exception) {
                        Log.e("PlaySongActivity", "[run] ${e.message}")
                    }
                }
            }, 0)

            playSongBinding.progressBar4.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (musicPlayer != null){
                        val currentTime = progress.toMusicTime()
                        val maxDuration = musicPlayer?.duration

                        playSongBinding.mntStart.text = currentTime

                        if (progress == maxDuration){
                            playNextSong()
                        }

                        if (!musicPlayer?.isPlaying!!){
                            playSongBinding.mntStart.text = musicPlayer?.currentPosition?.toMusicTime()
                        }

                        if (fromUser){
                            musicPlayer?.seekTo(progress)
                            seekBar?.progress = progress
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })
        }catch (e: IllegalStateException){
            Log.e("PlaySongActivity", "[playSong] ${e.message}")
        }
    }

    private fun checkMusicButton() {
        if (musicPlayer?.isPlaying!! && musicPlayer != null){
            playSongBinding.btnPlay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_pause_circle_filled_80))
        }else{
            playSongBinding.btnPlay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_play_circle_80))
        }
    }

    private fun onClick() {
        playSongBinding.tbPlaySong.setNavigationOnClickListener {
            finish()
        }

        playSongBinding.btnPrevMusic.setOnClickListener {
            playPrevSong()
            checkMyTrack()
        }

        playSongBinding.btnPlay.setOnClickListener {
            if (musicPlayer?.isPlaying!!){
                musicPlayer?.pause()
                playSongBinding.btnPlay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_play_circle_80))
            }else{
                musicPlayer?.start()
                playSongBinding.btnPlay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_pause_circle_filled_80))
            }
        }

        playSongBinding.btnNextMusic.setOnClickListener {
            playNextSong()
            checkMyTrack()
        }

        playSongBinding.btnFav.setOnClickListener {
            if (isMyTrack){
                removeMyTrack()
            }else{
                addMyTrack()
            }
        }
    }

    private fun removeMyTrack() {
        val song = songs?.get(position)
        databaseMyTracks
            .child(currentUser?.uid.toString())
            .child("my_tracks")
            .child(song?.keySong.toString())
            .removeValue()
        isMyTrack = false
        checkLikeButton()
    }

    private fun addMyTrack() {
        databaseMyTracks
            .child(currentUser?.uid.toString())
            .child("my_tracks")
            .addListenerForSingleValueEvent(eventListenerAddMyTrack)
    }

    private fun playNextSong() {
        val songsSize = songs?.size?.minus(1)
        if (position < songsSize!!){
            position += 1
            val song = songs?.get(position)
            musicPlayer?.reset()
            if (song != null){
                initView(song)
            }
        }
    }

    private fun playPrevSong() {
        if (position > 0){
            position -= 1
            val song = songs?.get(position)
            musicPlayer?.reset()
            if (song != null){
                initView(song)
            }
        }
    }

    private fun init() {
        //Set Support ActionBar
        setSupportActionBar(playSongBinding.tbPlaySong)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        handler = Handler(Looper.getMainLooper())
        musicPlayer = MediaPlayer()
        musicPlayer?.setAudioAttributes(
            AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )

        currentUser = FirebaseAuth.getInstance().currentUser
        databaseMyTracks = FirebaseDatabase.getInstance().getReference("users")
    }

    override fun onAudioVolumeChanged(currentVolume: Int, maxVolume: Int) {
        seekBar.progress = currentVolume
        handleIcon(currentVolume)
    }
}

