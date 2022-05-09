package com.adl.aplikasitim

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.adl.aplikasitim.Utils.toMusicTime
import com.adl.aplikasitim.databinding.PlaymusicBinding
import com.adl.aplikasitim.models.Music
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class PlayMusicActivity : AppCompatActivity() {

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

        init()
        getData()
        onClick()
    }

    override fun onPause() {
        super.onPause()
        musicPlayer?.pause()
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
}

