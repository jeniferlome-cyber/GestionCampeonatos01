package com.example.gestion_de_campeonatos01.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Jugador::class, Equipo::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun jugadorDao(): JugadorDao
    abstract fun equipoDao(): EquipoDao

    companion object {

        // Agrega equipos sin borrar los jugadores existentes.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `equipos` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`nombre` TEXT NOT NULL, `imagen` BLOB)"
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jugadores_database"
                ).addMigrations(MIGRATION_1_2).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
