package com.adl.aplikasitim.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adl.aplikasitim.R
import com.adl.aplikasitim.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {
    private var _binding :FragmentLibraryBinding? =null
    private val libraryBinding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentLibraryBinding.inflate(inflater,container, false)
        return _binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}