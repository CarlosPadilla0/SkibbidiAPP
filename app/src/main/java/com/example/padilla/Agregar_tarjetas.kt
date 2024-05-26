package com.example.padilla

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.padilla.adapter.AdapterTarjetas
import com.example.padilla.adapter.Tarjetas
import com.example.padilla.adapter.tarjetasProvider
import com.example.padilla.databinding.DialogAddCardBinding
import com.example.padilla.databinding.ListaTarjetasBinding
import com.getbase.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class Agregar_tarjetas : ComponentActivity(), OnClickListener {
    private lateinit var fab: FloatingActionButton
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ListaTarjetasBinding
    private lateinit var adapter: AdapterTarjetas
    private var tarjetasMutableList: MutableList<Tarjetas> = tarjetasProvider.tarjetasList.toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListaTarjetasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        getTarjetasData()
    }

    private fun init() {
        fab = findViewById(R.id.fab_Tarjeta)
        fab.setOnClickListener(this)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val manager = LinearLayoutManager(this)
        val decoration = DividerItemDecoration(this, manager.orientation)
        adapter = AdapterTarjetas(
            TarjetasList = tarjetasMutableList,
            onclickListener = { tarjeta -> onitemSelected(tarjeta) },
            onClickDelete = { position -> onDeleteItem(position) },
        )
        binding.recyclerViewTarjetas.layoutManager = manager
        binding.recyclerViewTarjetas.adapter = adapter
        binding.recyclerViewTarjetas.addItemDecoration(decoration)
    }

    private fun onitemSelected(tarjetas: Tarjetas) {
        Toast.makeText(this, tarjetas.terminacion, Toast.LENGTH_SHORT).show()
    }

    private fun onDeleteItem(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Tarjeta")
            .setMessage("¿Estás seguro de que quieres eliminar esta tarjeta?")
            .setPositiveButton("Sí") { dialog, which ->
                val tarjeta = tarjetasMutableList[position]
                val mAuth = FirebaseAuth.getInstance()
                val currentUser = mAuth.currentUser
                val tarjetaRef = db.collection("tarjetas")
                    .whereEqualTo("uid", currentUser?.uid)
                    .whereEqualTo("cardEnding", tarjeta.terminacion)
                    .whereEqualTo("provider", tarjeta.proveedor)
                    .limit(1)

                tarjetaRef.get().addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val document = result.documents[0]
                        db.collection("tarjetas").document(document.id).delete()
                            .addOnSuccessListener {
                                tarjetasMutableList.removeAt(position)
                                adapter.notifyItemRemoved(position)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error deleting card: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Error getting documents: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onClick(v: View?) {
        if (fab == v) {
            showAddCardDialog()
        }
    }

    private fun showAddCardDialog() {
        val builder = AlertDialog.Builder(this)
        val binding = DialogAddCardBinding.inflate(layoutInflater)
        builder.setView(binding.root)
            .setTitle("Agregar Tarjeta")
            .setPositiveButton("Aceptar") { dialog, id ->
                val cardEnding = binding.editTextCardEnding.text.toString()
                val provider = binding.spinnerProvider.selectedItem.toString()
                addCardToDatabase(cardEnding, provider)
            }
            .setNegativeButton("Cancelar") { dialog, id ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun addCardToDatabase(cardEnding: String, provider: String) {
        val user = auth.currentUser
        if (user != null) {
            val card = hashMapOf(
                "uid" to user.uid,
                "cardEnding" to cardEnding,
                "provider" to provider
            )
            val uuid = UUID.randomUUID().toString()
            db.collection("tarjetas").document(uuid).set(card)
                .addOnSuccessListener {
                    showAlert("Tarjeta Guardada Correctamente")
                    val newCard = Tarjetas(cardEnding, provider)
                    tarjetasMutableList.add(newCard)
                    adapter.notifyItemInserted(tarjetasMutableList.size - 1)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding card: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun getTarjetasData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("tarjetas").whereEqualTo("uid", currentUser.uid).get()
                .addOnSuccessListener { result ->
                    tarjetasMutableList.clear()
                    for (document in result) {
                        val uid = document.getString("uid") ?: ""
                        val cardEnding = document.getString("cardEnding") ?: ""
                        val provider = document.getString("provider") ?: ""
                        val tarjeta = Tarjetas(cardEnding, provider)
                        tarjetasMutableList.add(tarjeta)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error getting documents: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
