package com.krbyn.passtore

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.krbyn.passtore.sec.LocalPasswordStore


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (!LocalPasswordStore.isRegistered()) {
            startActivity(Intent(this, RegisterActivity::class.java))
        } else  if (!LocalPasswordStore.isLoggedIn()){
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            startActivity(Intent(this, PassActivity::class.java))
        }
    }
}
