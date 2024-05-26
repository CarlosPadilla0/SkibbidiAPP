package com.example.padilla.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.padilla.databinding.ItemTarjetasBinding
import com.example.padilla.databinding.ListaTransaccionesBinding
import com.example.padilla.databinding.TransaccionItemBinding

class TarjetasViewHolder(view: View): RecyclerView.ViewHolder(view){

    val binding = ItemTarjetasBinding.bind(view)
    fun render(
        TarjetasModel: Tarjetas,
        onclickListener: (Tarjetas) -> Unit,
        onClickDelete: (Int) -> Unit
    ){
        binding.txtTerminacionItem.text = TarjetasModel.terminacion
        binding.txtProveedorItem.text = TarjetasModel.proveedor
        itemView.setOnClickListener {onclickListener(TarjetasModel)}
        binding.btnEliminarTarjetaItem.setOnClickListener { onClickDelete(adapterPosition) }
    }
}