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
    private lateinit var binding: ListaTransaccionesBinding
    private lateinit var adapter: AdapterTransacciones
    private var transaccionesMutableList:MutableList<Transaccion> = transaccionProvider.transaccionesList.toMutableList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListaTransaccionesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        getTransaccionData()
    }

    private fun init() {
        db = FirebaseFirestore.getInstance()
        val manager =LinearLayoutManager(this)
        val decoration= DividerItemDecoration(this,manager.orientation)
        adapter = AdapterTransacciones(transaccionesList = transaccionesMutableList,
            onclickListener = { Transaccion -> onitemSelected(Transaccion) },
            onClickDelete = {position -> onDeleteItem(position) },)

        binding.recyclerViewTransacciones.layoutManager = manager
        binding.recyclerViewTransacciones.adapter = adapter
        binding.recyclerViewTransacciones.addItemDecoration(decoration)
    }

    private fun onitemSelected(Transaccion: Transaccion) {
        Toast.makeText(this, Transaccion.fecha, Toast.LENGTH_SHORT).show()
    }

    private fun onDeleteItem(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Transacción")
            .setMessage("¿Estás seguro de que quieres eliminar esta transacción?")
            .setPositiveButton("Sí") { dialog, which ->

                val transaccion = transaccionesMutableList[position]

                val transaccionRef = db.collection("Transacciones")
                    .whereEqualTo("Fecha", transaccion.fecha)
                    .whereEqualTo("monto", transaccion.monto)
                    .whereEqualTo("Nota", transaccion.nota)
                    .whereEqualTo("Tipo", transaccion.tipo)
                    .limit(1)

                transaccionRef.get().addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val document = result.documents[0]
                        db.collection("Transacciones").document(document.id).delete()
                            .addOnSuccessListener {
                                transaccionesMutableList.removeAt(position)
                                adapter.notifyItemRemoved(position)
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error deleting document", e)
                            }
                    }
                }.addOnFailureListener { e ->
                    Log.w(TAG, "Error getting documents", e)
                }
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getTransaccionData() {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        db = FirebaseFirestore.getInstance()
        if (currentUser != null) {
            db.collection("Transacciones").whereEqualTo("Usuario", currentUser.uid).get()
                .addOnSuccessListener { result ->
                    transaccionesMutableList.clear()

                    for (document in result) {
                        val fecha = document.getString("Fecha") ?: ""
                        val monto = document.getString("monto") ?: ""
                        val nota = document.getString("Nota") ?: ""
                        val tipo = document.getString("Tipo") ?: ""
                        var forma = document.getString("FormaPago") ?: ""
                        if (forma == "Tarjeta") {
                            forma+=" "+document.getString("TerminacionTarjeta").toString().substringAfter(" ")
                        }

                        val transaccion = Transaccion(fecha, monto, nota, tipo, forma)
                        transaccionesMutableList.add(transaccion)
                    }
                    binding.recyclerViewTransacciones.adapter = adapter
                    adapter.notifyDataSetChanged()

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

