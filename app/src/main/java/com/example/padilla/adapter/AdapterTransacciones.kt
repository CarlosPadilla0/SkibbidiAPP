package com.example.padilla.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.padilla.R

class AdapterTransacciones(private var transaccionesList: List<Transaccion>,
                           private val onclickListener: (Transaccion) -> Unit,
                           private val onClickDelete:(Int) -> Unit
                            ): RecyclerView.Adapter<transaccionesViewHolder>(){
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): transaccionesViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
        return transaccionesViewHolder(layoutInflater.inflate(R.layout.transaccion_item, parent, false))
    }

    override fun getItemCount(): Int {
       return transaccionesList.size
    }

    override fun onBindViewHolder(holder: transaccionesViewHolder, position: Int) {
        val item = transaccionesList[position]
        holder.render(item,onclickListener,onClickDelete)
    }

    fun updateTransacciones(transaccionesList: List<Transaccion>){
        this.transaccionesList = transaccionesList
        notifyDataSetChanged()
    }


}