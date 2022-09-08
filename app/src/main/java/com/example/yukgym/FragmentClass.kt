package com.example.yukgym

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yukgym.entity.Class

class FragmentClass : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Menghubungkan layout fragment_dosen.xml dengan fragment ini
        return inflater.inflate(R.layout.fragment_class, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        val adapter : RVClassAdapter = RVClassAdapter(Class.listOfClass)

        // Menghubungkan rvMahasiswa dengan recycler view yang ada pada layout
        val rvClassAdapter : RecyclerView = view.findViewById(R.id.rv_class)

        // Set Layout Manager dari recycler view
        rvClassAdapter.layoutManager = layoutManager

        // Tidak mengubah size recycler view jika terdapat item ditambahkan atau dikurangkan
        rvClassAdapter.setHasFixedSize(true)

        // Set Adapter dari recycler view
        rvClassAdapter.adapter = adapter
    }
}