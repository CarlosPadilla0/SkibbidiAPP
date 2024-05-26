package com.example.padilla.adapter

class tarjetasProvider {
    companion object {
        val tarjetasList = mutableListOf<Tarjetas>()
        fun agregarTarjetas(tarjetas: Tarjetas) {
            tarjetasList.add(tarjetas)
        }
    }
}