package com.example.brailleapp

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.brailleapp.databinding.ActivityMainBinding
import com.example.brailleapp.ui.game.GameFragment
import com.example.brailleapp.ui.list.ListBrailleFragment
import com.example.brailleapp.ui.profile.ProfileFragment
import com.example.brailleapp.ui.scan.ScanFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        binding.navView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_scan -> loadFragment(ScanFragment())
                R.id.navigation_list -> loadFragment(ListBrailleFragment())
                R.id.navigation_games -> loadFragment(GameFragment())
                R.id.navigation_profile -> loadFragment(ProfileFragment())
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
        return true
    }
}