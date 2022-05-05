package com.adl.aplikasitim.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adl.aplikasitim.MainActivity
import com.adl.aplikasitim.databinding.ActivityMainBinding
import com.adl.aplikasitim.databinding.PlayalbumBinding
import com.adl.aplikasitim.databinding.PlaylistBinding
import com.adl.aplikasitim.models.Album
import com.bumptech.glide.Glide

class TopAlbumAdapter :RecyclerView.Adapter<TopAlbumAdapter.ViewHolder>() {

    private var albums = mutableListOf<Album>()
    private var listener : ((Album) -> Unit)? = null

    class ViewHolder (private val binding : PlayalbumBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindItem(album :Album, listener: ((Album) -> Unit)?){
            Glide.with(itemView).load(album.imageAlbum).placeholder(android.R.color.darker_gray).into(binding.imageView3)
            binding.txtJudulAlbum.text = album.nameAlbum
            binding.txtArtisAlbum.text = album.artistAlbum

            itemView.setOnClickListener{
                if(listener != null){
                    listener(album)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlayalbumBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(albums[position],listener)
    }

    override fun getItemCount(): Int = albums.size
    fun onClick(listener: ((Album) -> Unit)) {
        this.listener = listener
    }

    fun setData(albums:MutableList<Album>){
        this.albums = albums
        notifyDataSetChanged()
    }
}