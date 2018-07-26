package com.krbyn.passtore

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.AsyncTask
import android.os.Bundle
import android.view.View

import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.View.VISIBLE
import android.widget.Toast
import com.krbyn.passtore.db.PasswordDAO
import com.krbyn.passtore.db.PasswordDataService
import com.krbyn.passtore.sec.LocalPasswordStore

import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A screen that offers authentication via password.
 */
class LoginActivity : AppCompatActivity() {
    @Volatile
    private var authTask: UserLoginTask? = null

    private var failedLoginCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginPassText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {loginPassInputLayout.error = null}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        failedLoginCount = 0
        login_btn.setOnClickListener { attemptLogin() }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        loginPassInputLayout.error = null
        val passwordStr = loginPassText.text.toString()
        showProgress(true)
        authTask = UserLoginTask(passwordStr)
        authTask!!.execute()
    }

    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        login_form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_form.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

        login_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_progress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }

    override fun onBackPressed() {
        authTask?.cancel(true)
        super.onBackPressed()
    }

    private fun fatalAuthenticationError() {
        LocalPasswordStore.logout()
        AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Fatal error! Do you want to clean all password?")
                .setNegativeButton(android.R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    dropPasswords()
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }

    private fun dropPasswords() {
        applicationContext.deleteDatabase(PasswordDAO.DB_NAME)
        LocalPasswordStore.unregister()
        val myIntent = Intent(this, SplashActivity::class.java)
        startActivity(myIntent)
    }

    private fun showResetDialogue() {
        val chars = arrayOf("I acknowledge that all my passwords will be deleted")
        val agree = AtomicBoolean(false)
        AlertDialog.Builder(this)
                .setTitle("Warning")
                //.setMessage("If you reset your master password, all your stored data will be lost")
                .setSingleChoiceItems(chars, -1) { _, _ ->
                    agree.set(!agree.get())
                }
                .setNegativeButton(android.R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    if (agree.get()) {
                        dropPasswords()
                    } else {
                        Toast.makeText(applicationContext, "Not acknowledged!", Toast.LENGTH_SHORT).show()
                    }
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }

    inner class UserLoginTask internal constructor(private val mPassword: String) : AsyncTask<Unit, Unit, Boolean>() {

        override fun doInBackground(vararg params: Unit): Boolean {
            var success = LocalPasswordStore.login(mPassword)
            if (success) {
                try {
                    PasswordDataService.getPasswordDAO(applicationContext).getAllPasswordNames()
                } catch (e: Exception) {
                    fatalAuthenticationError()
                    success = false
                }
            }
            return success
        }

        override fun onPostExecute(success: Boolean) {
            authTask = null
            if (isCancelled) {
                return
            }
            if (success) {
                failedLoginCount = 0
                startActivity(Intent(this@LoginActivity, PassActivity::class.java))
            } else {
                ++failedLoginCount
                if (failedLoginCount > 2) {
                    forgot_password_text.setOnClickListener { showResetDialogue() }
                    forgot_password_text.visibility = VISIBLE
                }
                loginPassInputLayout.error = "Wrong password"
                showProgress(false)
            }
        }

        override fun onCancelled() {
            authTask = null
            showProgress(false)
        }
    }
}
