package com.adl.aplikasitim.repository

import android.content.Context
import android.util.Log
import com.adl.aplikasitim.models.Album
import com.adl.aplikasitim.models.MusicResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object Repository {
    fun getDataTopChartsFromAssets(context: Context?): List<MusicResponse>? {
        val json: String?
        return try {
            val inputStream = context?.assets?.open("json/topcharts.json")
            json = inputStream?.bufferedReader().use { it?.readText() }
            Log.d("Repository", "getDataTopChartsFromAssets: $json")
            val groupListType = object : TypeToken<List<MusicResponse?>>() {}.type
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
            val inputStream = context?.assets?.open("json/topalbums.json")
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
}