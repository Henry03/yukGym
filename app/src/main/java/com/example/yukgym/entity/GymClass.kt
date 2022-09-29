package com.example.yukgym.entity

import com.example.yukgym.R

class GymClass(var id: Int, var name: String, var category: String, var duration: String, var images : Int, var second : Int) {
    companion object{
        @JvmField
        var listOfClass = arrayListOf(
            GymClass(1, "Advanced Flow Yoga", "Mind and Body", "90min", R.drawable.advanced_flow_yoga, 5400),
            GymClass(2, "Aerial Flow Yoga", "Mind and Body", "60min", R.drawable.aerial_flow_yoga, 3600),
            GymClass(3, "Animal Flow", "Strength and Conditioning", "60min", R.drawable.animal_flow, 3600),
            GymClass(4, "Aqua Fit", "Strength and Conditioning", "60min", R.drawable.aqua_fit, 3600),
            GymClass(5, "Aqua Zumba", "Feature Class", "60min", R.drawable.aqua_zumba, 3600),
            GymClass(6, "Body Attack", "Cardio", "60min", R.drawable.bodyattack_banner, 3600),
            GymClass(7, "Body Balance", "Mind and Body", "60min", R.drawable.bodybalance_banner, 3600),
            GymClass(8, "Body Combat", "Cardio", "60min", R.drawable.bodycombat_banner, 3600),
            GymClass(9, "Body Jam", "Dance", "60min", R.drawable.bodyjam_banner, 3600),
            GymClass(10, "Body Pump", "Strength and Conditioning", "60min", R.drawable.bodypump_banner, 3600)
        )
    }
}