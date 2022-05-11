package com.adl.aplikasitim.adapter

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.adl.aplikasitim.PlayMusicActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.playlist.view.*

class SearchViewHolder (view: View): RecyclerView.ViewHolder(view) {
//    private var isInMyFavorite = false
//    private lateinit var firebaseAuth: FirebaseAuth

    val judul = view.txtJudul
    val image= view.imageView2
    val artis = view.txtArtis
    val rvPlay= view.rvPlay


    fun bindData(adapter: SearchAdapter, position: Int) {
        judul.setText(adapter.data.get(position).nameSong)
        artis.setText(adapter.data.get(position).artisSong)

        image.let {
            Glide.with(adapter.parent.context)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .load(adapter.data.get(position).imageSong)
            .into(it)
        }

       rvPlay.setOnClickListener({
           val intent = Intent(adapter.parent.context,PlayMusicActivity::class.java)
           intent.putExtra("data",adapter.data.get(position))
           adapter.parent.context.startActivity(intent)
       })
    }
    // Glide.with(adapter.parent.context).load(adapter.data.get(position).uriSong).placeholder(android.R.color.darker_gray).into(playlist.imageView2)

}