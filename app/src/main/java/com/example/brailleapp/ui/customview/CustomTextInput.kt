package com.example.brailleapp.ui.customview

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.material.textfield.TextInputEditText
import com.example.brailleapp.R

class CustomTextInput : TextInputEditText, TextWatcher {

    var isValid: Boolean? = null
        set(value) {
            field = value ?: (text?.isNotEmpty() == true)
        }

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(this)
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users")
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
        if (text != null && text?.isNotEmpty() == true) {
            when {
                inputType == InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                    if (length() < 8) {
                        error = context.getString(R.string.password_less_8)
                        isValid = false
                    } else {
                        isValid = true
                    }
                }
                inputType == InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
                    if (!Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()) {
                        error = context.getString(R.string.invalid_email)
                        isValid = false
                    } else {
                        checkEmailAvailability(text.toString()) { available ->
                            if (!available) {
                                error = context.getString(R.string.email_already_registered)
                                isValid = false
                            } else {
                                isValid = true
                            }
                        }
                    }
                }
                inputType == InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_FLAG_CAP_SENTENCES -> {
                    val username = text.toString()
                    if (username.length < 4) {
                        error = context.getString(R.string.username_too_short)
                        isValid = false
                    } else if (!username.matches(Regex("^[a-zA-Z0-9_]*$"))) {
                        error = context.getString(R.string.username_invalid_characters)
                        isValid = false
                    } else {
                        checkUsernameAvailability(username) { available ->
                            if (!available) {
                                error = "Username is already taken"
                                isValid = false
                            } else {
                                isValid = true
                            }
                        }
                    }
                }
                else -> {
                    isValid = true
                }
            }
        } else {
            error = context.getString(R.string.required_field)
            isValid = false
        }
        isValid = isValid == true // Pastikan nilai isValid di-update
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        error = null
    }

    // Fungsi untuk memeriksa ketersediaan username
    private fun checkUsernameAvailability(username: String, callback: (Boolean) -> Unit) {
        reference.child(username).get().addOnSuccessListener {
            callback(!it.exists())
        }.addOnFailureListener {
            callback(false)
        }
    }

    // Fungsi untuk memeriksa ketersediaan email
    private fun checkEmailAvailability(email: String, callback: (Boolean) -> Unit) {
        reference.get().addOnSuccessListener { dataSnapshot ->
            val emailExists = dataSnapshot.children.any { it.child("email").value == email }
            callback(!emailExists)
        }.addOnFailureListener {
            callback(false)
        }
    }
}