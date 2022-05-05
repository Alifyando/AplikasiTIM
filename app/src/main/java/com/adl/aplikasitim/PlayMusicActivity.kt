package com.adl.aplikasitim

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adl.aplikasitim.databinding.PlaymusicBinding

class PlayMusicActivity : AppCompatActivity() {

    companion object{
        const val KEY_SONG = "key_song"
        const val KEY_POSITION = "key_position"
    }
    private lateinit var playmusicBinding: PlaymusicBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playmusicBinding= PlaymusicBinding.inflate(layoutInflater)
        setContentView(playmusicBinding.root)


        onClick()
    }
    private fun onClick(){

        playmusicBinding.btnBack.setOnClickListener{

        }
        playmusicBinding.btnVolume.setOnClickListener {

        }
        playmusicBinding.btnShuffle.setOnClickListener {

        }
        playmusicBinding.btnPrevMusic.setOnClickListener {

        }
        playmusicBinding.btnPlay.setOnClickListener {

        }
        playmusicBinding.btnNext.setOnClickListener {

        }
        playmusicBinding.btnFav.setOnClickListener {

        }

    }


}