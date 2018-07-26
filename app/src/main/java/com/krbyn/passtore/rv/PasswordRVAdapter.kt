package com.krbyn.passtore.rv

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.krbyn.passtore.pass.SimplePassword
import android.view.LayoutInflater
import com.krbyn.passtore.R


class PasswordRVAdapter(private val passwords: List<SimplePassword>): RecyclerView.Adapter<PassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.password_layout, parent, false)
        return PassViewHolder(v)
    }

    override fun getItemCount(): Int {
        return passwords.size
    }

    override fun onBindViewHolder(holder: PassViewHolder, position: Int) {
        holder.name.text = passwords[position].name
        android.R.drawable.spinner_background
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }
}