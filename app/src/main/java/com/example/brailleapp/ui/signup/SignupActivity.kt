package com.example.brailleapp.ui.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.brailleapp.R
import com.example.brailleapp.ui.profile.EditProfileActivity
import com.example.brailleapp.ui.customview.CustomButton
import com.example.brailleapp.ui.customview.CustomTextInput
import com.example.brailleapp.ui.signin.SigninActivity
import com.example.brailleapp.utils.HelperClass

class SignupActivity : AppCompatActivity() {

    private lateinit var signupName: CustomTextInput
    private lateinit var signupUsername: CustomTextInput
    private lateinit var signupEmail: CustomTextInput
    private lateinit var signupPassword: CustomTextInput
    private lateinit var signupButton: CustomButton
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signupName = findViewById(R.id.signup_name)
        signupEmail = findViewById(R.id.signup_email)
        signupUsername = findViewById(R.id.signup_username)
        signupPassword = findViewById(R.id.signup_password)
        signupButton = findViewById(R.id.signup_button)

        // Tambahkan listener untuk validasi input secara dinamis
        val textInputs = listOf(signupName, signupEmail, signupUsername, signupPassword)
        textInputs.forEach { input ->
            input.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    validateInputs()
                }
            })
        }

        signupButton.setOnClickListener {
            database = FirebaseDatabase.getInstance()
            reference = database.getReference("users")

            val name = signupName.text.toString()
            val email = signupEmail.text.toString()
            val username = signupUsername.text.toString()
            val password = signupPassword.text.toString()

            val helperClass = HelperClass(name, email, username, password)
            reference.child(username).setValue(helperClass)

            Toast.makeText(this@SignupActivity, "You have signed up successfully!", Toast.LENGTH_SHORT).show()

            // Set login state
            val sharedPref = getSharedPreferences("userDetails", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean("isLoggedIn", true)
                apply()
            }

            // Pass data to EditProfileActivity
            val intentEditProfile = Intent(this@SignupActivity, EditProfileActivity::class.java)
            intentEditProfile.putExtra("name", name)
            intentEditProfile.putExtra("email", email)
            intentEditProfile.putExtra("username", username)
            intentEditProfile.putExtra("password", password)
            startActivity(intentEditProfile)

            // Pass data to SignInActivity (optional if needed)
            val intentSignIn = Intent(this@SignupActivity, SigninActivity::class.java)
            startActivity(intentSignIn)
        }
        supportActionBar?.hide()
    }

    // Fungsi untuk memvalidasi semua input dan mengubah status tombol
    private fun validateInputs() {
        val isNameValid = signupName.isValid == true
        val isEmailValid = signupEmail.isValid == true
        val isUsernameValid = signupUsername.isValid == true
        val isPasswordValid = signupPassword.isValid == true

        if (isEmailValid && isUsernameValid) {
            val email = signupEmail.text.toString()
            val username = signupUsername.text.toString()

            checkUsernameAndEmail(username, email) { isValid, message ->
                if (!isValid) {
                    when (message) {
                        "Username is already taken." -> signupUsername.error = message
                        "Email is already registered." -> signupEmail.error = message
                    }
                }
                signupButton.isEnabled = isNameValid && isPasswordValid && isValid
            }
        } else {
            signupButton.isEnabled = false
        }
    }


    private fun checkUsernameAndEmail(username: String, email: String, callback: (isValid: Boolean, message: String?) -> Unit) {
        reference = FirebaseDatabase.getInstance().getReference("users")

        reference.get().addOnSuccessListener { dataSnapshot ->
            val isUsernameTaken = dataSnapshot.child(username).exists()
            val isEmailTaken = dataSnapshot.children.any { it.child("email").value == email }

            when {
                isUsernameTaken -> callback(false, "Username is already taken.")
                isEmailTaken -> callback(false, "Email is already registered.")
                else -> callback(true, null)
            }
        }.addOnFailureListener {
            callback(false, "Failed to validate data. Please try again.")
        }
    }


}
