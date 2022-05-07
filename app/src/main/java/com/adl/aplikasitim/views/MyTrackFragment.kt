package com.adl.aplikasitim.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.adl.aplikasitim.PlayMusicActivity
import com.adl.aplikasitim.Utils.gone
import com.adl.aplikasitim.Utils.hide
import com.adl.aplikasitim.Utils.visible
import com.adl.aplikasitim.adapter.MyTrackAdapter
import com.adl.aplikasitim.databinding.FragmentMyTrackBinding
import com.adl.aplikasitim.models.Music
import com.adl.aplikasitim.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import org.jetbrains.anko.startActivity

class MyTrackFragment : Fragment(){

    private var _binding: FragmentMyTrackBinding? = null
    private val myTracksBinding get() = _binding
    private lateinit var myTracksAdapter: MyTrackAdapter
    private var currentUser: FirebaseUser? = null
    private lateinit var databaseMyTracks: DatabaseReference

    private val eventListenerMyTracks = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            hideLoading()
            val songs = mutableListOf<Music>()

            if (snapshot.value != null){
                hideEmptyData()
                for (snap in snapshot.children){
                    val song = snap.getValue(Music::class.java)
                    if (song != null) songs.add(song)
                }

                myTracksAdapter.setData(songs)
            }else{
                showEmptyData()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            hideLoading()
            Log.e("MyTracksFragment", "[onCancelled] ${error.message}")
        }
    }

    private fun hideEmptyData() {
        myTracksBinding?.ivEmptyData?.gone()
        myTracksBinding?.rvMyTrack?.visible()
    }

    private fun showEmptyData(){
        myTracksBinding?.ivEmptyData?.visible()
        myTracksBinding?.rvMyTrack?.gone()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyTrackBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Init
        myTracksAdapter = MyTrackAdapter()
        currentUser = FirebaseAuth.getInstance().currentUser
        databaseMyTracks = FirebaseDatabase.getInstance().getReference("users")

        swipeMyTracks()
        onClick()

        showLoading()
        showMyTracks()
    }

    private fun onClick() {
        myTracksAdapter.onClick { songs, position ->
            context?.startActivity<PlayMusicActivity>(
                PlayMusicActivity.KEY_SONGS to songs,
                PlayMusicActivity.KEY_POSITION to position
            )
        }
    }

    private fun swipeMyTracks() {
        myTracksBinding?.swipeMyTrack?.setOnRefreshListener {
            showMyTracks()
        }
    }

    private fun showLoading() {
        myTracksBinding?.swipeMyTrack?.visible()
    }

    private fun hideLoading() {
        myTracksBinding?.swipeMyTrack?.hide()
    }

    private fun showMyTracks(){
        hideLoading()
        //GetData
//        val topCharts = Repository.getDataTopChartsFromAssets(context)
        //Get Data from Firebase
        databaseMyTracks
            .child(currentUser?.uid.toString())
            .child("my_tracks")
            .addValueEventListener(eventListenerMyTracks)

        //SetupRecyclerView
        myTracksBinding?.rvMyTrack?.adapter = myTracksAdapter
    }
}