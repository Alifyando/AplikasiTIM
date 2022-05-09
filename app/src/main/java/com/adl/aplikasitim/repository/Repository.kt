package com.adl.aplikasitim.repository

import android.content.Context
import android.util.Log
import com.adl.aplikasitim.models.Album
import com.adl.aplikasitim.models.Music
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object Repository {
    fun getDataTopChartsFromAssets(context: Context?): List<Music>? {
        val json: String?
        return try {
            val inputStream = context?.assets?.open("json/topChart.json")
            json = inputStream?.bufferedReader().use { it?.readText() }
            Log.d("Repository", "getDataTopChartsFromAssets: $json")
            val groupListType = object : TypeToken<List<Music?>>() {}.type
            Gson().fromJson(json, groupListType)
        }catch (e: IOException){
            e.printStackTrace()
            Log.e("Repository", "error_getDataTopChartsFromAssets: ${e.message}")
            null
        }
    }

    fun getDataTopAlbumsFromAssets(context: Context?): List<Album>? {
        val json: String?
        return try {
            val inputStream = context?.assets?.open("json/topAlbum.json")
            json = inputStream?.bufferedReader().use { it?.readText() }
            Log.d("Repository", "getDataTopChartsFromAssets: $json")
            val groupListType = object : TypeToken<List<Album?>>() {}.type

            Gson().fromJson(json, groupListType)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Repository", "error_getDataTopChartsFromAssets: ${e.message}")
            null
        }
    }
    fun addDataToTopChart(){
        val databaseTopCharts = FirebaseDatabase.getInstance().getReference("top_Chart")
        val songs = mutableListOf<Music?>()

        //Mengambil Data dari Firebase Storage
        FirebaseStorage
            .getInstance("gs://aplikasitim-8bf3a.appspot.com")
            .reference
            .child("musics")
            .listAll()
            .addOnSuccessListener { listResult ->
                listResult.items.forEach{item ->
                    item.downloadUrl
                        .addOnSuccessListener { uri->
                            val names = item.name.split("_")

                            val artistName = names[0].trim()
                            val albumName = names[1].trim()
                            val songName = names[2].trim()
                            val yearAlbum = names[3].trim().replace(".mp3", "")

                            val keySong = databaseTopCharts.push().key
                            songs.add(Music(
                                keySong = keySong,
                                nameSong = songName,
                                uriSong = uri.toString(),
                                artistSong = artistName,
                                albumNameSong = albumName,
                                yearSong = yearAlbum.toInt()
                            ))
                            databaseTopCharts.setValue(songs)
                        }
                        .addOnFailureListener { e->
                            Log.e("Repository", "[addDataToTopChart-downloadUrl] ${e.message}")
                        }
                }
            }
            .addOnFailureListener{e ->
                Log.e("Repository", "[addDataToTopChart] ${e.printStackTrace()}")
                Log.e("Repository", "[addDataToTopChart] ${e.message}")
            }
    }

    fun addDataToTopChartsImage(){
        val databaseTopCharts = FirebaseDatabase.getInstance().getReference("top_Chart")
        //Mengambil Data dari Firebase Storage
        FirebaseStorage
            .getInstance("gs://aplikasitim-8bf3a.appspot.com")
            .reference
            .child("images")
            .listAll()
            .addOnSuccessListener { listResult ->
                listResult.items.forEach { item->
                    item.downloadUrl
                        .addOnSuccessListener { uri->
                            val albumName = item.name.replace(".jpg", "").trim()
                            databaseTopCharts
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (snap in snapshot.children){
                                            val song = snap.getValue(Music::class.java)
                                            if (song?.albumNameSong == albumName){
                                                databaseTopCharts.child(snap.key.toString())
                                                    .updateChildren(mapOf(
                                                        "image_song" to uri.toString()
                                                    ))
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e("Repository", "[onCancelled] ${error.message}")
                                    }
                                })
                        }
                        .addOnFailureListener { e->
                            Log.e("Repository", "[addDataToTopChartsImage-downloadUrl] ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e->
                Log.e("Repository", "[addDataToTopChartsImage] ${e.message}")
            }
    }

    fun addDataToTopAlbums(){
        val databaseTopAlbums = FirebaseDatabase.getInstance().getReference("top_Album")
        val databaseTopCharts = FirebaseDatabase.getInstance().getReference("top_Chart")

        val albums = mutableListOf<Album>()
        var topCharts = mutableListOf<Music>()

        databaseTopCharts.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.toList()
                val data = items.sortedWith(compareBy {
                    it.getValue(Music::class.java)?.albumNameSong
                })

                for (i in data.indices){
                    var nextSong: Music? = Music()
                    if (i < data.size - 1){
                        nextSong = data[i + 1].getValue(Music::class.java)
                    }
                    val song = data[i].getValue(Music::class.java)
                    if (song != null){
                        topCharts.add(song)
                    }

                    if (song?.albumNameSong != nextSong?.albumNameSong){
                        val keyAlbum = databaseTopAlbums.push().key
                        albums.add(Album(
                            artistAlbum = song?.artistSong,
                            keyAlbum = keyAlbum,
                            nameAlbum = song?.albumNameSong,
                            yearAlbum = song?.yearSong,
                            songs = topCharts
                        ))
                        topCharts = mutableListOf()
                    }
                }
                databaseTopAlbums.setValue(albums)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Repository", "[onCancelled] ${error.message}")
            }

        })
    }

    fun addDataToTopAlbumsImage(){
        val databaseTopAlbums = FirebaseDatabase.getInstance().getReference("top_albums")
        FirebaseStorage
            .getInstance("gs://aplikasitim-8bf3a.appspot.com")
            .reference
            .child("images")
            .listAll()
            .addOnSuccessListener { listResult->
                listResult.items.forEach { item->
                    item.downloadUrl
                        .addOnSuccessListener { uri->
                            val albumName = item.name.replace(".jpg", "").trim()

                            databaseTopAlbums.addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (snap in snapshot.children){
                                        val album = snap.getValue(Album::class.java)
                                        if (album?.nameAlbum == albumName){
                                            databaseTopAlbums.child(snap.key.toString())
                                                .updateChildren(mapOf(
                                                    "image_album" to uri.toString()
                                                ))
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("Repository", "[onCancelled] ${error.message}")
                                }

                            })
                        }
                        .addOnFailureListener { e->
                            Log.e("Repository", "[addDataToTopAlbumsImage-downloadUrl] ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e->
                Log.e("Repository", "[addDataToTopAlbumsImage] ${e.message}")
            }
    }
}
