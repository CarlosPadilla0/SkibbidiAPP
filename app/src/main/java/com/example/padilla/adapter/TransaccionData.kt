package com.example.padilla.adapter

data class Transaccion(
    var fecha: String ?= null,
    var monto: String ?= null,
    var nota: String ?= null,
    var tipo:String ?= null,
    var tarjeta:String ?= null)
