package com.example.padilla

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
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
    private lateinit var  container: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)
        init()
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
        loadData()
    }

    private fun loadData() {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        db = FirebaseFirestore.getInstance()
        val userEmail = currentUser?.email
        if (userEmail != null) {
            db.collection("Users").document(currentUser.uid).get().addOnSuccessListener {
                val userNameString = it.get("Name") as String?
                val formattedName = "Bienvenido " + userNameString
                txtBarra.text = formattedName
                val meta = it.getLong("Meta")?.toString() ?: ""
                val ahorro = it.getLong("Ahorro")?.toString() ?: ""
                val gastos = it.getLong("Gastos")?.toString() ?: ""

                txtMeta.text = meta
                txtAhorro.text = ahorro
                txtGastos.text = gastos
            }
        }
    }

    private fun showAlert(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
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
            val intentAgregar = Intent(this, AgregarTransaccion::class.java)
            startActivity(intentAgregar)
            return
        }
        if (v == fabTarjeta) {
//            val intentBorrar = Intent(this, BorrarCuenta::class.java)
//            startActivity(intentBorrar)
//            return
        }
        if (v == fabModificarMeta){


        }

    }


}