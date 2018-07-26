package com.krbyn.passtore

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.krbyn.passtore.sec.LocalPasswordStore
import kotlinx.android.synthetic.main.activity_register.*
import android.widget.EditText
import com.krbyn.passtore.db.PasswordDataService

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        editText.addTextChangedListener(passTextWatcher)
        editText2.addTextChangedListener(passRepeatWatcher)
        editText2.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                    Handler().post {
                        register_screen_scroll_view.scrollTo(0, button.bottom)
                    }
            }
        }
        button.setOnClickListener {
            passTextWatcher.afterTextChanged(editText.editableText)
            passRepeatWatcher.afterTextChanged(editText2.editableText)
            when {
                textInputLayout.error != null -> Toast.makeText(applicationContext, textInputLayout.error, Toast.LENGTH_SHORT).show()
                textInputLayout2.error != null -> Toast.makeText(applicationContext, textInputLayout2.error, Toast.LENGTH_SHORT).show()
                else -> {
                    disableEditText(editText)
                    disableEditText(editText2)
                    LocalPasswordStore.register(editText.text.toString())
                    PasswordDataService.getPasswordDAO(applicationContext)
                    val myIntent = Intent(this, PassActivity::class.java)
                    startActivity(myIntent)
                }
            }
        }
    }

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null
        editText.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_register, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.register_help -> showHelpDialog()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showHelpDialog(): Boolean {
        AlertDialog.Builder(this)
                .setTitle("Information")
                .setMessage("Create a password store encrypted with a given password.\nYou will not be able to access you data if you lose the password")
                .setNeutralButton(android.R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    private val passTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (s == null || s.length < 6) {
                textInputLayout.error = "Password is short"
            } else {
                textInputLayout.error = null
            }
        }
    }

    private val passRepeatWatcher= object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (editText.text?.toString()?.equals(s.toString()) != true) {
                textInputLayout2.error = "Passwords should match"
            } else {
                textInputLayout2.error = null
            }
        }
    }
}
