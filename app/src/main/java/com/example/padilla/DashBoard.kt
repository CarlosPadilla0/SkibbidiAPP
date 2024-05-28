package com.example.padilla

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.example.padilla.databinding.DashboardBinding
import com.getbase.floatingactionbutton.FloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashBoard : ComponentActivity(),OnClickListener {
    private lateinit var txtBarra: TextView
    private lateinit var txtAhorro: TextView
    private lateinit var txtMeta: TextView
    private lateinit var txtGastos: TextView
    private lateinit var db: FirebaseFirestore
    private lateinit var fabModificarMeta: FloatingActionButton
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var fabTarjeta: FloatingActionButton
    private lateinit var fabMenu: FloatingActionsMenu
    private lateinit var container: View
    private lateinit var fabTransacciones: FloatingActionButton
    private lateinit var binding: DashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        actualizarGastos()
        actualizarAhorro()
        configSwipe()
    }

    private fun configSwipe() {
        binding.Swipe.setColorSchemeResources(R.color.Java)
        binding.Swipe.setOnRefreshListener {
            binding.Swipe.isRefreshing = false
            loadData()
            actualizarGastos()
            actualizarAhorro()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("¿Desea cerrar sesión?")
            builder.setPositiveButton("Sí") { dialog, which ->
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, actividad_login::class.java)
                startActivity(intent)
                finish()
            }
            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun init() {
        txtBarra = findViewById(R.id.Barra) ?: return
        txtMeta = findViewById(R.id.TxtMeta)
        txtAhorro = findViewById(R.id.TxtAhorro)
        txtGastos = findViewById(R.id.TxtGastos)
        fabMenu = findViewById(R.id.fab_menu)
        fabAgregar = findViewById(R.id.fab_Agregar)
        fabTarjeta = findViewById(R.id.fab_Tarjeta)
        fabModificarMeta = findViewById(R.id.fab_modificar)
        fabAgregar.setOnClickListener(this)
        fabTarjeta.setOnClickListener(this)
        fabModificarMeta.setOnClickListener(this)
        fabMenu.setOnClickListener(this)
        container= findViewById(android.R.id.content)
        container.setOnClickListener(this)
        fabTransacciones = findViewById(R.id.fab_Transacciones)
        fabTransacciones.setOnClickListener(this)
        db = FirebaseFirestore.getInstance()
        loadData()
    }

    private fun loadData() {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val userEmail = currentUser?.email
        if (userEmail != null) {
            db.collection("Users").document(currentUser.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    val userNameString = documentSnapshot.getString("Name")
                    val formattedName = "Bienvenido " + userNameString
                    txtBarra.text = formattedName
                    val metaValue = documentSnapshot.getString("Meta")
                        val meta = metaValue
                        val ahorro = documentSnapshot.getString("Ahorro")
                        val gastos = documentSnapshot.getString("Gastos")
                        txtMeta.text = meta
                        txtAhorro.text = ahorro
                        txtGastos.text = gastos

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

    override fun onClick(v: View?) {
        if(v==container){
            fabMenu.collapse()
        }
        if (v == fabAgregar) {
            val intentAgregar = Intent(this, Agregartransaccion::class.java)
            startActivity(intentAgregar)
            return
        }
        if (v == fabModificarMeta) {
           val intentModificarMeta = Intent(this, ModificarMeta::class.java)
            startActivity(intentModificarMeta)
            return
        }
        if (v == fabTarjeta){
            val intentTarjeta = Intent(this, Agregar_tarjetas::class.java)
            startActivity(intentTarjeta)
            return


        }
        if(v== fabTransacciones){
            val intentModificar = Intent(this,listaTransacciones::class.java)
            startActivity(intentModificar)
        }


    }
    @SuppressLint("SuspiciousIndentation")
    private fun actualizarGastos() {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

            db.collection("Transacciones")
                .whereEqualTo("Usuario", currentUser?.uid)
                .whereEqualTo("Tipo", "Egreso")
                .get()
                .addOnSuccessListener { result ->
                    var totalGastos = 0
                    for (document in result) {
                        val monto = document.getString("monto")?.toIntOrNull() ?: 0
                        totalGastos += monto
                    }
                    txtGastos.text = totalGastos.toString()
                }
                .addOnFailureListener { e ->
                    showAlert("Error al obtener los gastos: ${e.message}")
                }

    }
    private fun actualizarAhorro() {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        db.collection("Transacciones")
            .whereEqualTo("Usuario", currentUser?.uid)
            .get()
            .addOnSuccessListener { result ->
                var totalIngresos = 0
                var totalGastos = 0
                for (document in result) {
                    val tipo = document.getString("Tipo")
                    val monto = document.getString("monto")?.toIntOrNull() ?: 0
                    if (tipo == "Ingreso") {
                        totalIngresos += monto
                    } else if (tipo == "Egreso") {
                        totalGastos += monto
                    }
                }
                val ahorro = totalIngresos - totalGastos
                txtAhorro.text = ahorro.toString()

                // Actualizar el valor de ahorro en Firebase
                val userRef = db.collection("Users").document(currentUser!!.uid)
                userRef.update("Ahorro", ahorro.toString())
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener { e ->
                        showAlert("Error al actualizar el ahorro: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                showAlert("Error al obtener las transacciones: ${e.message}")
            }
    }

}