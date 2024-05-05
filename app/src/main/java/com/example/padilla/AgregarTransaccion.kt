package com.example.padilla

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.padilla.ui.theme.PadillaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.Format
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.Month
import java.time.MonthDay
import java.time.Year
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import java.util.logging.SimpleFormatter

class AgregarTransaccion : ComponentActivity(),OnClickListener {
    private lateinit var btnFecha: Button
    private lateinit var btnLimpiar: Button
    private lateinit var btnGuardar: Button
    private lateinit var formatDate: Format
    private lateinit var txtMontoTransaccion: EditText
    private lateinit var txtPersonaTransaccion: EditText
    private lateinit var txtNotaTransaccion: EditText
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_transaccion)
        init()

        btnFecha.setOnClickListener {
            val currentDate = Calendar.getInstance()
            year = currentDate.get(Calendar.YEAR)
            month = currentDate.get(Calendar.MONTH)
            day = currentDate.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)
                    val formattedDate = formatDate.format(selectedDate.time)
                    Toast.makeText(this, "Date: $formattedDate", Toast.LENGTH_SHORT).show()
                },
                year,
                month,
                day
            )
            datePicker.show()
        }
    }

    private fun init() {
        btnFecha = findViewById(R.id.btnFecha)
        btnLimpiar = findViewById(R.id.btnLimpiarTransaccion)
        btnGuardar = findViewById(R.id.btnGuardarTransaccion)
        btnFecha.setOnClickListener(this)
        btnLimpiar.setOnClickListener(this)
        btnGuardar.setOnClickListener(this)
        formatDate = SimpleDateFormat("dd,MMMM,yyyy", Locale.US)
        txtMontoTransaccion= findViewById(R.id.montoTransaccion)
        txtNotaTransaccion= findViewById(R.id.txtNotaTransaccion)
        txtPersonaTransaccion = findViewById(R.id.txtPersonaTransaccion)

    }

    override fun onClick(v: View?) {
        if(v == btnLimpiar){
            limpiar()
            return
        }
        if(v == btnGuardar){
            if(!validar()){
              showAlert("Todos los campos necesitan estar llenos")
                return
            }
            guardar()
            limpiar()
        }
    }

    private fun validar(): Boolean {
    return txtNotaTransaccion.text.isNotEmpty() && txtPersonaTransaccion.text.isNotEmpty() && txtMontoTransaccion.text.isNotEmpty()
            &&day !=0 &&month !=0 &&year !=0
    }

    private fun showAlert(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Aviso")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun guardar() {
        db = FirebaseFirestore.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val monto = txtMontoTransaccion.text.toString() ?: ""
        val fecha = "$day/$month/$year"
        val persona = txtPersonaTransaccion.text.toString() ?: ""
        val nota = txtNotaTransaccion.text.toString() ?: ""
        if (currentUser != null) {
            val uuid = UUID.randomUUID().toString()
            db.collection("Transacciones").document(uuid).set(
                hashMapOf(
                    "Usuario" to currentUser.uid,
                    "monto" to monto,
                    "Fecha" to fecha,
                    "Persona" to persona,
                    "Nota" to nota
                )
            )
        }
        showAlert("Transaccion Guardada Correctamente")
    }

    private fun limpiar(){
        txtNotaTransaccion.setText("")
        txtPersonaTransaccion.setText("")
        txtMontoTransaccion.setText("")
        day = 0
        month = 0
        year = 0
    }

}

