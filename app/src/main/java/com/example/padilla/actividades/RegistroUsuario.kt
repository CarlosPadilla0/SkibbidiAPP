package com.example.padilla.actividades

import android.graphics.Color
import android.os.Bundle
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
import com.example.padilla.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegistroUsuario : AppCompatActivity(), View.OnClickListener {
    private lateinit var txtNombre: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtContra: EditText
    private lateinit var txtConfContra: EditText
    private lateinit var radioHombre: RadioButton
    private lateinit var radioMujer: RadioButton
    private lateinit var btnRegistrar: Button
    private lateinit var msgToast: Toast
    private lateinit var db: FirebaseFirestore
    private val activityScope = CoroutineScope(Dispatchers.Main)

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

        init ()
    }

    private fun init() {
        txtNombre = findViewById(R.id.inputNombreRegistro)
        txtCorreo = findViewById(R.id.inputCorreoRegistro)
        txtContra = findViewById(R.id.inputContraseñaRegistro)
        txtConfContra = findViewById(R.id.inputContraseñaConfirmacionRegistro)
        radioHombre = findViewById(R.id.RadioHombre)
        radioMujer = findViewById(R.id.RadioMujer)
        btnRegistrar = findViewById(R.id.btnConfirmarRegistro)

        radioHombre.setOnClickListener(this)
        radioMujer.setOnClickListener(this)
        btnRegistrar.setOnClickListener(this)

        msgToast = Toast.makeText(this, "", Toast.LENGTH_LONG)
        db = FirebaseFirestore.getInstance()
    }

    override fun onClick(v: View) {

        if (v == btnRegistrar) {

            if (!validate()) {
                msgToast.setText("Faltan campos por llenar")
                msgToast.show()
                return

            }
            if (!validate2()) {
                msgToast.setText("Las Contraseñas no son iguales")
                msgToast.show()
                return
            }
            registrar()

        }
    }

    private fun validate(): Boolean {
        val nombre = txtNombre.text.toString().trim { it <= ' ' }
        val correo = txtCorreo.text.toString().trim { it <= ' ' }
        val contra = txtContra.text.toString().trim { it <= ' ' }
        val confContra = txtConfContra.text.toString().trim { it <= ' ' }
        return nombre.isNotEmpty() && correo.isNotEmpty() && contra.isNotEmpty() && confContra.isNotEmpty() && (radioHombre.isChecked || radioMujer.isChecked)
    }

    private fun validate2(): Boolean {
        val contra = txtContra.text.toString().trim()
        val confContra = txtConfContra.text.toString().trim()
        return contra == confContra
    }

    private fun registrar() {
        val correo = txtCorreo.text.toString()
        val contra = txtConfContra.text.toString()
        val nombre = txtNombre.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, contra)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val mAuth = FirebaseAuth.getInstance()
                    val currentUser = mAuth.currentUser

                    if (currentUser != null) {
                        db.collection("Users").document(currentUser.uid).set(
                            hashMapOf(
                                "Email" to correo,
                                "Ahorro" to "0",
                                "Meta" to "0",
                                "Gastos" to "0",
                                "Name" to nombre,
                                "Sexo" to if (radioHombre.isChecked) "Hombre" else "Mujer"
                            )
                        ).addOnSuccessListener {
                            activityScope.launch {
                                delay(2000) // Retraso de 2 segundos
                                showAlert("Registro exitoso")
                                finish()
                            }
                        }.addOnFailureListener { exception ->
                            showAlert("Error al registrar en Firestore: ${exception.message}")
                        }
                        msgToast.setText("Registro exitoso")
                        msgToast.show()
                        limpiar()
                    } else {
                        showAlert("Error al registrar. Revisa los datos.")
                    }
                } else {
                    showAlert("Error al registrar. ${task.exception?.message}")
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
    private fun limpiar() {
        txtNombre.text.clear()
        txtContra.text.clear()
        txtCorreo.text.clear()
        txtConfContra.text.clear()
        radioHombre.isChecked = false
        radioMujer.isChecked = false
    }

}