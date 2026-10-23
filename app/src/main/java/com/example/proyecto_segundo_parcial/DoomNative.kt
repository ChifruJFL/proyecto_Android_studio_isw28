package com.example.proyecto_segundo_parcial

/** Puente con el codigo C++. Todavia NO incluye el motor de DOOM. */
object DoomNative {
    init {
        System.loadLibrary("geodoom")
    }

    external fun estado(): String
}
