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
