package com.example.yukgym

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        changeFragment(HomeFragment())
    }

    fun changeFragment(fragment: Fragment?){
        if(fragment != null){
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutHome, fragment)
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Disini kita menghubungkan menu yang telah kita buat dengan activity ini
        val menuInflater = MenuInflater(this)
        menuInflater.inflate(R.menu.navigation_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_home){
            // Jika menu yang dipilih adalah Mahasiswa maka ganti fragmentnya dengan FragmentMahasiswa
            changeFragment(HomeFragment())
        }else if(item.itemId == R.id.menu_membership){
            // Jika menu yang dipilih adalah Dosen maka ganti fragmentnya dengan FragmentDosen
            changeFragment(MembershipFragment())
        }else if(item.itemId == R.id.menu_trainer){
            changeFragment(TrainerFragment())
        }else if(item.itemId == R.id.menu_schedule){
            changeFragment(ScheduleFragment())
        }
        else{
            // Jika menu yang dipilih adalah menu Exit, maka tampilkan sebuah dialog
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@HomeActivity)
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
}