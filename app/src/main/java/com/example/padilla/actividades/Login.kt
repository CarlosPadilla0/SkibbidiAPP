package com.example.padilla.actividades

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.padilla.R
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity(), View.OnClickListener {
    private lateinit var btnIniciarSesion: Button
    private lateinit var btnRegistrarse: Button
    private lateinit var txtCorreo: EditText
    private lateinit var txtPassword: EditText
    private lateinit var msgToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.actividad_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
    }

    private fun init() {
        txtCorreo = findViewById(R.id.inputCorreoInicio)
        txtPassword = findViewById(R.id.inputContraseñaInicio)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesionInicio)
        btnRegistrarse = findViewById(R.id.btnRegistrarseInicio)
        btnRegistrarse.setOnClickListener(this)
        btnIniciarSesion.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnRegistrarseInicio -> {
                val intent = Intent(this, RegistroUsuario::class.java)
                startActivity(intent)
            }
            R.id.btnIniciarSesionInicio -> {
                if (validate()) {
                    signIn()
                } else {
                    showAlert("Por favor, verifica los datos.")
                }
            }
        }
    }

    private fun validate(): Boolean {
        val correo = txtCorreo.text.toString().trim()
        val contra = txtPassword.text.toString().trim()

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val correoValido = correo.matches(emailPattern.toRegex())

        return correo.isNotEmpty() && contra.isNotEmpty() && correoValido
    }

    private fun signIn() {
        val correo = txtCorreo.text.toString()
        val contra = txtPassword.text.toString()

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(correo, contra)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, DashBoard::class.java)
                    startActivity(intent)
                    txtCorreo.text.clear()
                    txtPassword.text.clear()
                    finish()
                } else {
                    showAlert("Error al iniciar sesión. Por favor, verifica los datos.")
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("¿Desea salir de BBVO?")
            builder.setPositiveButton("Sí") { _, which ->
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
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
}
