package com.example.gestion_de_campeonatos01.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gestion_de_campeonatos01.data.EquipoRepository

class EquipoViewModelFactory(
    private val repository: EquipoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(EquipoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EquipoViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}
