package com.example.gestion_de_campeonatos01.data

import androidx.lifecycle.LiveData

class JugadorRepository(
    private val jugadorDao: JugadorDao
) {

    val jugadores: LiveData<List<Jugador>> =
        jugadorDao.listarJugadores()

    suspend fun insertar(jugador: Jugador) {
        jugadorDao.insertar(jugador)
    }

    suspend fun actualizar(jugador: Jugador) {
        jugadorDao.actualizar(jugador)
    }

    suspend fun eliminar(id: Int) {
        jugadorDao.eliminar(id)
    }
}