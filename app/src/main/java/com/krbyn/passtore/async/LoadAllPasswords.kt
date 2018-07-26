package com.krbyn.passtore.async

import android.os.AsyncTask
import com.krbyn.passtore.db.PasswordDAO
import java.util.concurrent.atomic.AtomicBoolean

class LoadAllPasswords(
        private val dao: PasswordDAO,
        private val callback: LoadCallback): AsyncTask<Unit, Int, AllPasswordResult>() {

    private val canceled = AtomicBoolean(false)

    override fun doInBackground(vararg params: Unit?): AllPasswordResult {
        return try {
            val passwords = dao.getAllPasswordNames()
            if (canceled.get()) {
                AllPasswordResult()
            } else {
                AllPasswordResult(passwords)
            }
        } catch (e: Exception) {
            AllPasswordResult(e.message?: "Unknown message", e)
        }
    }

    override fun onPostExecute(result: AllPasswordResult) {
        if (!canceled.get()) {
            callback.onPasswordsLoaded(result)
        }
    }

    override fun onCancelled(result: AllPasswordResult) {
        canceled.set(true)
    }

    override fun onCancelled() {
        canceled.set(true)
    }

    interface LoadCallback {
        fun onPasswordsLoaded(result: AllPasswordResult)
    }
}