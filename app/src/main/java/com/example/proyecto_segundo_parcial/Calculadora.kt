package com.example.proyecto_segundo_parcial

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

/** Lógica del programa original, separada de la interfaz de Android. */
data class Campo(val id: String, val nombre: String)

data class Operacion(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val campos: List<Campo>,
    val esEcuacion: Boolean = false
)

data class Resultado(val texto: String, val detalle: String = "")

object Calculadora {
    val areas = listOf(
        Operacion("equilatero", "Triángulo equilátero", "Todos sus lados son iguales.", listOf(Campo("base", "Base (cm)"), Campo("altura", "Altura (cm)"), Campo("lado", "Lado (cm)"))),
        Operacion("isosceles", "Triángulo isósceles", "Tiene dos lados iguales.", listOf(Campo("base", "Base (cm)"), Campo("altura", "Altura (cm)"), Campo("igual", "Uno de los lados iguales (cm)"), Campo("distinto", "Lado diferente (cm)"))),
        Operacion("escaleno", "Triángulo escaleno", "Sus tres lados son diferentes.", listOf(Campo("base", "Base (cm)"), Campo("altura", "Altura (cm)"), Campo("lado1", "Primer lado (cm)"), Campo("lado2", "Segundo lado (cm)"), Campo("lado3", "Tercer lado (cm)"))),
        Operacion("circulo", "Círculo", "Área y perímetro (circunferencia).", listOf(Campo("radio", "Radio (cm)"))),
        Operacion("rectangulo", "Rectángulo", "Calcula con base y altura.", listOf(Campo("base", "Base (cm)"), Campo("altura", "Altura (cm)"))),
        Operacion("cuadrado", "Cuadrado", "Los cuatro lados son iguales.", listOf(Campo("lado", "Lado (cm)"))),
        Operacion("pentagono", "Pentágono regular", "Necesita el lado y la apotema.", listOf(Campo("lado", "Lado (cm)"), Campo("apotema", "Apotema (cm)"))),
        Operacion("trapecio", "Trapecio", "Utiliza sus dos bases, la altura y los lados laterales.", listOf(Campo("mayor", "Base mayor (cm)"), Campo("menor", "Base menor (cm)"), Campo("altura", "Altura (cm)"), Campo("izq", "Lado izquierdo (cm)"), Campo("der", "Lado derecho (cm)"))),
        Operacion("rombo", "Rombo", "Utiliza diagonales para el área y lado para el perímetro.", listOf(Campo("diagMayor", "Diagonal mayor (cm)"), Campo("diagMenor", "Diagonal menor (cm)"), Campo("lado", "Lado (cm)")))
    )

    val volumenes = listOf(
        Operacion("esfera", "Esfera", "Volumen = 4/3 × π × radio³.", listOf(Campo("radio", "Radio (cm)"))),
        Operacion("piramide", "Pirámide", "Volumen = área de la base × altura / 3.", listOf(Campo("areaBase", "Área de la base (cm²)"), Campo("altura", "Altura (cm)"))),
        Operacion("cubo", "Cubo", "Volumen = lado³.", listOf(Campo("lado", "Lado (cm)"))),
        Operacion("cilindro", "Cilindro", "Volumen = π × radio² × altura.", listOf(Campo("radio", "Radio (cm)"), Campo("altura", "Altura (cm)"))),
        Operacion("paralelepipedo", "Paralelepípedo", "Volumen = ancho × profundidad × altura.", listOf(Campo("ancho", "Ancho (cm)"), Campo("profundidad", "Profundidad (cm)"), Campo("altura", "Altura (cm)")))
    )

    val ecuacion = Operacion(
        "ecuacion", "Fórmula general", "Resuelve ax² + bx + c = 0. El valor de a no puede ser 0.",
        listOf(Campo("a", "Coeficiente a"), Campo("b", "Coeficiente b"), Campo("c", "Coeficiente c")),
        esEcuacion = true
    )

