package com.example.padilla

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistroUsuario : AppCompatActivity(), View.OnClickListener {
    private lateinit var txtNombre: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtContraseña: EditText
    private lateinit var txtConfContraseña: EditText
    private lateinit var radioHombre: RadioButton
    private lateinit var radioMujer: RadioButton
    private lateinit var BtnRegistrar: Button
    private lateinit var msgToast: Toast
    private lateinit var db: FirebaseFirestore

    companion object {
        private const val TAG = "RegistroUsuario"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.actividad_registro)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Registro"
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Init()
    }

    private fun Init() {
        txtNombre = findViewById(R.id.inputNombreRegistro)
        txtCorreo = findViewById(R.id.inputCorreoRegistro)
        txtContraseña = findViewById(R.id.inputContraseñaRegistro)
        txtConfContraseña = findViewById(R.id.inputContraseñaConfirmacionRegistro)
        radioHombre = findViewById(R.id.RadioHombre)
        radioMujer = findViewById(R.id.RadioMujer)
        BtnRegistrar = findViewById(R.id.btnConfirmarRegistro)

        radioHombre.setOnClickListener(this)
        radioMujer.setOnClickListener(this)
        BtnRegistrar.setOnClickListener(this)

        msgToast = Toast.makeText(this, "", Toast.LENGTH_LONG)
        db = FirebaseFirestore.getInstance()
    }

    override fun onClick(v: View) {

        if (v == BtnRegistrar) {

            if (!validate()) {
                msgToast.setText("Faltan campos por seleccionar")
                msgToast.show()
                return

            }
            if (!validate2()) {
                msgToast.setText("Las Contraseñas no son iguales")
                msgToast.show()
                return
            }
            registrar()
            msgToast.setText("Registro exitoso")
            msgToast.show()

            txtNombre.text.clear()
            txtContraseña.text.clear()
            txtCorreo.text.clear()
            txtConfContraseña.text.clear()
            radioHombre.clearFocus()
            radioMujer.clearFocus()
        }


    }

    private fun validate(): Boolean {
        val nombre = txtNombre.text.toString().trim { it <= ' ' }
        val correo = txtCorreo.text.toString().trim { it <= ' ' }
        val contraseña = txtContraseña.text.toString().trim { it <= ' ' }
        val confContraseña = txtConfContraseña.text.toString().trim { it <= ' ' }

        return nombre.isNotEmpty() && correo.isNotEmpty() && contraseña.isNotEmpty() && confContraseña.isNotEmpty()
    }

    private fun validate2(): Boolean {
        val contraseña = txtContraseña.text.toString().trim()
        val confContraseña = txtConfContraseña.text.toString().trim()
        return contraseña == confContraseña
    }

    private fun registrar() {
        val correo = txtCorreo.text.toString()
        val contraseña = txtConfContraseña.text.toString()
        val nombre = txtNombre.text.toString()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, contraseña)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Registro exitoso")

                    val mAuth = FirebaseAuth.getInstance()
                    val currentUser = mAuth.currentUser
                    val userEmail = correo
                    val userName = nombre

                    if (currentUser != null) {
                        db.collection("Users").document(currentUser.uid).set(
                            hashMapOf(
                                "Email" to userEmail,
                                "Ahorro" to 0,
                                "Meta" to 0,
                                "Gastos" to 0,
                                "Name" to userName,
                                "Sexo" to if (radioHombre.isChecked) "Hombre" else "Mujer"
                            )
                        )
                    } else {
                        showAlert("Revisa los datos")
                    }
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

}