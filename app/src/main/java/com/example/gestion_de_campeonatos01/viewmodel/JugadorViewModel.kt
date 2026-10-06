package com.example.gestion_de_campeonatos01.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestion_de_campeonatos01.data.Jugador
import com.example.gestion_de_campeonatos01.data.JugadorRepository
import kotlinx.coroutines.launch

class JugadorViewModel(
    private val repository: JugadorRepository
) : ViewModel() {

    val jugadores: LiveData<List<Jugador>> =
        repository.jugadores

    fun insertar(jugador: Jugador) {
        viewModelScope.launch {
            repository.insertar(jugador)
        }
    }

    fun actualizar(jugador: Jugador) {
        viewModelScope.launch {
            repository.actualizar(jugador)
        }
    }

    fun eliminar(id: Int) {
        viewModelScope.launch {
            repository.eliminar(id)
        }
    }
}