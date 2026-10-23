package com.example.proyecto_segundo_parcial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class DoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mensaje = try {
            DoomNative.estado()
        } catch (e: UnsatisfiedLinkError) {
            "No se pudo cargar la biblioteca nativa: ${e.message}"
        }

        setContent {
            PantallaPruebaDoom(
                mensaje = mensaje,
                volver = { finish() }
            )
        }
    }
}

@Composable
private fun PantallaPruebaDoom(mensaje: String, volver: () -> Unit) {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0D1625)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("GeoDoom - prueba nativa", style = MaterialTheme.typography.headlineMedium)
                Text(mensaje, modifier = Modifier.padding(vertical = 24.dp))
                Text("Esta pantalla prueba JNI. Todavia no ejecuta DOOM.")
                Button(onClick = volver, modifier = Modifier.padding(top = 20.dp)) {
                    Text("Volver a la calculadora")
                }
            }
        }
    }
}
