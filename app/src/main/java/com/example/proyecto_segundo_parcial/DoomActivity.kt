package com.example.proyecto_segundo_parcial

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlin.concurrent.thread
import java.util.concurrent.atomic.AtomicBoolean

/** Primera pantalla experimental que intenta ejecutar Doomgeneric con Freedoom Phase 1. */
class DoomActivity : ComponentActivity() {
    private val running = AtomicBoolean(false)
    private val updatePending = AtomicBoolean(false)
    private val paused = AtomicBoolean(false)
    private lateinit var frameView: DoomFrameView
    private lateinit var status: TextView
    private var worker: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(13, 22, 37))
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        header.addView(Button(this).apply {
            text = "← Volver"
            setOnClickListener { finish() }
        })
        status = TextView(this).apply {
            text = "Preparando Freedoom..."
            setTextColor(Color.WHITE)
            textSize = 12f
            setPadding(12, 0, 0, 0)
        }
        header.addView(status)
        layout.addView(header)

        frameView = DoomFrameView()
        layout.addView(frameView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        val keys = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.rgb(26, 41, 63))
        }
        listOf(
            "←" to DoomNative.LEFT,
            "↑" to DoomNative.UP,
            "↓" to DoomNative.DOWN,
            "→" to DoomNative.RIGHT,
            "FIRE" to DoomNative.FIRE,
            "USE" to DoomNative.USE,
            "ENTER" to DoomNative.ENTER,
            "ESC" to DoomNative.ESCAPE,
            "MAP" to DoomNative.TAB
        ).forEach { (name, code) ->
            val key = Button(this).apply {
                text = name
                textSize = 10f
                isAllCaps = false
                setPadding(0, 0, 0, 0)
                setOnTouchListener { view, event ->
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            DoomNative.tecla(code, true)
                            true
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            DoomNative.tecla(code, false)
                            view.performClick()
                            true
                        }
                        else -> true
                    }
                }
            }
            keys.addView(key, LinearLayout.LayoutParams(0, 56.dp(), 1f))
        }
        layout.addView(keys)
        setContentView(layout)

        running.set(true)
        worker = thread(name = "GeoDoom-Engine") {
            try {

                val info = DoomAssets.preparar(applicationContext)

                if (!running.get()) return@thread
                val ok = DoomNative.iniciar(info.archivo.absolutePath, filesDir.absolutePath)
                if (!ok) {
                    runOnUiThread { status.text = "No se pudo iniciar el WAD" }
                    return@thread
                }
                runOnUiThread { status.text = "DOOM Phase 1 · prueba experimental (sin audio)" }
                val pixels = IntArray(DoomNative.WIDTH * DoomNative.HEIGHT)
                while (running.get()) {
                    if (paused.get()) {
                        Thread.sleep(100)
                        continue
                    }
                    if (!DoomNative.cuadro(pixels)) break
                    if (updatePending.compareAndSet(false, true)) {
                        val snapshot = pixels.copyOf()
                        runOnUiThread {
                            if (!isFinishing && !isDestroyed) frameView.update(snapshot)
                            updatePending.set(false)
                        }
                    }
                    Thread.sleep(24)
                }
            } catch (e: Exception) {
                runOnUiThread { status.text = "Error: ${e.message}" }
            }
        }
    }

    override fun onPause() {
        paused.set(true)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        paused.set(false)
    }

    override fun onDestroy() {
        running.set(false)
        super.onDestroy()
    }

    private fun Int.dp(): Int = (this * resources.displayMetrics.density).toInt()

    private inner class DoomFrameView : View(this@DoomActivity) {
        private val bitmap = Bitmap.createBitmap(
            DoomNative.WIDTH, DoomNative.HEIGHT, Bitmap.Config.ARGB_8888
        )
        private val paint = Paint(Paint.FILTER_BITMAP_FLAG)
        private val src = Rect(0, 0, DoomNative.WIDTH, DoomNative.HEIGHT)
        private val dest = Rect()

        fun update(pixels: IntArray) {
            bitmap.setPixels(pixels, 0, DoomNative.WIDTH, 0, 0,
                DoomNative.WIDTH, DoomNative.HEIGHT)
            invalidate()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawColor(Color.BLACK)
            val aspect = DoomNative.WIDTH.toFloat() / DoomNative.HEIGHT
            val w = minOf(width.toFloat(), height.toFloat() * aspect)
            val h = w / aspect
            val left = ((width - w) / 2).toInt()
            val top = ((height - h) / 2).toInt()
            dest.set(left, top, left + w.toInt(), top + h.toInt())
            canvas.drawBitmap(bitmap, src, dest, paint)
        }
    }
}
