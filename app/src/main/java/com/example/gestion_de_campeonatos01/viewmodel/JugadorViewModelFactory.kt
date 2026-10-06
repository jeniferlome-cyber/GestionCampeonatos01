package com.example.gestion_de_campeonatos01.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gestion_de_campeonatos01.data.JugadorRepository

class JugadorViewModelFactory(
    private val repository: JugadorRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(JugadorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JugadorViewModel(repository) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}