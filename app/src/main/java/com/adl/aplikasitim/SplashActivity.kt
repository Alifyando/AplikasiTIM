package com.adl.aplikasitim

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.adl.aplikasitim.repository.Repository
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Menambahkan data top charts
        Repository.addDataToTopChart()
        //Menambahkan data image di top charts
        Repository.addDataToTopChartsImage()
        //Menambahkan data albums
        Repository.addDataToTopAlbums()
        //Menambahkan data image di top albums
        Repository.addDataToTopAlbumsImage()
        delayAndGoToLogin()
    }

    private fun delayAndGoToLogin() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity<LoginActivity>()
            finishAffinity()
        }, 1200)
    }
}