package com.example.proyecto_segundo_parcial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

// Colores de GeoDoom
private val Fondo = Color(0xFF0D1625)
private val Tarjeta = Color(0xFF1A293F)
private val Acento = Color(0xFF57D6B4)
private val Blanco = Color(0xFFF3F8FF)
private val Gris = Color(0xFFB1C2D5)
private val ErrorColor = Color(0xFFFF8A8A)

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
                Scaffold(
                    containerColor = Fondo
                ) { padding ->

                    GeoDoomApp(
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}

@Composable
private fun GeoDoomApp(modifier: Modifier = Modifier) {

    var pantalla by rememberSaveable {
        mutableStateOf("cientifica")
    }

    var categoria by rememberSaveable {
        mutableStateOf("areas")
    }

    var operacionId by rememberSaveable {
        mutableStateOf("")
    }

    var ultimoResultado by rememberSaveable {
        mutableStateOf("0")
    }

    var historial by remember {
        mutableStateOf(listOf<String>())
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun volver() {
        when (pantalla) {
            "operacion" -> {
                pantalla =
                    if (operacionId == "ecuacion") {
                        "cientifica"
                    } else {
                        "categoria"
                    }
            }

            "categoria", "historial" -> {
                pantalla = "cientifica"
            }
        }
    }

    BackHandler(enabled = pantalla != "cientifica") {
        volver()
    }

    fun navegar(destino: String) {

        when (destino) {

            "areas" -> {
                categoria = "areas"
                pantalla = "categoria"
            }

            "volumenes" -> {
                categoria = "volumenes"
                pantalla = "categoria"
            }

            "ecuacion" -> {
                operacionId = "ecuacion"
                pantalla = "operacion"
            }

            "cientifica" -> {
                pantalla = "cientifica"
            }

            "historial" -> {
                pantalla = "historial"
            }

            "doom" -> {
                scope.launch {
                    drawerState.close()
                    abrirFreedoom(context)
                }
                return
            }
        }

        scope.launch {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        modifier = modifier.fillMaxSize(),
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet(
                drawerContainerColor = Fondo
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {

                    Text(
                        text = "GeoDoom",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Acento
                    )

                    Text(
                        text = "Herramientas matemáticas",
                        color = Gris,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "MENÚ PRINCIPAL",
                        color = Gris,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    OpcionDrawer(
                        titulo = "🧮 Calculadora científica",
                        seleccionada = pantalla == "cientifica"
                    ) {
                        navegar("cientifica")
                    }

                    OpcionDrawer(
                        titulo = "📐 Áreas y perímetros",
                        seleccionada =
                            pantalla == "categoria" &&
                                    categoria == "areas"
                    ) {
                        navegar("areas")
                    }

                    OpcionDrawer(
                        titulo = "📦 Volúmenes",
                        seleccionada =
                            pantalla == "categoria" &&
                                    categoria == "volumenes"
                    ) {
                        navegar("volumenes")
                    }

                    OpcionDrawer(
                        titulo = "➗ Fórmula general",
                        seleccionada =
                            pantalla == "operacion" &&
                                    operacionId == "ecuacion"
                    ) {
                        navegar("ecuacion")
                    }

                    OpcionDrawer(
                        titulo = "🕘 Historial",
                        seleccionada = pantalla == "historial"
                    ) {
                        navegar("historial")
                    }

                    Spacer(Modifier.height(16.dp))

                    HorizontalDivider(color = Tarjeta)

                    Spacer(Modifier.height(16.dp))

                    OpcionDrawer(
                        titulo = "🎮 DOOM",
                        seleccionada = false
                    ) {
                        navegar("doom")
                    }

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = "GeoDoom · Calculadora y videojuegos",
                        color = Gris,
                        fontSize = 11.sp
                    )
                }
            }
        }
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // Barra superior con el menú hamburguesa
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Tarjeta,
                        contentColor = Acento
                    )
                ) {
                    Text(
                        text = "☰",
                        fontSize = 25.sp
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column {
                    Text(
                        text = "GeoDoom",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blanco
                    )

                    Text(
                        text = when (pantalla) {
                            "cientifica" -> "Calculadora científica"
                            "categoria" ->
                                if (categoria == "areas") {
                                    "Áreas y perímetros"
                                } else {
                                    "Volúmenes"
                                }

                            "operacion" -> "Herramienta matemática"
                            "historial" -> "Historial de operaciones"
                            else -> "Calculadora"
                        },
                        fontSize = 12.sp,
                        color = Gris
                    )
                }
            }

            HorizontalDivider(color = Tarjeta)

            when (pantalla) {

                "cientifica" -> {

                    CalculadoraCientifica(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        ultimoResultado = ultimoResultado,
                        alCalcular = { expresion, resultado ->

                            ultimoResultado = resultado

                            historial = (
                                    listOf("$expresion = $resultado") +
                                            historial
                                    ).take(50)
                        },
                        abrirHistorial = {
                            pantalla = "historial"
                        }
                    )
                }

                "categoria" -> {

                    PantallaCategoria(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        categoria = categoria,
                        volver = {
                            pantalla = "cientifica"
                        },
                        seleccionarOperacion = { id ->

                            operacionId = id
                            pantalla = "operacion"
                        }
                    )
                }

                "operacion" -> {

                    val operacion =
                        (
                                Calculadora.areas +
                                        Calculadora.volumenes +
                                        listOf(Calculadora.ecuacion)
                                ).firstOrNull {
                                it.id == operacionId
                            }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                    ) {

                        TextButton(
                            onClick = { volver() }
                        ) {
                            Text("← Volver a la calculadora")
                        }

                        if (operacion != null) {
                            PantallaOperacion(operacion)
                        } else {
                            Text(
                                text = "No se encontró la operación.",
                                color = Blanco
                            )
                        }
                    }
                }

                "historial" -> {
                    PantallaHistorial(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        historial = historial,
                        volver = {
                            pantalla = "cientifica"
                        },
                        borrarHistorial = {
                            historial = emptyList()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OpcionDrawer(
    titulo: String,
    seleccionada: Boolean,
    alPulsar: () -> Unit
) {

    NavigationDrawerItem(
        label = {
            Text(
                text = titulo,
                fontSize = 14.sp
            )
        },
        selected = seleccionada,
        onClick = alPulsar,
        modifier = Modifier.padding(vertical = 2.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = Tarjeta,
            selectedTextColor = Acento,
            unselectedTextColor = Blanco
        )
    )
}

// ----------------------------------------------------
// CALCULADORA CIENTÍFICA
// ----------------------------------------------------

@Composable
private fun CalculadoraCientifica(
    modifier: Modifier = Modifier,
    ultimoResultado: String,
    alCalcular: (String, String) -> Unit,
    abrirHistorial: () -> Unit

) {

    val context = androidx.compose.ui.platform.LocalContext.current

    var expresion by rememberSaveable {
        mutableStateOf("")
    }

    var resultado by rememberSaveable {
        mutableStateOf("")
    }

    var error by rememberSaveable {
        mutableStateOf("")
    }

    var grados by rememberSaveable {
        mutableStateOf(true)
    }

    var calculado by rememberSaveable {
        mutableStateOf(false)
    }

    fun agregar(texto: String) {

        expresion += texto
        resultado = ""
        error = ""
    }

    fun pulsar(tecla: String) {

        if (tecla == "6" && expresion == "66" && !calculado) {
            expresion = ""
            resultado = ""
            error = ""
            calculado = false

            abrirFreedoom(context)
            return
        }

        if (tecla == "C") {
            expresion = ""
            resultado = ""
            error = ""
            calculado = false
            return
        }

        if (tecla == "DEG/RAD") {
            grados = !grados
            resultado = ""
            error = ""
            calculado = false
            return
        }

        if (tecla == "=") {

            if (expresion.isBlank()) {
                error = "Escribe una operación primero."
                return
            }

            try {

                val valor = EvaluadorCientifico(
                    expresion,
                    grados
                ).evaluar()

                val formateado = formatearNumero(valor)

                resultado = formateado
                error = ""
                calculado = true

                alCalcular(
                    expresion,
                    formateado
                )

            } catch (ex: IllegalArgumentException) {

                error = ex.message ?: "Revisa la expresión."
                resultado = ""
                calculado = false

            } catch (ex: ArithmeticException) {

                error = "No se puede realizar esta operación."
                resultado = ""
                calculado = false
            }

            return
        }

        if (tecla == "±") {

            if (expresion.isBlank()) {
                expresion = "-"
            } else if (
                expresion.startsWith("-(") &&
                expresion.endsWith(")")
            ) {
                expresion = expresion
                    .removePrefix("-(")
                    .dropLast(1)
            } else {
                expresion = "-($expresion)"
            }

            resultado = ""
            error = ""
            calculado = false

            return
        }

        if (tecla == "DEL") {

            if (calculado) {
                expresion = resultado
                resultado = ""
                calculado = false
            }

            if (expresion.isNotEmpty()) {
                expresion = expresion.dropLast(1)
            }

            error = ""
            return
        }

        val operadores = listOf(
            "+", "−", "×", "÷", "^", "%", "!"
        )

        if (calculado) {

            if (tecla in operadores) {
                expresion = resultado
            } else {
                expresion = ""
            }

            resultado = ""
            error = ""
            calculado = false
        }

        when (tecla) {

            "sin", "cos", "tan", "ln", "log" ->
                agregar("$tecla(")

            "√" -> agregar("sqrt(")

            "x²" -> agregar("^2")

            "π" -> agregar("pi")

            "Ans" -> agregar(ultimoResultado)

            "×" -> agregar("*")

            "÷" -> agregar("/")

            "−" -> agregar("-")

            else -> agregar(tecla)
        }
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        // Pantalla de expresión y resultado
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Tarjeta
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = if (grados) {
                        "Unidad angular: Grados sexagesimales (DEG)"
                    } else {
                        "Unidad angular: RAD (radianes)"
                    },
                    color = Acento,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(18.dp))

                Text(
                    text = if (expresion.isEmpty()) {
                        "0"
                    } else {
                        expresion
                            .replace("sqrt(", "√(")
                            .replace("pi", "π")
                            .replace("*", "×")
                            .replace("/", "÷")
                            .replace("-", "−")
                    },
                    color = Blanco,
                    fontSize = 26.sp,
                    textAlign = TextAlign.End
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = resultado.ifEmpty { " " },
                    color = Acento,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )

                if (error.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = error,
                        color = ErrorColor,
                        fontSize = 13.sp,
                        textAlign = TextAlign.End
                    )
                }
            }
        }

        TextButton(
            onClick = abrirHistorial,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("🕘 Ver historial")
        }

        val filas = listOf(
            listOf("sin", "cos", "tan", "√", "x²"),
            listOf("ln", "log", "(", ")", "÷"),
            listOf("π", "e", "^", "%", "×"),
            listOf("7", "8", "9", "DEL", "−"),
            listOf("4", "5", "6", "C", "+"),
            listOf("1", "2", "3", "±", "="),
            listOf("0", ".", "!", "DEG/RAD", "Ans")
        )

        filas.forEach { fila ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                fila.forEach { tecla ->

                    val esEspecial = tecla in listOf(
                        "C", "DEL", "=", "DEG/RAD"
                    )

                    BotonCalculadora(
                        texto = when (tecla) {
                            "DEG/RAD" -> if (grados) "DEG" else "RAD"
                            else -> tecla
                        },
                        especial = esEspecial || tecla == "=",
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                    ) {

                        pulsar(tecla)
                    }
                }
            }
        }

        Text(
            text = "GeoDoom · Calculadora científica",
            color = Gris,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BotonCalculadora(
    texto: String,
    especial: Boolean,
    modifier: Modifier = Modifier,
    alPulsar: () -> Unit
) {

    Button(
        onClick = alPulsar,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (especial) Acento else Tarjeta,
            contentColor = if (especial) Fondo else Blanco
        )
    ) {

        Text(
            text = texto,
            fontSize = if (texto.length > 3) 12.sp else 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

// ----------------------------------------------------
// PANTALLAS DE LAS HERRAMIENTAS EXISTENTES
// ----------------------------------------------------

@Composable
private fun PantallaCategoria(
    modifier: Modifier = Modifier,
    categoria: String,
    volver: () -> Unit,
    seleccionarOperacion: (String) -> Unit
) {

    val operaciones = if (categoria == "areas") {
        Calculadora.areas
    } else {
        Calculadora.volumenes
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        TextButton(onClick = volver) {
            Text("← Volver a la calculadora")
        }

        Text(
            text = if (categoria == "areas") {
                "Áreas y perímetros"
            } else {
                "Volúmenes"
            },
            color = Blanco,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Selecciona una operación.",
            color = Gris
        )

        operaciones.forEach { operacion ->

            MenuOpcion(
                titulo = operacion.nombre,
                descripcion = operacion.descripcion
            ) {
                seleccionarOperacion(operacion.id)
            }
        }
    }
}

@Composable
private fun MenuOpcion(
    titulo: String,
    descripcion: String,
    alPulsar: () -> Unit
) {

    Button(
        onClick = alPulsar,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Tarjeta,
            contentColor = Blanco
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(18.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            Text(
                text = titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = descripcion,
                fontSize = 13.sp,
                color = Gris
            )
        }
    }
}

@Composable
private fun PantallaOperacion(operacion: Operacion) {

    val entradas = remember(operacion.id) {
        mutableStateMapOf<String, String>()
    }

    var resultado by remember(operacion.id) {
        mutableStateOf<Resultado?>(null)
    }

    var error by remember(operacion.id) {
        mutableStateOf<String?>(null)
    }

    Text(
        text = operacion.nombre,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = Blanco
    )

    Text(
        text = operacion.descripcion,
        color = Gris
    )

    Spacer(Modifier.height(12.dp))

    operacion.campos.forEach { campo ->

        OutlinedTextField(
            value = entradas[campo.id].orEmpty(),
            onValueChange = {
                entradas[campo.id] = it
                resultado = null
                error = null
            },
            label = {
                Text(campo.nombre)
            },
            placeholder = {
                Text("Escribe un número")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }

    Spacer(Modifier.height(12.dp))

    Button(
        onClick = {

            try {

                val valores = operacion.campos.associate { campo ->

                    val numero = entradas[campo.id]
                        ?.trim()
                        ?.replace(',', '.')
                        ?.toDoubleOrNull()
                        ?: throw IllegalArgumentException(
                            "Introduce un número válido en: ${campo.nombre}"
                        )

                    campo.id to numero
                }

                resultado = Calculadora.calcular(
                    operacion,
                    valores
                )

                error = null

            } catch (ex: IllegalArgumentException) {

                resultado = null
                error = ex.message ?: "Revisa los datos."
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Acento,
            contentColor = Fondo
        )
    ) {

        Text(
            text = "Calcular",
            fontWeight = FontWeight.Bold
        )
    }

    error?.let {

        Text(
            text = it,
            color = ErrorColor
        )
    }

    resultado?.let { respuesta ->

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Tarjeta
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = respuesta.texto,
                    color = Acento,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                if (respuesta.detalle.isNotEmpty()) {

                    Text(
                        text = respuesta.detalle,
                        color = Gris
                    )
                }
            }
        }
    }
}


@Composable
private fun PantallaHistorial(
    modifier: Modifier = Modifier,
    historial: List<String>,
    volver: () -> Unit,
    borrarHistorial: () -> Unit
) {

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Botón para regresar a la calculadora
        TextButton(
            onClick = volver,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text(
                text = "← Volver a la calculadora",
                color = Acento,
                fontSize = 14.sp
            )
        }

        Text(
            text = "Historial",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Blanco
        )

        if (historial.isEmpty()) {

            Text(
                text = "Todavía no has realizado operaciones.",
                color = Gris
            )

        } else {

            historial.forEach { elemento ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Tarjeta
                    )
                ) {

                    Text(
                        text = elemento,
                        color = Blanco,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Button(
                onClick = borrarHistorial,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Tarjeta,
                    contentColor = Blanco
                )
            ) {
                Text("Borrar historial")
            }
        }
    }
}

// ----------------------------------------------------
// EVALUADOR DE EXPRESIONES CIENTÍFICAS
// ----------------------------------------------------

private class EvaluadorCientifico(
    private val entrada: String,
    private val grados: Boolean
) {

    private var posicion = 0

    fun evaluar(): Double {

        if (entrada.isBlank()) {
            throw IllegalArgumentException("Escribe una operación.")
        }

        val resultado = expresion()

        espacios()

        if (posicion < entrada.length) {
            throw IllegalArgumentException(
                "Expresión no válida cerca de: ${entrada.substring(posicion)}"
            )
        }

        if (!resultado.isFinite()) {
            throw IllegalArgumentException(
                "El resultado no es un número real válido."
            )
        }

        return resultado
    }

    private fun espacios() {

        while (
            posicion < entrada.length &&
            entrada[posicion].isWhitespace()
        ) {
            posicion++
        }
    }

    private fun consumir(caracter: Char): Boolean {

        espacios()

        if (
            posicion < entrada.length &&
            entrada[posicion] == caracter
        ) {
            posicion++
            return true
        }

        return false
    }

    private fun esperar(caracter: Char) {

        if (!consumir(caracter)) {
            throw IllegalArgumentException(
                "Falta el símbolo '$caracter'."
            )
        }
    }

    // Sumas y restas
    private fun expresion(): Double {

        var valor = termino()

        while (true) {

            when {

                consumir('+') -> {
                    valor += termino()
                }

                consumir('-') -> {
                    valor -= termino()
                }

                else -> return valor
            }
        }
    }

    // Multiplicaciones y divisiones
    private fun termino(): Double {

        var valor = unario()

        while (true) {

            when {

                consumir('*') -> {
                    valor *= unario()
                }

                consumir('/') -> {

                    val divisor = unario()

                    if (divisor == 0.0) {
                        throw IllegalArgumentException(
                            "No se puede dividir entre cero."
                        )
                    }

                    valor /= divisor
                }

                posicion < entrada.length &&
                        (entrada[posicion] == '(' ||
                                entrada[posicion].isLetter() ||
                                entrada[posicion] == 'π') -> {

                    // Multiplicación implícita: 2pi, 2(3+4), etc.
                    valor *= unario()
                }

                else -> return valor
            }
        }
    }

    private fun unario(): Double {

        return when {

            consumir('+') -> unario()

            consumir('-') -> -unario()

            else -> potencia()
        }
    }

    private fun potencia(): Double {

        val base = posterior()

        return if (consumir('^')) {
            base.pow(unario())
        } else {
            base
        }
    }

    // Porcentaje y factorial
    private fun posterior(): Double {

        var valor = primario()

        while (true) {

            when {

                consumir('%') -> {
                    valor /= 100.0
                }

                consumir('!') -> {

                    if (
                        valor < 0 ||
                        valor > 170 ||
                        valor % 1.0 != 0.0
                    ) {
                        throw IllegalArgumentException(
                            "El factorial requiere un entero entre 0 y 170."
                        )
                    }

                    var factorial = 1.0

                    for (i in 2..valor.toInt()) {
                        factorial *= i
                    }

                    valor = factorial
                }

                else -> return valor
            }
        }
    }

    private fun primario(): Double {

        espacios()

        if (posicion >= entrada.length) {
            throw IllegalArgumentException(
                "La expresión está incompleta."
            )
        }

        if (consumir('(')) {

            val valor = expresion()

            esperar(')')

            return valor
        }

        val caracter = entrada[posicion]

        if (caracter.isDigit() || caracter == '.') {
            return numero()
        }

        if (caracter.isLetter() || caracter == 'π') {
            return identificador()
        }

        throw IllegalArgumentException(
            "Símbolo no reconocido: $caracter"
        )
    }

    private fun numero(): Double {

        espacios()

        val inicio = posicion
        var punto = false
        var digito = false

        while (posicion < entrada.length) {

            val caracter = entrada[posicion]

            when {

                caracter.isDigit() -> {
                    digito = true
                    posicion++
                }

                caracter == '.' && !punto -> {
                    punto = true
                    posicion++
                }

                caracter == '.' && punto -> {
                    throw IllegalArgumentException(
                        "El número contiene más de un punto decimal."
                    )
                }

                else -> break
            }
        }

        if (!digito) {
            throw IllegalArgumentException(
                "Introduce un número válido."
            )
        }

        return entrada.substring(inicio, posicion).toDouble()
    }

    private fun identificador(): Double {

        espacios()

        if (entrada[posicion] == 'π') {
            posicion++
            return PI
        }

        val inicio = posicion

        while (
            posicion < entrada.length &&
            entrada[posicion].isLetter()
        ) {
            posicion++
        }

        val nombre = entrada.substring(inicio, posicion)
            .lowercase(Locale.US)

        when (nombre) {

            "pi" -> return PI

            "e" -> return Math.E
        }

        esperar('(')

        val argumento = expresion()

        esperar(')')

        val angulo = if (grados) {
            Math.toRadians(argumento)
        } else {
            argumento
        }

        return when (nombre) {

            "sin" -> sin(angulo)

            "cos" -> cos(angulo)

            "tan" -> tan(angulo)

            "sqrt" -> {
                if (argumento < 0) {
                    throw IllegalArgumentException(
                        "No existe una raíz cuadrada real de un número negativo."
                    )
                }

                sqrt(argumento)
            }

            "ln" -> {
                if (argumento <= 0) {
                    throw IllegalArgumentException(
                        "El logaritmo natural requiere un número positivo."
                    )
                }

                ln(argumento)
            }

            "log" -> {
                if (argumento <= 0) {
                    throw IllegalArgumentException(
                        "El logaritmo requiere un número positivo."
                    )
                }

                log10(argumento)
            }

            "abs" -> abs(argumento)

            "exp" -> exp(argumento)

            "asin" -> {
                val valor = asin(argumento)
                if (grados) Math.toDegrees(valor) else valor
            }

            "acos" -> {
                val valor = acos(argumento)
                if (grados) Math.toDegrees(valor) else valor
            }

            "atan" -> {
                val valor = atan(argumento)
                if (grados) Math.toDegrees(valor) else valor
            }

            else -> throw IllegalArgumentException(
                "Función no reconocida: $nombre"
            )
        }
    }
}

private fun formatearNumero(numero: Double): String {

    return DecimalFormat(
        "0.##########",
        DecimalFormatSymbols(Locale.US)
    ).format(numero)
}


private fun abrirFreedoom(context: Context) {

    val intent = Intent(
        context,
        DoomActivity::class.java
    )

    context.startActivity(intent)
}