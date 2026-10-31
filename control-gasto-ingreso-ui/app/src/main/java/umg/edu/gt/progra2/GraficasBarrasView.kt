package umg.edu.gt.progra2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import umg.edu.gt.progra2.modelo.ResumenPeriodo
import java.util.Locale

class GraficaBarrasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var datos: List<ResumenPeriodo> = emptyList()
    private val d = resources.displayMetrics.density

    private val pincelIngresos = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#0F7B6C") }
    private val pincelGastos = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#C4332F") }
    private val pincelEje = Paint().apply { color = Color.parseColor("#CFCDC8"); strokeWidth = 1.5f * d }
    private val pincelGuia = Paint().apply { color = Color.parseColor("#EFEEEC"); strokeWidth = 1f * d }
    private val pincelEtiqueta = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#6B6A66"); textSize = 10f * d; textAlign = Paint.Align.CENTER
    }
    private val pincelValor = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#37352F"); textSize = 9f * d; textAlign = Paint.Align.CENTER
    }
    private val pincelEscala = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#6B6A66"); textSize = 9f * d; textAlign = Paint.Align.RIGHT
    }

    fun mostrar(nuevos: List<ResumenPeriodo>) {
        datos = nuevos
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (datos.isEmpty()) {
            canvas.drawText("Sin movimientos", width / 2f, height / 2f, pincelEtiqueta)
            return
        }

        val izquierda = 34f * d
        val arriba = 18f * d
        val base = height - 26f * d
        val altoUtil = base - arriba
        val ancho = (width - izquierda) / datos.size
        val barra = ancho / 3f
        val maximo = datos.maxOf { maxOf(it.ingresos, it.gastos) }.coerceAtLeast(1.0)

        // Líneas guía al 50% y al 100%, con su valor a la izquierda
        for (fraccion in listOf(0.5f, 1f)) {
            val y = base - altoUtil * fraccion
            canvas.drawLine(izquierda, y, width.toFloat(), y, pincelGuia)
            canvas.drawText(corto(maximo * fraccion), izquierda - 4f * d, y + 3f * d, pincelEscala)
        }
        canvas.drawLine(izquierda, base, width.toFloat(), base, pincelEje)

        datos.forEachIndexed { i, dato ->
            val centro = izquierda + ancho * i + ancho / 2f
            val altoIngresos = (dato.ingresos / maximo * altoUtil).toFloat()
            val altoGastos = (dato.gastos / maximo * altoUtil).toFloat()
            val xIngresos = centro - barra / 2f - 1f * d
            val xGastos = centro + barra / 2f + 1f * d

            canvas.drawRect(centro - barra - 1f * d, base - altoIngresos, centro - 1f * d, base, pincelIngresos)
            canvas.drawRect(centro + 1f * d, base - altoGastos, centro + barra + 1f * d, base, pincelGastos)

            if (dato.ingresos > 0) canvas.drawText(corto(dato.ingresos), xIngresos, base - altoIngresos - 3f * d, pincelValor)
            if (dato.gastos > 0) canvas.drawText(corto(dato.gastos), xGastos, base - altoGastos - 3f * d, pincelValor)

            canvas.drawText(etiqueta(dato.periodo), centro, height - 8f * d, pincelEtiqueta)
        }
    }

    // 1250 -> "1.3k"   |   80 -> "80"
    private fun corto(v: Double): String =
        if (v >= 1000) String.format(Locale.US, "%.1fk", v / 1000) else String.format(Locale.US, "%.0f", v)

    // "2026-10-21" -> "21/10"   |   "2026-10" -> "10/26"
    private fun etiqueta(p: String): String =
        if (p.length == 10) "${p.substring(8, 10)}/${p.substring(5, 7)}"
        else "${p.substring(5, 7)}/${p.substring(2, 4)}"
}