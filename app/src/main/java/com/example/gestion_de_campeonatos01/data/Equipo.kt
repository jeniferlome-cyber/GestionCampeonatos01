package com.example.gestion_de_campeonatos01.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipos")
data class Equipo(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    // Copia reducida de la imagen; null representa un equipo sin imagen.
    val imagen: ByteArray? = null
)