    private fun numero(n: Double): String {
        val formato = DecimalFormat("0.####", DecimalFormatSymbols(Locale.forLanguageTag("es-MX")))
        return formato.format(if (n == 0.0) 0.0 else n)
    }

    fun calcular(operacion: Operacion, valores: Map<String, Double>): Resultado {
        for (campo in operacion.campos) {
            val valor = valores[campo.id]
                ?: throw IllegalArgumentException("Falta ${campo.nombre}.")
            if (!valor.isFinite()) throw IllegalArgumentException("Introduce números válidos.")
            if (!operacion.esEcuacion && valor <= 0.0) {
                throw IllegalArgumentException("Las medidas deben ser mayores que cero.")
            }
        }
        fun v(id: String): Double = valores.getValue(id)
        fun areaYPerimetro(area: Double, perimetro: Double) = Resultado(
            "Área: ${numero(area)} cm²\nPerímetro: ${numero(perimetro)} cm"
        )
        fun volumen(valor: Double) = Resultado("Volumen: ${numero(valor)} cm³")

        return when (operacion.id) {
            "equilatero" -> areaYPerimetro(v("base") * v("altura") / 2, 3 * v("lado"))
            "isosceles" -> areaYPerimetro(v("base") * v("altura") / 2, 2 * v("igual") + v("distinto"))
            "escaleno" -> areaYPerimetro(v("base") * v("altura") / 2, v("lado1") + v("lado2") + v("lado3"))
            "circulo" -> areaYPerimetro(PI * v("radio").pow(2), 2 * PI * v("radio"))
            "rectangulo" -> areaYPerimetro(v("base") * v("altura"), 2 * (v("base") + v("altura")))
            "cuadrado" -> areaYPerimetro(v("lado").pow(2), 4 * v("lado"))
            "pentagono" -> {
                val perimetro = 5 * v("lado")
                areaYPerimetro(perimetro * v("apotema") / 2, perimetro)
            }
            "trapecio" -> {
                if (v("mayor") < v("menor")) throw IllegalArgumentException("La base mayor debe ser igual o mayor que la menor.")
                areaYPerimetro((v("mayor") + v("menor")) * v("altura") / 2,
                    v("mayor") + v("menor") + v("izq") + v("der"))
            }
            "rombo" -> areaYPerimetro(v("diagMayor") * v("diagMenor") / 2, 4 * v("lado"))
            "esfera" -> volumen((4.0 / 3.0) * PI * v("radio").pow(3))
            "piramide" -> volumen(v("areaBase") * v("altura") / 3)
            "cubo" -> volumen(v("lado").pow(3))
            "cilindro" -> volumen(PI * v("radio").pow(2) * v("altura"))
            "paralelepipedo" -> volumen(v("ancho") * v("profundidad") * v("altura"))
            "ecuacion" -> {
                val a = v("a")
                val b = v("b")
                val c = v("c")
                if (a == 0.0) throw IllegalArgumentException("El coeficiente a no puede ser cero: no sería de segundo grado.")
                val discriminante = b * b - 4 * a * c
                if (discriminante > 0) {
                    val x1 = (-b + sqrt(discriminante)) / (2 * a)
                    val x2 = (-b - sqrt(discriminante)) / (2 * a)
                    Resultado("x₁ = ${numero(x1)}\nx₂ = ${numero(x2)}", "Dos soluciones reales.")
                } else if (discriminante == 0.0) {
                    Resultado("x₁ = x₂ = ${numero(-b / (2 * a))}", "Una solución real doble.")
                } else {
                    val real = -b / (2 * a)
                    val imaginaria = sqrt(-discriminante) / (2 * kotlin.math.abs(a))
                    Resultado("x₁ = ${numero(real)} + ${numero(imaginaria)}i\nx₂ = ${numero(real)} − ${numero(imaginaria)}i",
                        "No hay soluciones reales; las dos soluciones son complejas.")
                }
            }
            else -> throw IllegalArgumentException("Operación no encontrada.")
        }
    }
}
