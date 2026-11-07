package com.example.proyecto_segundo_parcial

import android.content.Context
import java.io.File
import java.io.RandomAccessFile

/**
 * Instala en el almacenamiento privado de la app el WAD libre incluido en assets/.
 * No inicia el motor de DOOM: solo prepara y valida sus datos.
 */
object DoomAssets {
    private const val NOMBRE = "freedoom1.wad"

    data class WADInfo(
        val archivo: File,
        val cantidadRecursos: Int,
        val tamanoMB: Double
    )

    fun preparar(context: Context): WADInfo {
        val carpeta = File(context.filesDir, "wads")
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            error("No se pudo crear la carpeta interna de WADs")
        }

        val destino = File(carpeta, NOMBRE)
        if (!destino.exists() || destino.length() < 12) {
            val temporal = File(carpeta, "$NOMBRE.part")
            try {
                context.assets.open(NOMBRE).use { entrada ->
                    temporal.outputStream().use { salida -> entrada.copyTo(salida) }
                }
                verificarWad(temporal)
                if (destino.exists() && !destino.delete()) {
                    error("No se pudo reemplazar el WAD anterior")
                }
                if (!temporal.renameTo(destino)) {
                    error("No se pudo completar la copia del WAD")
                }
            } finally {
                if (temporal.exists()) temporal.delete()
            }
        }

        val recursos = verificarWad(destino)
        return WADInfo(
            archivo = destino,
            cantidadRecursos = recursos,
            tamanoMB = destino.length() / (1024.0 * 1024.0)
        )
    }

    private fun verificarWad(archivo: File): Int = RandomAccessFile(archivo, "r").use { wad ->
        if (wad.length() < 12) error("Archivo WAD incompleto")

        val cabecera = ByteArray(4)
        wad.readFully(cabecera)
        val tipo = cabecera.toString(Charsets.US_ASCII)
        if (tipo != "IWAD") error("El archivo no es un IWAD válido: $tipo")

        val recursos = Integer.reverseBytes(wad.readInt())
        val posicionDirectorio = Integer.reverseBytes(wad.readInt()).toLong() and 0xffffffffL
        if (recursos <= 0 || posicionDirectorio < 12L ||
            posicionDirectorio + recursos.toLong() * 16L > wad.length()) {
            error("El directorio del WAD está incompleto o dañado")
        }
        recursos
    }
}
