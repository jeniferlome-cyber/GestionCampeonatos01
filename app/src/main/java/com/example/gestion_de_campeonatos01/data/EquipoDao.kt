package com.example.gestion_de_campeonatos01.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface EquipoDao {

    @Query("SELECT * FROM equipos ORDER BY id DESC")
    fun listarEquipos(): LiveData<List<Equipo>>

    @Insert
    suspend fun insertar(equipo: Equipo)

    @Update
    suspend fun actualizar(equipo: Equipo)

    @Query("DELETE FROM equipos WHERE id = :id")
    suspend fun eliminar(id: Int)
}
