package com.example.padilla

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class listaTransacciones : ComponentActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var items: RecyclerView
    private lateinit var transactions: ArrayList<TransaccionData>
    private lateinit var adapter: AdapterTransacciones

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lista_transacciones)
        init()
        getTransaccionData()
    }

    private fun init() {
        db = FirebaseFirestore.getInstance()
        items = findViewById(R.id.recyclerViewTransacciones)
        items.layoutManager = LinearLayoutManager(this)
        items.setHasFixedSize(true)
        transactions = arrayListOf()
        adapter = AdapterTransacciones(transactions)
        items.adapter = adapter
    }

    private fun getTransaccionData() {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        db = FirebaseFirestore.getInstance()
        if (currentUser != null) {
            db.collection("Transacciones").whereEqualTo("Usuario", currentUser.uid).get().addOnSuccessListener { result ->
                    transactions.clear()


                for (document in result) {
                        val fecha = document.getString("Fecha") ?: ""
                        val monto = document.getString("monto") ?: ""
                        val nota = document.getString("Nota") ?: ""
                        val tipo = document.getString("Tipo") ?: ""
                        val transaccion = TransaccionData(fecha, monto, nota, tipo)
                        transactions.add(transaccion)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                    // Mostrar un mensaje de error al usuario
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

