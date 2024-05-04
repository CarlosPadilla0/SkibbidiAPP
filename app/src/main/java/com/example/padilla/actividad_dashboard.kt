package com.example.padilla

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.AppBarConfiguration.Builder
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.padilla.databinding.ActividadDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

class actividad_dashboard : AppCompatActivity() {
    private lateinit var txtMeta: TextView
    private var binding: ActividadDashboardBinding? = null
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActividadDashboardBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        val appBarConfiguration: AppBarConfiguration = Builder(
            R.id.navigation_dashboard, R.id.navigation_home, R.id.navigation_notifications
        )
            .build()
        val navController = findNavController(this, R.id.nav_host_fragment_actividad_dashboard)
        setupWithNavController(binding!!.navView, navController)
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
                finish() // Cerrar la actividad actual
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
        txtMeta = findViewById(R.id.textMeta) ?: return
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        db = FirebaseFirestore.getInstance()
        val userEmail = currentUser?.email
        if (userEmail != null) {
            db.collection("Users").document(userEmail).get().addOnSuccessListener {
                txtMeta.text = it.get("Email") as String?
            }
        }
    }


    private fun showAlert(msg:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }



}