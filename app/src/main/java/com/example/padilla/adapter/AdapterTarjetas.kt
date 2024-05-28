package com.example.padilla.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.padilla.R

class AdapterTarjetas (private var TarjetasList: List<Tarjetas>,
                       private val onclickListener: (Tarjetas) -> Unit,
                       private val onClickDelete:(Int) -> Unit
): RecyclerView.Adapter<TarjetasViewHolder>(){
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarjetasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TarjetasViewHolder(layoutInflater.inflate(R.layout.item_tarjetas, parent, false))
    }

    override fun getItemCount(): Int {
        return TarjetasList.size
    }

    override fun onBindViewHolder(holder: TarjetasViewHolder, position: Int) {
        val item = TarjetasList[position]
        holder.render(item,onclickListener,onClickDelete)
    }

    fun updateTarjetas(tarjetasList: List<Tarjetas>){
        this.TarjetasList=tarjetasList
        notifyDataSetChanged()
    }


}