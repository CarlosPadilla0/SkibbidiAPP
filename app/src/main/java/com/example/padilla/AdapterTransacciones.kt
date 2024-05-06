package com.example.padilla

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterTransacciones(private val  listaTransacciones : ArrayList<TransaccionData>) : RecyclerView.Adapter<AdapterTransacciones.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaccion_item,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder:MyViewHolder, position: Int) {
        val currentitem = listaTransacciones[position]

        holder.fecha.text = currentitem.fecha
        holder.monto.text = currentitem.monto
        holder.nota.text = currentitem.nota
        holder.tipo.text = currentitem.tipo


    }

    override fun getItemCount(): Int {
        return listaTransacciones.size
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fecha:TextView = itemView.findViewById(R.id.txtFechaItem)
        val monto:TextView = itemView.findViewById(R.id.txtMontoItem)
        val nota:TextView = itemView.findViewById(R.id.txtNotaItem)
        var tipo:TextView = itemView.findViewById(R.id.txtTipoTransaccion)
    }


}