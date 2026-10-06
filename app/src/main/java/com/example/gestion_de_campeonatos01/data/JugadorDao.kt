package com.example.gestion_de_campeonatos01.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface JugadorDao {

    @Query("SELECT * FROM jugadores ORDER BY id DESC")
    fun listarJugadores(): LiveData<List<Jugador>>

    @Insert
    suspend fun insertar(jugador: Jugador)

    @Update
    suspend fun actualizar(jugador: Jugador)

    @Query("DELETE FROM jugadores WHERE id = :id")
    suspend fun eliminar(id: Int)
}