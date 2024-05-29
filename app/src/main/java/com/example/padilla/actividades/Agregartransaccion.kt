package com.example.padilla.actividades

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.example.padilla.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

class Agregartransaccion : ComponentActivity(), View.OnClickListener {
    private lateinit var btnFecha: Button
    private lateinit var btnLimpiar: Button
    private lateinit var btnGuardar: Button
    private lateinit var formatDate: Format
    private lateinit var txtMontoTransaccion: EditText
    private lateinit var txtPersonaTransaccion: EditText
    private lateinit var txtNotaTransaccion: EditText
    private lateinit var radioIngreso: RadioButton
    private lateinit var radioEgreso: RadioButton
    private lateinit var radioTarjeta: RadioButton
    private lateinit var radioEfectivo: RadioButton
    private lateinit var spinnerTarjetas: Spinner
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private var tarjetaList: MutableList<String> = mutableListOf()
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var txtTerminacion:TextView
    private val activityScope = CoroutineScope(Dispatchers.Main)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_transaccion)
        init()
        getTarjetasdata()
    }

    private fun init() {
        btnFecha = findViewById(R.id.btnFecha)
        btnLimpiar = findViewById(R.id.btnLimpiarTransaccion)
        btnGuardar = findViewById(R.id.btnGuardarTransaccion)
        formatDate = SimpleDateFormat("dd,MMMM,yyyy", Locale.US)
        txtMontoTransaccion = findViewById(R.id.montoTransaccion)
        txtNotaTransaccion = findViewById(R.id.txtNotaTransaccion)
        txtPersonaTransaccion = findViewById(R.id.txtPersonaTransaccion)
        radioIngreso = findViewById(R.id.radioIngreso)
        radioEgreso = findViewById(R.id.radioEgreso)
        radioTarjeta = findViewById(R.id.radioTarjeta)
        radioEfectivo = findViewById(R.id.radioEfectivo)
        spinnerTarjetas = findViewById(R.id.spinnerTarjetas)
        txtTerminacion = findViewById(R.id.txtTerminacion)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        radioTarjeta= findViewById(R.id.radioTarjeta)
        radioEfectivo= findViewById(R.id.radioEfectivo)

        btnFecha.setOnClickListener(this)
        btnLimpiar.setOnClickListener(this)
        btnGuardar.setOnClickListener(this)
        radioTarjeta.setOnClickListener(this)
        radioEfectivo.setOnClickListener(this)

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


        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tarjetaList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTarjetas.adapter = spinnerAdapter

        spinnerTarjetas.visibility = View.GONE
        txtTerminacion.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        when (v) {
            btnLimpiar -> {
                limpiar()
            }

            btnGuardar -> {
                if (!validar()) {
                    showAlert("Todos los campos necesitan estar llenos")
                    return
                }
                guardar()
                limpiar()

            }

            radioTarjeta -> {
                if (radioTarjeta.isChecked) {
                    spinnerTarjetas.visibility = View.VISIBLE
                    txtTerminacion.visibility = View.VISIBLE
                }
            }

            radioEfectivo -> {
                if (radioEfectivo.isChecked) {
                    spinnerTarjetas.visibility = View.GONE
                    txtTerminacion.visibility = View.GONE
                }
            }
        }
    }

    private fun validar(): Boolean {
        return txtNotaTransaccion.text.isNotEmpty() && txtPersonaTransaccion.text.isNotEmpty() && txtMontoTransaccion.text.isNotEmpty()
                && day != 0 && month != 0 && year != 0 && (radioIngreso.isChecked || radioEgreso.isChecked) && (radioTarjeta.isChecked || radioEfectivo.isChecked)
    }

    private fun showAlert(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Aviso")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun guardar() {
        val currentUser = auth.currentUser
        val monto = txtMontoTransaccion.text.toString()
        val fecha = "$day/$month/$year"
        val persona = txtPersonaTransaccion.text.toString()
        val nota = txtNotaTransaccion.text.toString()
        val tipo = if (radioIngreso.isChecked) "Ingreso" else "Egreso"
        val formaPago = if (radioTarjeta.isChecked) "Tarjeta" else "Efectivo"
        val terminacionTarjeta = if (radioTarjeta.isChecked) "Tarjeta: ${spinnerTarjetas.selectedItem.toString().split(" ").last()}" else ""

        if (currentUser != null) {
            val uuid = UUID.randomUUID().toString()
            val transaccion = hashMapOf(
                "Usuario" to currentUser.uid,
                "monto" to monto,
                "Fecha" to fecha,
                "Persona" to persona,
                "Nota" to nota,
                "Tipo" to tipo,
                "FormaPago" to formaPago,
                "TerminacionTarjeta" to terminacionTarjeta
            )

            if (radioTarjeta.isChecked && tarjetaList.isEmpty()) {
                showAlert("Para realizar una transacción con tarjeta, primero debes registrar una tarjeta.")
                return
            }

            db.collection("Transacciones").document(uuid).set(transaccion)
                .addOnSuccessListener {
                    showAlert("Transaccion Guardada Correctamente")
                    activityScope.launch {
                        delay(2000) // Retraso de 2 segundos
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    showAlert("Error al guardar la transaccion: ${e.message}")
                }
        }
    }



    private fun limpiar() {
        txtNotaTransaccion.setText("")
        txtPersonaTransaccion.setText("")
        txtMontoTransaccion.setText("")
        day = 0
        month = 0
        year = 0
        radioIngreso.isChecked = false
        radioEgreso.isChecked = false
        radioTarjeta.isChecked = false
        radioEfectivo.isChecked = false
        spinnerTarjetas.visibility = View.GONE
    }

    private fun getTarjetasdata() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("tarjetas").whereEqualTo("uid", currentUser.uid).get()
                .addOnSuccessListener { result ->
                    tarjetaList.clear()
                    for (document in result) {
                        val terminacion = document.getString("cardEnding") ?: ""
                        val proveedor = document.getString("provider") ?: ""
                        tarjetaList.add("$proveedor - Terminación: $terminacion")
                    }
                    spinnerAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    showAlert("Error al obtener las tarjetas: ${e.message}")
                }
        }
    }
}
