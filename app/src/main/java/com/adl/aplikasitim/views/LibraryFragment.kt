package com.adl.aplikasitim.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adl.aplikasitim.PlayMusicActivity
import com.adl.aplikasitim.R
import com.adl.aplikasitim.Utils.gone
import com.adl.aplikasitim.Utils.hide
import com.adl.aplikasitim.Utils.visible
import com.adl.aplikasitim.adapter.TopMusicAdapter
import com.adl.aplikasitim.databinding.FragmentLibraryBinding
import com.adl.aplikasitim.models.Music
import com.adl.aplikasitim.repository.Repository
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_library.*
import org.jetbrains.anko.startActivity

class LibraryFragment : Fragment() {
    private var _binding :FragmentLibraryBinding? =null
    private val libraryBinding get() = _binding
    private lateinit var topMusicAdapter : TopMusicAdapter
    private lateinit var databaseTopCharts: DatabaseReference

    private val eventListenerTopCharts = object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            hideLoading()
            Log.d("LibraryFragment", "[onDataChange] ${snapshot.value}")
            val gson = Gson().toJson(snapshot.value)
            val type = object : TypeToken<MutableList<Music>>(){}.type
            val songs = Gson().fromJson<MutableList<Music>>(gson, type)

            if (songs != null)
                topMusicAdapter.setData(songs)
        }
        override fun onCancelled(error: DatabaseError) {
            hideLoading()
            Log.e("LibraryFragment", "[onCancelled] ${error.message}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentLibraryBinding.inflate(inflater,container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topMusicAdapter = TopMusicAdapter()
        databaseTopCharts= FirebaseDatabase.getInstance().getReference("top_charts")

        swipeTopMusic()

        onClik()

        showLoading()
        showTopMusics()

        Handler(Looper.getMainLooper()).postDelayed({
          showTopMusics()
        },2000)
    }

    private fun swipeTopMusic(){
        libraryBinding?.swipeTopMusic?.setOnRefreshListener{
            showTopMusics()
        }
    }

    private fun showLoading() {
        libraryBinding?.swipeTopMusic?.visible()

    }

    private fun hideLoading(){
        libraryBinding?.swipeTopMusic?.hide()
    }

    private fun showTopMusics(){
        //GetData
        //val topMusics = Repository.getDataTopChartsFromAssets(context)
        //SetData
        //topMusicAdapter.setData(topMusics as MutableList<Music>)

        databaseTopCharts.addValueEventListener(eventListenerTopCharts)
        //SetUpRecycleView
        libraryBinding?.rvMusic?.adapter = topMusicAdapter
    }

    private fun onClik() {
        topMusicAdapter.onClick { musics, position ->
            context?.startActivity<PlayMusicActivity>(
                PlayMusicActivity.KEY_SONGS to musics,
                PlayMusicActivity.KEY_POSITION to position
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun searchView(){
        _binding?.btnSearch?.setOnClickListener {


        }
    }


}