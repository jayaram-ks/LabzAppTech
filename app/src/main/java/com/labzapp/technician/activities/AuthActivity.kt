package com.labzapp.technician.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.labzapp.technician.R
import com.labzapp.technician.databinding.ActivityAuthBinding
import com.labzapp.technician.storage.SharedPrefManager
import com.labzapp.technician.ui.auth.LoginActivityViewModel
import com.labzapp.technician.utils.toastz

class AuthActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityAuthBinding
    private lateinit var viewModel: LoginActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)

        viewModel.response.observe(this, Observer {
            if (it != null){
                if (it.code == 200) {
                    val api_tokn = it.api_token.toString()
                    val auth_ky  = it.auth_key.toString()
                    SharedPrefManager.getInstance(applicationContext).saveCredentials(api_tokn,auth_ky)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    toastz(this,it.message.toString())
                }
            }
        })

        binding.loginBtn.setOnClickListener {
            val mobile = binding.mobileNumber.editText?.text.toString()
            val authcode = binding.authCode.editText?.text.toString()
            viewModel.loginTechnician(mobile,authcode)
        }
    }

    override fun onStart() {
        super.onStart()
        if(SharedPrefManager.getInstance(this).isLoggedIn){
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

}