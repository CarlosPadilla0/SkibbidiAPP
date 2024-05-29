package com.example.padilla.adapter

class TransaccionProvider {
    companion object {
        val transaccionesList = mutableListOf<Transaccion>()
        fun agregarTransaccion(transaccion: Transaccion)  {
            transaccionesList.add(transaccion)
        }
    }

}