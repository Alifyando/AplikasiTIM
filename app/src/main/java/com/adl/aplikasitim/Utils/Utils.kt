package com.adl.aplikasitim.Utils

import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

fun SwipeRefreshLayout.visible(){
    isRefreshing = true
}

fun SwipeRefreshLayout.hide(){
    isRefreshing = false
}

fun View.visible(){
    visibility = View.VISIBLE
}

fun View.gone(){
    visibility = View.GONE
}
fun Int.toMusicTime(): String{
    var elapsedTime: String?
    val minutes = this / 1000 / 60
    val seconds = this / 1000 % 60
    elapsedTime = "$minutes:"
    if (seconds < 10){
        elapsedTime += "0"
    }
    elapsedTime += seconds
    return elapsedTime
}
