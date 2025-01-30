package com.example.brailleapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.brailleapp.MainActivity
import com.example.brailleapp.R
import com.example.brailleapp.ui.signin.SigninActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tambahkan logika untuk memeriksa status login
        val sharedPref = getSharedPreferences("userDetails", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        // Tambahkan delay untuk memberikan efek splash
        findViewById<View>(R.id.main).postDelayed({
            if (isLoggedIn) {
                // Jika pengguna sudah login, arahkan ke MainActivity
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
            } else {
                // Jika belum login atau sudah logout, arahkan ke SigninActivity
                val intent = Intent(this@SplashActivity, SigninActivity::class.java)
                startActivity(intent)
            }
            finish() // Tutup SplashActivity
        }, 2000) // Delay selama 2 detik
    }
}
