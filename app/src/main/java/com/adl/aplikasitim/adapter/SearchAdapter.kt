package com.adl.aplikasitim.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adl.aplikasitim.R
import com.adl.aplikasitim.models.MusicX

class SearchAdapter (val data: ArrayList<MusicX>): RecyclerView.Adapter<SearchViewHolder>(){
    lateinit var parent: ViewGroup
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        this.parent = parent

        return SearchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.playlist,parent,false))
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bindData(this@SearchAdapter,position)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}