package com.adl.aplikasitim.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.adl.aplikasitim.PlayMusicActivity
import com.adl.aplikasitim.R
import com.adl.aplikasitim.Utils.gone
import com.adl.aplikasitim.Utils.hide
import com.adl.aplikasitim.Utils.visible
import com.adl.aplikasitim.adapter.SearchAdapter
import com.adl.aplikasitim.adapter.TopMusicAdapter
import com.adl.aplikasitim.databinding.FragmentLibraryBinding
import com.adl.aplikasitim.models.Music
import com.adl.aplikasitim.models.MusicX
import com.adl.aplikasitim.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_library.*
import org.jetbrains.anko.startActivity

class LibraryFragment : Fragment() {
    private var _binding :FragmentLibraryBinding? =null
    private val libraryBinding get() = _binding
    private lateinit var topMusicAdapter : TopMusicAdapter
    private lateinit var databaseTopCharts: DatabaseReference

    lateinit var searchAdapter: SearchAdapter
    lateinit var lstMusicX : ArrayList<MusicX>
    lateinit var lstSearch:ArrayList<MusicX>
    lateinit var searchString:String

    private lateinit var auth: FirebaseAuth

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
      //initDB
        databaseTopCharts= FirebaseDatabase.getInstance().getReference("top_Chart")

       lstMusicX=ArrayList<MusicX>()
        lstSearch= ArrayList<MusicX>()
        searchAdapter=SearchAdapter(lstSearch)



        swipeTopMusic()
        onClik()
        showLoading()
        showTopMusics()

        Handler(Looper.getMainLooper()).postDelayed({
          showTopMusics()
        },2000)


        ///btn search
        btnSearch.setOnClickListener({
            searchString = searchText.text.toString()
            databaseTopCharts.get().addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    lstMusicX.clear()
                    for (data in snapshot.children){
                        val albumname= data.child("album_name_song").getValue(String::class.java)
                        val artis = data.child("artist_song").getValue(String::class.java)
                        val keysong = data.child("key_song").getValue(String::class.java)
                        val namesong = data.child("name_song").getValue(String::class.java)
                        val urisong = data.child("uri_song").getValue(String::class.java)
                        val yearsong = data.child("year_song").getValue(Int::class.java)
                        val imagesong = data.child("image_song").getValue(String::class.java)
                        lstMusicX.add( MusicX(albumname.toString(),namesong.toString(),yearsong.toString(),artis.toString(), urisong.toString(),imagesong.toString(),keysong.toString()))
                        // Log.d("TAG", "nama: ${namaresep}\nimagelink: ${imagelink}")
                    }
                    //resepAdapter.notifyDataSetChanged()
                    Search(lstMusicX)
                } else {
                    Log.d("TAG", task.exception!!.message!!) //Don't ignore potential errors!
                }
            }

        })
    }
//menamppilkan userinfo
    private fun loadUserInfo() {
            val user =Firebase.auth.currentUser
            val uid = Firebase.auth.uid!!
            val ref = FirebaseDatabase.getInstance().getReference("users")
            ref.child(uid).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    val name = "${snapshot.child("full_name").value}"
                    txtUsername.setText(name)
                }
            }
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
    fun Search(list:ArrayList<MusicX>){

        val pattern = searchString.toRegex((RegexOption.IGNORE_CASE))
        lstSearch.clear()
        for(data in list){
            if (pattern.containsMatchIn(data.nameSong)) {
                println("${data.nameSong} matches")
                val resep:String=data.nameSong.toString()
                //Toast.makeText(activity,"${dataresep.title} matches",Toast.LENGTH_LONG).show()
              lstSearch.add( MusicX(data.albumNameSong,data.nameSong,data.yearSong,data.artisSong,data.uriSong,data.imageSong,data.keySong))
            }
        }
       //searchAdapter.notifyDataSetChanged()

        rvMusic.apply {
          layoutManager= LinearLayoutManager(activity)
         adapter=searchAdapter
       }
    }

    override fun onResume() {
        super.onResume()
        loadUserInfo()
    }
}