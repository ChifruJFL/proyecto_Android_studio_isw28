package com.example.proyecto_segundo_parcial

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Fondo = Color(0xFF0D1625)
private val Tarjeta = Color(0xFF1A293F)
private val Acento = Color(0xFF57D6B4)
private val Blanco = Color(0xFFF3F8FF)
private val Gris = Color(0xFFB1C2D5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Acento,
                    onPrimary = Fondo,
                    background = Fondo,
                    onBackground = Blanco,
                    surface = Tarjeta,
                    onSurface = Blanco
                )
            ) {
                Scaffold(containerColor = Fondo) { padding ->
                    GeoDoomApp(modifier = Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
private fun GeoDoomApp(modifier: Modifier = Modifier) {
    var pantalla by rememberSaveable { mutableStateOf("inicio") }
    var categoria by rememberSaveable { mutableStateOf("areas") }
    var operacionId by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    fun volver() {
        when (pantalla) {
            "operacion" -> pantalla = if (operacionId == "ecuacion") "inicio" else "categoria"
            "categoria" -> pantalla = "inicio"
        }
    }

    BackHandler(enabled = pantalla != "inicio") { volver() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        when (pantalla) {
            "inicio" -> {
                Text("GeoDoom", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Blanco)
                Text("Tu calculadora de C++ ahora en Android", color = Gris)
                MenuOpcion("Áreas y perímetros", "7 figuras: triángulos, círculo, rectángulo y más") {
                    categoria = "areas"
                    pantalla = "categoria"
                }
                MenuOpcion("Volúmenes", "Esfera, pirámide, cubo, cilindro y paralelepípedo") {
                    categoria = "volumenes"
                    pantalla = "categoria"
                }
                MenuOpcion("Fórmula general", "Resolver ax² + bx + c = 0") {
                    operacionId = "ecuacion"
                    pantalla = "operacion"
                }

                MenuOpcion(
                    "🎮 DOOM - Prueba nativa",
                    "Comprobar conexión entre Kotlin y C++"
                ) {
                    abrirFreedoom(context)
                }

                Text(
                    "Primera prueba del motor nativo de GeoDoom.",
                    color = Gris,
                    fontSize = 13.sp
                )

            }
            "categoria" -> {
                TextButton(onClick = { volver() }) { Text("← Volver") }
                Text(
                    if (categoria == "areas") "Áreas y perímetros" else "Volúmenes",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blanco
                )
                val operaciones = if (categoria == "areas") Calculadora.areas else Calculadora.volumenes
                operaciones.forEach { operacion ->
                    MenuOpcion(operacion.nombre, operacion.descripcion) {
                        operacionId = operacion.id
                        pantalla = "operacion"
                    }
                }
            }
            "operacion" -> {
                TextButton(onClick = { volver() }) { Text("← Volver") }
                val operacion = (Calculadora.areas + Calculadora.volumenes + listOf(Calculadora.ecuacion))
                    .firstOrNull { it.id == operacionId }
                if (operacion != null) {
                    PantallaOperacion(operacion)
                } else {
                    Text("No se encontró la operación.", color = Blanco)
                }
            }
        }
    }
}

@Composable
private fun MenuOpcion(titulo: String, descripcion: String, alPulsar: () -> Unit) {
    Button(
        onClick = alPulsar,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Tarjeta, contentColor = Blanco),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(descripcion, fontSize = 13.sp, color = Gris)
        }
    }
}

@Composable
private fun PantallaOperacion(operacion: Operacion) {
    val entradas = remember(operacion.id) { mutableStateMapOf<String, String>() }
    var resultado by remember(operacion.id) { mutableStateOf<Resultado?>(null) }
    var error by remember(operacion.id) { mutableStateOf<String?>(null) }

    Text(operacion.nombre, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Blanco)
    Text(operacion.descripcion, color = Gris)

    operacion.campos.forEach { campo ->
        OutlinedTextField(
            value = entradas[campo.id].orEmpty(),
            onValueChange = {
                entradas[campo.id] = it
                resultado = null
                error = null
            },
            label = { Text(campo.nombre) },
            placeholder = { Text("Escribe un número") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
    }

    Button(
        onClick = {
            try {
                val valores = operacion.campos.associate { campo ->
                    val numero = entradas[campo.id]
                        ?.trim()
                        ?.replace(',', '.')
                        ?.toDoubleOrNull()
                        ?: throw IllegalArgumentException("Introduce un número válido en: ${campo.nombre}")
                    campo.id to numero
                }
                resultado = Calculadora.calcular(operacion, valores)
                error = null
            } catch (ex: IllegalArgumentException) {
                resultado = null
                error = ex.message ?: "Revisa los números ingresados"
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Acento, contentColor = Fondo)
    ) {
        Text("Calcular", fontWeight = FontWeight.Bold)
    }

    if (error != null) {
        Text(error.orEmpty(), color = Color(0xFFFFA7A7))
    }
    resultado?.let { respuesta ->
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Tarjeta)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(respuesta.texto, color = Acento, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (respuesta.detalle.isNotEmpty()) {
                    Text(respuesta.detalle, color = Gris)
                }
            }
        }
    }
}


private fun abrirFreedoom(context: Context) {
    val intent = Intent(context, DoomActivity::class.java)
    context.startActivity(intent)
}

