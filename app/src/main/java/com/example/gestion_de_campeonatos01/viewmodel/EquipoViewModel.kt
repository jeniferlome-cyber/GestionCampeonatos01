package com.example.gestion_de_campeonatos01.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestion_de_campeonatos01.data.Equipo
import com.example.gestion_de_campeonatos01.data.EquipoRepository
import com.example.gestion_de_campeonatos01.util.ImagenEquipo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EquipoViewModel(
    private val repository: EquipoRepository
) : ViewModel() {

    val equipos: LiveData<List<Equipo>> = repository.equipos
    val imagenSeleccionada = MutableLiveData<ByteArray?>(null)
    val cargandoImagen = MutableLiveData(false)
    val guardando = MutableLiveData(false)
    val guardado = MutableLiveData(false)
    val error = MutableLiveData<String?>(null)
    var mensajeExito = ""
        private set

    fun cargarImagen(context: Context, uri: Uri) {
        val applicationContext = context.applicationContext
        cargandoImagen.value = true
        viewModelScope.launch {
            try {
                imagenSeleccionada.value = withContext(Dispatchers.IO) {
                    ImagenEquipo.leer(applicationContext, uri)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error.value = "No se pudo abrir la imagen. Selecciona otra."
            } finally {
                cargandoImagen.value = false
            }
        }
    }

    fun insertar(equipo: Equipo) {
        ejecutarOperacion("Equipo agregado") { repository.insertar(equipo) }
    }

    fun actualizar(equipo: Equipo) {
        ejecutarOperacion("Equipo actualizado") { repository.actualizar(equipo) }
    }

    fun eliminar(id: Int) {
        ejecutarOperacion("Equipo eliminado") { repository.eliminar(id) }
    }

    private fun ejecutarOperacion(mensaje: String, operacion: suspend () -> Unit) {
        if (guardando.value == true) return
        guardando.value = true
        viewModelScope.launch {
            try {
                operacion()
                mensajeExito = mensaje
                guardado.value = true
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error.value = "No se pudo completar la operación. Inténtalo nuevamente."
            } finally {
                guardando.value = false
            }
        }
    }
}
