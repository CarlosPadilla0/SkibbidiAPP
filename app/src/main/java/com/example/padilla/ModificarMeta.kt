package com.example.padilla

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ModificarMeta : ComponentActivity(),OnClickListener{
    private lateinit var metaActual: TextView
    private lateinit var btnGuardar: Button
    private lateinit var txtmeta: EditText
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_meta)
        init()
        fill()
    }

    private fun init() {
        metaActual= findViewById(R.id.outputmeta)
        btnGuardar= findViewById(R.id.btnGuardarMeta)
        txtmeta= findViewById(R.id.txtmetaNueva)
        btnGuardar.setOnClickListener(this)
    }
    private fun fill() {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        db = FirebaseFirestore.getInstance()
        if (currentUser != null) {
            db.collection("Users").document(currentUser.uid).get().addOnSuccessListener { document ->
                val userMeta = document.getString("Meta")
                if (userMeta != null)
                    metaActual.text = userMeta
                metaActual.setText(userMeta)
            }
        }
    }

    override fun onClick(v: View?) {
        if(v == btnGuardar){
            if(!validar()){
                showAlert("Todos los campos necesitan estar llenos")
                return
            }
            guardar()
        }
    }
    private fun validar():Boolean{
        return txtmeta.text.isNotEmpty()
    }
    private fun guardar() {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        db = FirebaseFirestore.getInstance()
        if (currentUser != null) {
            db.collection("Users").document(currentUser.uid).update("Meta", txtmeta.text.toString()).addOnSuccessListener {
                showAlert("La meta ha sido actualizada")
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
