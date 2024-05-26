package com.example.padilla.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.padilla.databinding.TransaccionItemBinding


class transaccionesViewHolder(view: View): RecyclerView.ViewHolder(view){

    val binding = TransaccionItemBinding.bind(view)
    fun render(
        transaccionModel: Transaccion,
        onclickListener: (Transaccion) -> Unit,
        onClickDelete: (Int) -> Unit
    ){
        binding.txtFechaItem.text = transaccionModel.fecha
        binding.txtMontoItem.text = transaccionModel.monto
        binding.txtNotaItem.text = transaccionModel.nota
        binding.txtTipoTransaccionItem.text = transaccionModel.tipo
        binding.txtTarjetaItem.text = transaccionModel.tarjeta
        itemView.setOnClickListener {onclickListener(transaccionModel)}
        binding.buttonEliminartransaccion.setOnClickListener { onClickDelete(adapterPosition) }
    }
}