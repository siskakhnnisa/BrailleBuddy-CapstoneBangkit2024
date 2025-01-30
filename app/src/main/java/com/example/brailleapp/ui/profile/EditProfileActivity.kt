package com.example.brailleapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.brailleapp.R
import com.example.brailleapp.ui.customview.CustomTextInput
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : AppCompatActivity() {

    private lateinit var editName: CustomTextInput
    private lateinit var editUsername: CustomTextInput
    private lateinit var editPassword: CustomTextInput
    private lateinit var saveButton: Button

    private lateinit var nameUser: String
    private lateinit var emailUser: String
    private lateinit var usernameUser: String
    private lateinit var passwordUser: String

    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        reference = FirebaseDatabase.getInstance().getReference("users")

        editName = findViewById(R.id.editName)
        editUsername = findViewById(R.id.editUsername)
        editPassword = findViewById(R.id.editPassword)
        saveButton = findViewById(R.id.saveButton)

        showData()

        saveButton.setOnClickListener {
            var isChanged = false

            if (isNameChanged()) isChanged = true
            if (isPasswordChanged()) isChanged = true

            val newUsername = editUsername.text.toString()
            if (usernameUser != newUsername) {
                reference.child(newUsername).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        // Username sudah terdaftar, tampilkan pesan error
                        showErrorDialog("Username is already taken.")
                    } else {
                        isChanged = true
                        reference.child(usernameUser).removeValue()
                        val updatedData = mapOf(
                            "name" to nameUser,
                            "email" to emailUser,
                            "username" to newUsername,
                            "password" to passwordUser
                        )
                        reference.child(newUsername).setValue(updatedData)
                        usernameUser = newUsername
                        showSuccessDialog()
                    }
                }
            } else {
                if (isChanged) {
                    showSuccessDialog()
                } else {
                    showNoChangesDialog()
                }
            }
        }
    }

    private fun isNameChanged(): Boolean {
        return if (nameUser != editName.text.toString()) {
            val newName = editName.text.toString()
            reference.child(usernameUser).child("name").setValue(newName)
            val sharedPref = getSharedPreferences("userDetails", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("name", newName)
                apply()
            }
            nameUser = newName
            true
        } else {
            false
        }
    }

    private fun isPasswordChanged(): Boolean {
        return if (passwordUser != editPassword.text.toString()) {
            reference.child(usernameUser).child("password").setValue(editPassword.text.toString())
            passwordUser = editPassword.text.toString()
            true
        } else {
            false
        }
    }

    private fun showData() {
        val intent = intent
        nameUser = intent.getStringExtra("name").toString()
        emailUser = intent.getStringExtra("email").toString()
        usernameUser = intent.getStringExtra("username").toString()
        passwordUser = intent.getStringExtra("password").toString()

        editName.setText(nameUser)
        editUsername.setText(usernameUser)
        editPassword.setText(passwordUser)
    }

    private fun showNoChangesDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No Changes Detected")
        builder.setMessage("No changes were made to your profile. What would you like to do?")
        builder.setPositiveButton("Stay") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setNegativeButton("Go Back") { _, _ ->
            finish()
        }
        builder.create().show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showSuccessDialog() {
        Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
        val intent = Intent()
        intent.putExtra("profile_updated", true)
        setResult(RESULT_OK, intent)
        finish()
    }
}
