package com.adl.aplikasitim.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import kotlinx.android.synthetic.main.fragment_library.*
import org.jetbrains.anko.startActivity

class LibraryFragment : Fragment() {
    private var _binding :FragmentLibraryBinding? =null
    private val libraryBinding get() = _binding
    private lateinit var topMusicAdapter : TopMusicAdapter

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

        swipeTopMusic()

        onClik()

        showLoading()

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
        hideLoading()
        //GetData
        val topMusics = Repository.getDataTopChartsFromAssets(context)
        //SetData
        topMusicAdapter.setData(topMusics as MutableList<Music>)
        //SetUpRecycleView
        libraryBinding?.rvMusic?.adapter = topMusicAdapter
    }

    //TOMBOL SEARCH
    private fun onClik() {
        topMusicAdapter.onClick { musics, position ->
            context?.startActivity<PlayMusicActivity>(
                PlayMusicActivity.KEY_SONG to musics,
                PlayMusicActivity.KEY_POSITION to position


            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}