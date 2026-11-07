package com.example.proyecto_segundo_parcial

/** Enlace JNI con el motor Doomgeneric incluido dentro de libgeodoom.so. */
object DoomNative {
    init {
        System.loadLibrary("geodoom")
    }

    const val WIDTH = 320
    const val HEIGHT = 200

    // Códigos originales de doomkeys.h
    const val UP = 0xad
    const val DOWN = 0xaf
    const val LEFT = 0xac
    const val RIGHT = 0xae
    const val FIRE = 0xa3
    const val USE = 0xa2
    const val ENTER = 13
    const val ESCAPE = 27
    const val TAB = 9

    external fun estado(): String
    external fun iniciar(wadPath: String, configDir: String): Boolean
    external fun cuadro(argb: IntArray): Boolean
    external fun tecla(codigo: Int, presionada: Boolean)
}
