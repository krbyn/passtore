package com.krbyn.passtore

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.krbyn.passtore.async.AllPasswordResult
import com.krbyn.passtore.async.LoadAllPasswords
import com.krbyn.passtore.db.PasswordDataService
import com.krbyn.passtore.rv.PasswordRVAdapter

/**
 * A fragment containing RecyclerView with Passwords
 */
class PassActivityFragment : Fragment(), LoadAllPasswords.LoadCallback {
    private lateinit var rv: RecyclerView
    private lateinit var load: View

    override fun onPasswordsLoaded(result: AllPasswordResult) {
        if (result.calceled) {
            return
        }
        if (result.failed) {
            Toast.makeText(context, "Failed to laod password", Toast.LENGTH_SHORT).show()
            return
        }
        rv.adapter = PasswordRVAdapter(result.passwords)
        load.visibility = View.GONE
        rv.visibility = View.VISIBLE
        rv.adapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val frag = inflater.inflate(R.layout.fragment_pass, container, false)
        rv = frag.findViewById(R.id.password_rv)
        rv.layoutManager = LinearLayoutManager(context)
        load = frag.findViewById(R.id.load_progress)
        context?:return frag
        LoadAllPasswords(PasswordDataService.getPasswordDAO(context!!), this).execute()
        return frag
    }
}
