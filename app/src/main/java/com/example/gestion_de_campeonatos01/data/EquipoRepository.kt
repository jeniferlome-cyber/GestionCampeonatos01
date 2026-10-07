package com.example.gestion_de_campeonatos01.data

import androidx.lifecycle.LiveData

class EquipoRepository(
    private val equipoDao: EquipoDao
) {

    val equipos: LiveData<List<Equipo>> =
        equipoDao.listarEquipos()

    suspend fun insertar(equipo: Equipo) {
        equipoDao.insertar(equipo)
    }
}
