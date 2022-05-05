package com.adl.aplikasitim.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adl.aplikasitim.databinding.PlaylistBinding
import com.adl.aplikasitim.databinding.PlaymusicBinding
import com.adl.aplikasitim.models.Music


class TopMusicAdapter: RecyclerView.Adapter<TopMusicAdapter.ViewHolder>() {

    private var music = mutableListOf<Music>()
    private  var listener : ((MutableList<Music>, Int) -> Unit)? = null

    class ViewHolder(private val binding: PlaylistBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindItem(music: Music,listener: ((MutableList<Music>, Int) -> Unit)?){
            binding.txtArtis.text = music.artistSong
            binding.txtJudulmusic.text=music.nameSong

           // itemView.setOnClickListener{

           // }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val binding = PlaylistBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(music[position],listener)
    }

    override fun getItemCount(): Int = music.size

        fun onClick(listener : ((MutableList<Music>, Int) -> Unit)){
            this.listener=listener
        }


        fun setData(music:MutableList<Music>){
            this.music = music
            notifyDataSetChanged()
        }



}