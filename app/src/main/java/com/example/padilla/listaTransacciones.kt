package com.example.padilla

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.padilla.adapter.AdapterTransacciones
import com.example.padilla.adapter.Transaccion
import com.example.padilla.adapter.transaccionProvider
import com.example.padilla.databinding.ListaTransaccionesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class listaTransacciones : ComponentActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var transactions: ArrayList<Transaccion>
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ListaTransaccionesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListaTransaccionesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        getTransaccionData()
    }

    private fun init() {
        val manager =LinearLayoutManager(this)
        val decoration= DividerItemDecoration(this,manager.orientation)
        db = FirebaseFirestore.getInstance()
        binding.recyclerViewTransacciones.layoutManager = manager
        binding.recyclerViewTransacciones.adapter =
            AdapterTransacciones(transaccionProvider.transaccionesList) { Transaccion ->
                onitemSelected(
                    Transaccion
                )
            }
        transactions = arrayListOf()
        binding.recyclerViewTransacciones.addItemDecoration(decoration)
    }

    fun onitemSelected(Transaccion: Transaccion) {
        Toast.makeText(this, Transaccion.tipo, Toast.LENGTH_SHORT).show()
    }

    private fun getTransaccionData() {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        db = FirebaseFirestore.getInstance()
        if (currentUser != null) {
            db.collection("Transacciones").whereEqualTo("Usuario", currentUser.uid).get()
                .addOnSuccessListener { result ->
                    transactions.clear()


                    for (document in result) {
                        val fecha = document.getString("Fecha") ?: ""
                        val monto = document.getString("monto") ?: ""
                        val nota = document.getString("Nota") ?: ""
                        val tipo = document.getString("Tipo") ?: ""
                        val transaccion = Transaccion(fecha, monto, nota, tipo, "1234")
                        transactions.add(transaccion)
                    }
                    binding.recyclerViewTransacciones.adapter =
                        AdapterTransacciones(transactions) { Transaccion ->
                            onitemSelected(
                                Transaccion
                            )
                        }

                }
        }
    }
    private fun showAlert(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Aviso")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}

