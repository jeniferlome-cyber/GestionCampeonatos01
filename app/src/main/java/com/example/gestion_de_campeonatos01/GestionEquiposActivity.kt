package com.example.gestion_de_campeonatos01

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestion_de_campeonatos01.adapter.EquipoAdapter
import com.example.gestion_de_campeonatos01.data.AppDatabase
import com.example.gestion_de_campeonatos01.data.Equipo
import com.example.gestion_de_campeonatos01.data.EquipoRepository
import com.example.gestion_de_campeonatos01.viewmodel.EquipoViewModel
import com.example.gestion_de_campeonatos01.viewmodel.EquipoViewModelFactory

class GestionEquiposActivity : AppCompatActivity() {

    private lateinit var viewModel: EquipoViewModel
    private lateinit var adapter: EquipoAdapter
    private var dialogoAgregar: AlertDialog? = null
    private var vistaFormulario: View? = null

    // El selector del sistema permite elegir una imagen sin permisos de almacenamiento.
    private val seleccionarImagen = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null && dialogoAgregar != null) {
            viewModel.cargarImagen(applicationContext, uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestion_equipos)

        val main = findViewById<View>(R.id.main)
        val padding = main.paddingLeft
        ViewCompat.setOnApplyWindowInsetsListener(main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                padding + systemBars.left,
                padding + systemBars.top,
                padding + systemBars.right,
                padding + systemBars.bottom
            )
            insets
        }

        val database = AppDatabase.getDatabase(this)
        val repository = EquipoRepository(database.equipoDao())
        val factory = EquipoViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[EquipoViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerEquipos)
        adapter = EquipoAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnAgregarEquipo).setOnClickListener {
            viewModel.imagenSeleccionada.value = null
            mostrarDialogAgregar()
        }
        findViewById<View>(R.id.btnVolver).setOnClickListener { finish() }

        if (savedInstanceState?.getBoolean("formularioAbierto") == true) {
            if (viewModel.imagenSeleccionada.value == null) {
                viewModel.imagenSeleccionada.value = savedInstanceState.getByteArray("imagenEquipo")
            }
            mostrarDialogAgregar(savedInstanceState.getString("nombreEquipo").orEmpty())
        }

        viewModel.equipos.observe(this) { equipos ->
            adapter.actualizarLista(equipos)
            findViewById<View>(R.id.txtSinEquipos).visibility =
                if (equipos.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.imagenSeleccionada.observe(this) { mostrarVistaPrevia() }
        viewModel.cargandoImagen.observe(this) { actualizarBotones() }
        viewModel.guardando.observe(this) { actualizarBotones() }
        viewModel.guardado.observe(this) { guardado ->
            if (guardado) {
                dialogoAgregar?.dismiss()
                viewModel.imagenSeleccionada.value = null
                viewModel.guardado.value = false
                Toast.makeText(this, "Equipo agregado", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.error.observe(this) { mensaje ->
            if (mensaje != null) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                viewModel.error.value = null
            }
        }
    }

    private fun mostrarDialogAgregar(nombre: String = "") {
        if (dialogoAgregar != null) return

        val vista = layoutInflater.inflate(R.layout.dialog_agregar_equipo, null)
        vistaFormulario = vista
        val etNombre = vista.findViewById<EditText>(R.id.etNombreEquipo)
        etNombre.setText(nombre)

        vista.findViewById<Button>(R.id.btnSeleccionarImagen).setOnClickListener {
            try {
                seleccionarImagen.launch("image/*")
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "No hay un selector de imágenes disponible", Toast.LENGTH_SHORT).show()
            }
        }
        vista.findViewById<Button>(R.id.btnQuitarImagen).setOnClickListener {
            viewModel.imagenSeleccionada.value = null
        }
        vista.findViewById<View>(R.id.contenedorImagenEquipo).setOnClickListener {
            vista.findViewById<Button>(R.id.btnSeleccionarImagen).performClick()
        }

        val dialogo = AlertDialog.Builder(this)
            .setTitle("Agregar equipo")
            .setView(vista)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Guardar", null)
            .create()

        dialogoAgregar = dialogo
        dialogo.setOnDismissListener {
            dialogoAgregar = null
            vistaFormulario = null
        }
        dialogo.show()
        // Valida sin cerrar el formulario cuando el nombre está vacío.
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nombreEquipo = etNombre.text.toString().trim()
            if (nombreEquipo.isEmpty()) {
                etNombre.error = "Ingresa el nombre del equipo"
                etNombre.requestFocus()
                return@setOnClickListener
            }

            val equipo = Equipo(
                nombre = nombreEquipo,
                imagen = viewModel.imagenSeleccionada.value
            )
            viewModel.insertar(equipo)
        }
        mostrarVistaPrevia()
        actualizarBotones()
    }

    private fun mostrarVistaPrevia() {
        val imagen = viewModel.imagenSeleccionada.value
        val imgEquipo = vistaFormulario?.findViewById<ImageView>(R.id.imgVistaPrevia) ?: return
        val padding = if (imagen == null) (16 * resources.displayMetrics.density).toInt() else 0
        imgEquipo.setPadding(padding, padding, padding, padding)
        if (imagen != null) {
            imgEquipo.setImageBitmap(BitmapFactory.decodeByteArray(imagen, 0, imagen.size))
        } else {
            imgEquipo.setImageResource(R.drawable.ic_agregar_imagen_equipo)
        }
        vistaFormulario?.findViewById<Button>(R.id.btnQuitarImagen)?.visibility =
            if (imagen == null) View.GONE else View.VISIBLE
    }

    private fun actualizarBotones() {
        val ocupado = viewModel.cargandoImagen.value == true || viewModel.guardando.value == true
        dialogoAgregar?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = !ocupado
        dialogoAgregar?.getButton(AlertDialog.BUTTON_NEGATIVE)?.isEnabled = !ocupado
        dialogoAgregar?.setCancelable(!ocupado)
        vistaFormulario?.findViewById<Button>(R.id.btnSeleccionarImagen)?.apply {
            isEnabled = !ocupado
            text = if (viewModel.cargandoImagen.value == true) "Cargando imagen…" else "Seleccionar imagen"
        }
        vistaFormulario?.findViewById<Button>(R.id.btnQuitarImagen)?.isEnabled = !ocupado
        vistaFormulario?.findViewById<View>(R.id.contenedorImagenEquipo)?.isEnabled = !ocupado
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("formularioAbierto", dialogoAgregar != null)
        outState.putString("nombreEquipo", vistaFormulario?.findViewById<EditText>(R.id.etNombreEquipo)?.text?.toString())
        outState.putByteArray("imagenEquipo", viewModel.imagenSeleccionada.value)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        dialogoAgregar?.setOnDismissListener(null)
        dialogoAgregar?.dismiss()
        dialogoAgregar = null
        vistaFormulario = null
        super.onDestroy()
    }
}
