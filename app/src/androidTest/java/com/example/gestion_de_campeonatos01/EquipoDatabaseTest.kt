package com.example.gestion_de_campeonatos01

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.gestion_de_campeonatos01.data.AppDatabase
import com.example.gestion_de_campeonatos01.data.Equipo
import com.example.gestion_de_campeonatos01.util.ImagenEquipo
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class EquipoDatabaseTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun editarYEliminarSoloAfectanAlEquipoSeleccionado() = runBlocking {
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val dao = database.equipoDao()
            val imagen = byteArrayOf(1, 2, 3)
            dao.insertar(Equipo(id = 1, nombre = "Tigres", imagen = imagen))
            dao.insertar(Equipo(id = 2, nombre = "Leones"))
            dao.actualizar(Equipo(id = 1, nombre = "Tigres FC", imagen = imagen))
            database.openHelper.readableDatabase.query("SELECT nombre, imagen FROM equipos WHERE id = 1").use {
                assertTrue(it.moveToFirst())
                assertEquals("Tigres FC", it.getString(0))
                assertArrayEquals(imagen, it.getBlob(1))
            }
            val nuevaImagen = byteArrayOf(4, 5, 6)
            dao.actualizar(Equipo(id = 1, nombre = "Tigres FC", imagen = nuevaImagen))
            database.openHelper.readableDatabase.query("SELECT imagen FROM equipos WHERE id = 1").use {
                assertTrue(it.moveToFirst())
                assertArrayEquals(nuevaImagen, it.getBlob(0))
            }
            dao.actualizar(Equipo(id = 1, nombre = "Tigres FC", imagen = null))
            database.openHelper.readableDatabase.query("SELECT imagen FROM equipos WHERE id = 1").use {
                assertTrue(it.moveToFirst())
                assertTrue(it.isNull(0))
            }
            dao.eliminar(1)
            database.openHelper.readableDatabase.query("SELECT id, nombre FROM equipos").use {
                assertEquals(1, it.count)
                assertTrue(it.moveToFirst())
                assertEquals(2, it.getInt(0))
                assertEquals("Leones", it.getString(1))
            }
        } finally {
            database.close()
        }
    }

    @Test
    fun migracionConservaJugadoresYGuardaEquiposConYSinImagen() = runBlocking {
        val nombreDatabase = "equipos-migracion-${System.nanoTime()}.db"
        var database: AppDatabase? = null
        try {
            // Reproduce exactamente la tabla de la versión 1 instalada.
            context.openOrCreateDatabase(nombreDatabase, Context.MODE_PRIVATE, null).use { db ->
                db.execSQL("CREATE TABLE jugadores (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, nombre TEXT NOT NULL, dni TEXT NOT NULL, edad INTEGER NOT NULL, equipo TEXT NOT NULL)")
                db.execSQL("INSERT INTO jugadores (nombre, dni, edad, equipo) VALUES ('Luis', '01234567', 21, 'Tigres')")
                db.version = 1
            }
            val migrada = Room.databaseBuilder(context, AppDatabase::class.java, nombreDatabase)
                .addMigrations(AppDatabase.MIGRATION_1_2)
                .build()
            database = migrada
            migrada.openHelper.writableDatabase.query("SELECT nombre, dni FROM jugadores").use {
                assertTrue(it.moveToFirst())
                assertEquals("Luis", it.getString(0))
                assertEquals("01234567", it.getString(1))
            }

            val imagen = byteArrayOf(10, 20, 30)
            migrada.equipoDao().insertar(Equipo(nombre = "Tigres", imagen = imagen))
            migrada.equipoDao().insertar(Equipo(nombre = "Leones"))
            migrada.close()

            val reabierta = Room.databaseBuilder(context, AppDatabase::class.java, nombreDatabase)
                .addMigrations(AppDatabase.MIGRATION_1_2)
                .build()
            database = reabierta
            reabierta.openHelper.readableDatabase.query("SELECT nombre, imagen FROM equipos ORDER BY id DESC").use {
                assertTrue(it.moveToFirst())
                assertEquals("Leones", it.getString(0))
                assertTrue(it.isNull(1))
                assertTrue(it.moveToNext())
                assertEquals("Tigres", it.getString(0))
                assertArrayEquals(imagen, it.getBlob(1))
            }
        } finally {
            database?.close()
            context.deleteDatabase(nombreDatabase)
        }
    }

    @Test
    fun imagenGrandeSeReduceAntesDeGuardarse() {
        val archivo = File.createTempFile("equipo", ".png", context.cacheDir)
        try {
            val original = Bitmap.createBitmap(1600, 800, Bitmap.Config.ARGB_8888)
            archivo.outputStream().use { original.compress(Bitmap.CompressFormat.PNG, 100, it) }
            original.recycle()
            val imagen = ImagenEquipo.leer(context, Uri.fromFile(archivo))
            val icono = BitmapFactory.decodeByteArray(imagen, 0, imagen.size)
            assertTrue(icono.width <= 192)
            assertTrue(icono.height <= 192)
            assertEquals(2f, icono.width.toFloat() / icono.height, 0.05f)
            icono.recycle()
        } finally {
            archivo.delete()
        }
    }
}
