package com.example.padilla.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.padilla.R

class AdapterTransacciones(private val transaccionesList: List<Transaccion>, private val onclickListener: (Transaccion) -> Unit): RecyclerView.Adapter<transaccionesViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): transaccionesViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
        return transaccionesViewHolder(layoutInflater.inflate(R.layout.transaccion_item, parent, false))
    }

    override fun getItemCount(): Int {
       return transaccionesList.size
    }

    override fun onBindViewHolder(holder: transaccionesViewHolder, position: Int) {
        val item = transaccionesList[position]
        holder.render(item,onclickListener)
    }


}