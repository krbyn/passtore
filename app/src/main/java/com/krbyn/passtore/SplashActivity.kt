package com.krbyn.passtore

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*val password = PasswordService.getPasswordManager().getPassword()
        if (password == null) {
            showLoginActivity()
        } else {
            showPassActivity()
        }*/
        setContentView(R.layout.activity_splash)
    }

    private fun showLoginActivity() {

    }

    private fun showPassActivity() {

    }
}
