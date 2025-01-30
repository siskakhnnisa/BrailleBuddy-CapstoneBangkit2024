package com.example.brailleapp.ui.signin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.example.brailleapp.MainActivity
import com.example.brailleapp.R
import com.example.brailleapp.ui.customview.CustomButton
import com.example.brailleapp.ui.customview.CustomTextInput
import com.example.brailleapp.ui.signup.SignupActivity

class SigninActivity : AppCompatActivity() {

    private lateinit var loginUsername: CustomTextInput
    private lateinit var loginPassword: CustomTextInput
    private lateinit var loginButton: CustomButton
    private lateinit var signupButton: CustomButton
    private fun updateLoginButtonState() {
        loginButton.isEnabled = loginUsername.isValid == true && loginPassword.isValid == true
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        loginUsername = findViewById(R.id.signin_username)
        loginPassword = findViewById(R.id.signin_password)
        loginButton = findViewById(R.id.btn_signin)
        signupButton = findViewById(R.id.btn_signup)

        loginUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateLoginButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        loginPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateLoginButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        signupButton.setOnClickListener {
            val intent = Intent(this@SigninActivity, SignupActivity::class.java)
            startActivity(intent)
        }


        loginButton.isEnabled = false // Awalnya tombol dinonaktifkan
        loginButton.setOnClickListener {
            if (!validateUsername() || !validatePassword()) {
                // Validation failed
            } else {
                checkUser()
            }
        }
        supportActionBar?.hide()
    }

    private fun validateUsername(): Boolean {
        val value = loginUsername.text.toString()
        return if (value.isEmpty()) {
            loginUsername.error = "Username cannot be empty"
            false
        } else {
            loginUsername.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val value = loginPassword.text.toString()
        return if (value.isEmpty()) {
            loginPassword.error = "Password cannot be empty"
            false
        } else {
            loginPassword.error = null
            true
        }
    }

    private fun checkUser() {
        val userUsername = loginUsername.text.toString().trim()
        val userPassword = loginPassword.text.toString().trim()

        val reference = FirebaseDatabase.getInstance().getReference("users")
        val checkUserDatabase = reference.orderByChild("username").equalTo(userUsername)

        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    loginUsername.error = null
                    val passwordFromDB = snapshot.child(userUsername).child("password").getValue(String::class.java)

                    if (passwordFromDB == userPassword) {
                        loginUsername.error = null

                        // Set login state
                        val sharedPref = getSharedPreferences("userDetails", MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putBoolean("isLoggedIn", true)
                            apply()
                        }

                        val nameFromDB = snapshot.child(userUsername).child("name").getValue(String::class.java)
                        val emailFromDB = snapshot.child(userUsername).child("email").getValue(String::class.java)
                        val usernameFromDB = snapshot.child(userUsername).child("username").getValue(String::class.java)

                        // Save user details in SharedPreferences
                        with (sharedPref.edit()) {
                            putString("name", nameFromDB)
                            putString("email", emailFromDB)
                            putString("username", usernameFromDB)
                            putString("password", passwordFromDB)
                            apply()
                        }

                        // Simpan preferensi status login user
                        val loginPref = getSharedPreferences("loginStatus", Context.MODE_PRIVATE)
                        with (loginPref.edit()) {
                            putBoolean("isLoggedIn", true)
                            apply()
                        }

                        val intent = Intent(this@SigninActivity, MainActivity::class.java)
                        intent.putExtra("selectFragment", "ProfileFragment")
                        startActivity(intent)
                        finish()
                    } else {
                        loginPassword.error = "Invalid Credentials"
                        loginPassword.requestFocus()
                    }
                } else {
                    loginUsername.error = "User does not exist"
                    loginUsername.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SigninActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


}