package com.adl.aplikasitim.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.adl.aplikasitim.PlayMusicActivity
import com.adl.aplikasitim.Utils.hide
import com.adl.aplikasitim.Utils.visible
import com.adl.aplikasitim.adapter.MyTrackAdapter
import com.adl.aplikasitim.databinding.FragmentMyTrackBinding
import com.adl.aplikasitim.models.Music
import com.adl.aplikasitim.repository.Repository
import org.jetbrains.anko.startActivity

class MyTrackFragment : Fragment(){

    private var _binding : FragmentMyTrackBinding? = null
    private val myTrackBinding get() = _binding
    private lateinit var myTrackAdapter: MyTrackAdapter

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

        myTrackAdapter = MyTrackAdapter()


        swipeMyTrack()
        onClick()

        showLoading()
        Handler(Looper.getMainLooper()).postDelayed({
            showMyTrack()
        },2000)
    }

    private fun swipeMyTrack() {
        myTrackBinding?.swipeMyTrack?.setOnRefreshListener {
            showMyTrack()
        }
    }


    private fun showMyTrack() {
        hideLoading()
        //GetData
        val topTracks = Repository.getDataTopChartsFromAssets(context)
        //SetData
        myTrackAdapter.setData(topTracks as MutableList<Music>)
        //SetUpRecycleView
        myTrackBinding?.rvMyTrack?.adapter = myTrackAdapter
    }

    private fun hideLoading() {
        myTrackBinding?.swipeMyTrack?.hide()
    }

    private fun showLoading() {
        myTrackBinding?.swipeMyTrack?.visible()

    }

    private fun onClick() {
        myTrackAdapter.onClick { musics,position ->
            context?.startActivity<PlayMusicActivity>(
                PlayMusicActivity.KEY_SONG to musics,
                PlayMusicActivity.KEY_POSITION to position
            )
        }
    }
}