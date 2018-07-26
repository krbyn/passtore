package com.krbyn.passtore.rv

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.krbyn.passtore.R

class PassViewHolder(item: View): RecyclerView.ViewHolder(item) {
    val name: TextView = item.findViewById(R.id.password_name)
}