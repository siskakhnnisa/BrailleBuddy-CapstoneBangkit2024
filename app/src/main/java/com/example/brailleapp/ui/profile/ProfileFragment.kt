package com.example.brailleapp.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.brailleapp.R
import com.example.brailleapp.ui.signin.SigninActivity
import com.example.brailleapp.utils.NightMode
import java.util.Locale

class ProfileFragment : Fragment() {

    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileUsername: TextView // Jika ingin menampilkan username

    companion object {
        const val REQUEST_CODE_EDIT_PROFILE = 1 // Kode request untuk edit profile
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileName = view.findViewById(R.id.tv_name)
        profileEmail = view.findViewById(R.id.tv_email)
//        profileUsername = view.findViewById(R.id.tv_username)

        loadUserProfile()

        return view
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        val darkModeSwitch: Switch = view.findViewById(R.id.switch_dark_mode)
        darkModeSwitch.isChecked = isNightModeEnabled()
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            setNightMode(isChecked)
        }

        val changePasswordTextView: TextView = view.findViewById(R.id.tv_changepassword)
        changePasswordTextView.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java).apply {
                val sharedPref = activity?.getSharedPreferences("userDetails", Context.MODE_PRIVATE)
                putExtra("name", sharedPref?.getString("name", ""))
                putExtra("email", sharedPref?.getString("email", ""))
                putExtra("username", sharedPref?.getString("username", ""))
                putExtra("password", sharedPref?.getString("password", ""))
            }
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE)
        }

        val languageTextView: TextView = view.findViewById(R.id.tv_language)
        languageTextView.setOnClickListener {
            languageTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple11))
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
        }

        val signOutTextView: TextView = view.findViewById(R.id.tv_logout)
        signOutTextView.setOnClickListener {
            signOutTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple11))
            val sharedPref = activity?.getSharedPreferences("userDetails", Context.MODE_PRIVATE)
            with(sharedPref?.edit()) {
                this?.clear()
                this?.apply()
            }
            val loginPref = activity?.getSharedPreferences("loginStatus", Context.MODE_PRIVATE)
            with(loginPref?.edit()) {
                this?.putBoolean("isLoggedIn", false)
                this?.apply()
            }
            val intent = Intent(activity, SigninActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun loadUserProfile() {
        val sharedPref = activity?.getSharedPreferences("userDetails", Context.MODE_PRIVATE)
        profileName.text = sharedPref?.getString("name", "")
        profileEmail.text = sharedPref?.getString("email", "")
//        profileUsername.text = sharedPref?.getString("username", "")
    }

    private fun isNightModeEnabled(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val nightModePref = sharedPreferences.getString(getString(R.string.pref_key_dark), NightMode.AUTO.name)
        val nightMode = NightMode.valueOf(nightModePref!!.toUpperCase(Locale.getDefault())).value
        return nightMode == AppCompatDelegate.MODE_NIGHT_YES
    }

    private fun setNightMode(isEnabled: Boolean) {
        val nightMode = if (isEnabled) {
            NightMode.ON.value
        } else {
            NightMode.OFF.value
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferences.edit().putString(getString(R.string.pref_key_dark), if (isEnabled) NightMode.ON.name else NightMode.OFF.name).apply()
        activity?.recreate()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == AppCompatActivity.RESULT_OK) {
            loadUserProfile()
        }
    }
}
