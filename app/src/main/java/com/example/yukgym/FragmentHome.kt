package com.example.yukgym

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView


class FragmentHome : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Proses menghubungkan layout fragment_mahasiswa.xml dengan fragment ini
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
//        super.onViewCreated(view, savedInstanceState)
//        val layoutManager = LinearLayoutManager(context)
//        val adapter : R = RVMahasiswaAdapter(Mahasiswa.listOfMahasiswa)
//
//        // Menghubungkan rvMahasiswa dengan recycler view yang ada pada layout
//        val rvMahasiswa: RecyclerView = view.findViewById(R.id.rv_mahasiswa)
//
//        // Set layout manager dari recycle view
//        rvMahasiswa.layoutManager = layoutManager
//
//        // Tidak mengubah size recycler view jika terdapat item ditambahkan atau dikurangkan
//        rvMahasiswa.setHasFixedSize(true)
//
//        // Set adapter dari recycler view.
//        rvMahasiswa.adapter = adapter
//    }

}