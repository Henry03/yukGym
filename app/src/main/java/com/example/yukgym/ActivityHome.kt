package com.example.yukgym

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class ActivityHome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fragmentHome = FragmentHome()
        val fragmentClass = FragmentClass()
        val fragmentProfile = FragmentProfile()
        val fragmentSchedule = FragmentSchedule()

        setCurrentFragment(fragmentHome)

        val btmNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        btmNav.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.menu_home -> {
                    setCurrentFragment(fragmentHome)
                    true
                }
                R.id.menu_class -> {
                    setCurrentFragment(fragmentClass)
                    true
                }
                R.id.menu_trainer -> {
                    setCurrentFragment(fragmentProfile)
                    true
                }
                R.id.menu_schedule -> {
                    setCurrentFragment(fragmentSchedule)
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Disini kita menghubungkan menu yang telah kita buat dengan activity ini
        val menuInflater = MenuInflater(this)
        menuInflater.inflate(R.menu.exit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_exit){

            // Jika menu yang dipilih adalah menu Exit, maka tampilkan sebuah dialog
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@ActivityHome)
            builder.setMessage("Are you sure want to exit?")
                .setPositiveButton("YES", object : DialogInterface.OnClickListener{
                    override fun onClick(dialogInterface: DialogInterface, i: Int){
                        // Keluar dari aplikasi
                        finishAndRemoveTask()
                    }
                })
                .show()
        }
        return super.onOptionsItemSelected(item)
    }

    fun setCurrentFragment(fragment:Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

    fun setActivity(activity: AppCompatActivity){
        val moveActivity = Intent(this, activity::class.java)
        startActivity(moveActivity)
    }
}