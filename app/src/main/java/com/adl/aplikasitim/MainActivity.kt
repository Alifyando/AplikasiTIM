package com.adl.aplikasitim

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.adl.aplikasitim.databinding.ActivityMainBinding
import com.adl.aplikasitim.views.LibraryFragment
import com.adl.aplikasitim.views.MyTrackFragment
import com.adl.aplikasitim.views.ProfileFragment
import com.adl.aplikasitim.views.TopAlbumsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        init()

    }
    override fun onBackPressed() {
        super.onBackPressed()
        val selectedItemId = mainBinding.btmNavigationMain.getSelectedItemId()
        if (selectedItemId == R.id.action_library) {
            finishAffinity()
        } else {
            openHomeFragment()
        }
    }
    private fun init(){
        // untk sett up botton navigation bar
        mainBinding.btmNavigationMain.setOnItemSelectedListener { id ->
            when (id) {
                R.id.action_library -> openFragment(LibraryFragment())
                R.id.action_my_Accounts -> openFragment(ProfileFragment())
                R.id.action_my_tracks -> openFragment(MyTrackFragment())
                R.id.action_top_albums-> openFragment(TopAlbumsFragment())

            }
        }
        openHomeFragment()
    }

    private fun openHomeFragment() {
        mainBinding.btmNavigationMain.setItemSelected(R.id.action_library)
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_main,fragment)
            .addToBackStack(null)
            .commit()
    }
}