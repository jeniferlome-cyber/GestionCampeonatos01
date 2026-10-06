package com.example.gestion_de_campeonatos01

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestion_de_campeonatos01.adapter.JugadorAdapter
import com.example.gestion_de_campeonatos01.data.AppDatabase
import com.example.gestion_de_campeonatos01.data.JugadorRepository
import com.example.gestion_de_campeonatos01.viewmodel.JugadorViewModel
import com.example.gestion_de_campeonatos01.viewmodel.JugadorViewModelFactory
import android.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.gestion_de_campeonatos01.data.Jugador
class GestionJugadoresActivity : AppCompatActivity() {

    private lateinit var viewModel: JugadorViewModel
    private lateinit var adapter: JugadorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestion_jugadores)
        val btnAgregarJugador = findViewById<Button>(R.id.btnAgregarJugador)

        btnAgregarJugador.setOnClickListener {
            mostrarDialogAgregar()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val database = AppDatabase.getDatabase(this)
        val repository = JugadorRepository(database.jugadorDao())

        val factory = JugadorViewModelFactory(repository)

        viewModel = ViewModelProvider(
            this,
            factory
        )[JugadorViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerJugadores)

        adapter = JugadorAdapter(
            onEliminar = { jugador ->
                viewModel.eliminar(jugador.id)
            },
            onModificar = { jugador ->
                mostrarDialogEditar(jugador)

            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.jugadores.observe(this) { jugadores ->
            adapter.actualizarLista(jugadores)
        }
    }

private fun mostrarDialogAgregar() {

    val vista = layoutInflater.inflate(
        R.layout.dialog_agregar_jugador,
        null
    )

    val etNombre = vista.findViewById<EditText>(R.id.etNombre)
    val etDni = vista.findViewById<EditText>(R.id.etDni)
    val etEdad = vista.findViewById<EditText>(R.id.etEdad)
    val etEquipo = vista.findViewById<EditText>(R.id.etEquipo)

    AlertDialog.Builder(this)
        .setTitle("Agregar jugador")
        .setView(vista)
        .setNegativeButton("Cancelar", null)
        .setPositiveButton("Guardar") { _, _ ->

            val nombre = etNombre.text.toString().trim()
            val dni = etDni.text.toString().trim()
            val edadTexto = etEdad.text.toString().trim()
            val equipo = etEquipo.text.toString().trim()

            if (nombre.isEmpty() ||
                dni.isEmpty() ||
                edadTexto.isEmpty() ||
                equipo.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Completa todos los campos",
                    Toast.LENGTH_SHORT
                ).show()

                return@setPositiveButton
            }

            val edad = edadTexto.toInt()

            val jugador = Jugador(
                nombre = nombre,
                dni = dni,
                edad = edad,
                equipo = equipo
            )

            viewModel.insertar(jugador)

            Toast.makeText(
                this,
                "Jugador agregado",
                Toast.LENGTH_SHORT
            ).show()
        }
        .show()
}
    private fun mostrarDialogEditar(jugador: Jugador) {

        val vista = layoutInflater.inflate(
            R.layout.dialog_editar_jugador,
            null
        )

        val etNombre = vista.findViewById<EditText>(R.id.etEditarNombre)
        val etDni = vista.findViewById<EditText>(R.id.etEditarDni)
        val etEdad = vista.findViewById<EditText>(R.id.etEditarEdad)
        val etEquipo = vista.findViewById<EditText>(R.id.etEditarEquipo)

        // Mostrar los datos actuales
        etNombre.setText(jugador.nombre)
        etDni.setText(jugador.dni)
        etEdad.setText(jugador.edad.toString())
        etEquipo.setText(jugador.equipo)

        AlertDialog.Builder(this)
            .setTitle("Editar jugador")
            .setView(vista)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Guardar") { _, _ ->

                val nombre = etNombre.text.toString().trim()
                val dni = etDni.text.toString().trim()
                val edadTexto = etEdad.text.toString().trim()
                val equipo = etEquipo.text.toString().trim()

                if (nombre.isEmpty() ||
                    dni.isEmpty() ||
                    edadTexto.isEmpty() ||
                    equipo.isEmpty()
                ) {
                    Toast.makeText(
                        this,
                        "Completa todos los campos",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val edad = edadTexto.toInt()

                val jugadorActualizado = Jugador(
                    id = jugador.id,
                    nombre = nombre,
                    dni = dni,
                    edad = edad,
                    equipo = equipo
                )

                viewModel.actualizar(jugadorActualizado)

                Toast.makeText(
                    this,
                    "Jugador actualizado",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }
}
