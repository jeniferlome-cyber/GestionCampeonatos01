package com.example.gestion_de_campeonatos01.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.math.max
import kotlin.math.roundToInt

object ImagenEquipo {

    private const val TAMANO_ICONO = 192

    // Se llama desde Dispatchers.IO; evita guardar o cargar la foto a tamaño completo.
    fun leer(context: Context, uri: Uri): ByteArray {
        val resolver = context.contentResolver
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(resolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                val escala = minOf(1f, TAMANO_ICONO.toFloat() / max(info.size.width, info.size.height))
                decoder.setTargetSize(
                    max(1, (info.size.width * escala).roundToInt()),
                    max(1, (info.size.height * escala).roundToInt())
                )
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, opciones)
            }
            if (opciones.outWidth <= 0 || opciones.outHeight <= 0) {
                throw IOException("Imagen no válida")
            }
            opciones.inSampleSize = 1
            while (max(opciones.outWidth, opciones.outHeight) / opciones.inSampleSize > TAMANO_ICONO) {
                opciones.inSampleSize *= 2
            }
            opciones.inJustDecodeBounds = false
            resolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, opciones)
            } ?: throw IOException("No se pudo leer la imagen")
        }

        return try {
            ByteArrayOutputStream().use { salida ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, salida)) {
                    throw IOException("No se pudo preparar la imagen")
                }
                salida.toByteArray()
            }
        } finally {
            bitmap.recycle()
        }
    }
}
