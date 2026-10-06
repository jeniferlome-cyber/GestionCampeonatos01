package com.example.gestion_de_campeonatos01.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jugadores")
data class Jugador(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val dni: String,
    val edad: Int,
    val equipo: String
)