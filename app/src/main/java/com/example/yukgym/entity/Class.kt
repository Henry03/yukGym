package com.example.yukgym.entity

import com.example.yukgym.R

class Class(var name: String, var category: String, var duration: String, var images : Int) {
    companion object{
        @JvmField
        var listOfClass = arrayOf(
            Class("Advanced Flow Yoga", "Mind and Body", "90min", R.drawable.advanced_flow_yoga),
            Class("Aerial Flow Yoga", "Mind and Body", "60min", R.drawable.aerial_flow_yoga),
            Class("Animal Flow", "Strength and Conditioning", "60min", R.drawable.animal_flow),
            Class("Aqua Fit", "Strength and Conditioning", "60min", R.drawable.aqua_fit),
            Class("Aqua Zumba", "Feature Class", "60min", R.drawable.aqua_zumba),
            Class("Body Attack", "Cardio", "60min", R.drawable.bodyattack_banner),
            Class("Body Balance", "Mind and Body", "60min", R.drawable.bodybalance_banner),
            Class("Body Combat", "Cardio", "60min", R.drawable.bodycombat_banner),
            Class("Body Jam", "Dance", "60min", R.drawable.bodyjam_banner),
            Class("Body Pump", "Strength and Conditioning", "60min", R.drawable.bodypump_banner)
        )
    }
}