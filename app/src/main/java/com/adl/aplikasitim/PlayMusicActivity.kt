package com.adl.aplikasitim

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adl.aplikasitim.databinding.PlaymusicBinding
import com.adl.aplikasitim.models.Music
import com.bumptech.glide.Glide

class PlayMusicActivity : AppCompatActivity() {

    companion object{
        const val KEY_SONG = "key_song"
        const val KEY_POSITION = "key_position"
    }
    private lateinit var playmusicBinding: PlaymusicBinding
    private var position = 0
    private var musics :MutableList<Music>? = null
    private var musicPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playmusicBinding= PlaymusicBinding.inflate(layoutInflater)
        setContentView(playmusicBinding.root)

        getData()
        onClick()
    }

    private fun getData() {
        if(intent != null){
            musics = intent.getParcelableArrayListExtra(KEY_SONG)
            position = intent.getIntExtra(KEY_POSITION,0)
            musics?.let {
                val music = it[position]
                initView(music)
            }
        }
    }

    private fun initView(music: Music) {
        playmusicBinding.txtJudul.text=music.nameSong
        playmusicBinding.txtMusisi.text=music.artistSong
        Glide.with(this)
            .load(music.imageSong)
            .placeholder(android.R.color.darker_gray)
            .into(playmusicBinding.imgMusic)

    }

    private fun onClick(){

        playmusicBinding.btnBack.setOnClickListener{

        }
        playmusicBinding.btnVolume.setOnClickListener {

        }
        playmusicBinding.btnShuffle.setOnClickListener {

        }
        playmusicBinding.btnPrevMusic.setOnClickListener {
            playPrevMusic()
        }
        playmusicBinding.btnPlay.setOnClickListener {

        }
        playmusicBinding.btnNextMusic.setOnClickListener {
            playNextMusic()
        }
        playmusicBinding.btnFav.setOnClickListener {

        }

    }

    private fun playNextMusic() {
        val musicSize = musics?.size?.minus(1)
        if(position < musicSize!!){
            position +=1
            val music = musics?.get(position)
            if(music != null) {
                initView(music)
            }
        }
    }

    private fun playPrevMusic() {
        if(position > 0){
            position -=1
            val music = musics?.get(position)
            musicPlayer?.reset()
            if(music != null){
                initView(music)
            }
        }
    }


}