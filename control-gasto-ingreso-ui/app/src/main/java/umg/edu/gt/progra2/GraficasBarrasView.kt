package umg.edu.gt.progra2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import umg.edu.gt.progra2.modelo.ResumenPeriodo

class GraficaBarrasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var datos: List<ResumenPeriodo> = emptyList()
    private val pincelIngresos = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#0F7B6C") }
    private val pincelGastos = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#C4332F") }
    private val pincelEje = Paint().apply { color = Color.parseColor("#E9E9E7"); strokeWidth = 2f }
    private val pincelTexto = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#6B6A66")
        textSize = 11f * resources.displayMetrics.density
        textAlign = Paint.Align.CENTER
    }

    fun mostrar(nuevos: List<ResumenPeriodo>) {
        datos = nuevos
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (datos.isEmpty()) {
            canvas.drawText("Sin movimientos", width / 2f, height / 2f, pincelTexto)
            return
        }
        val maximo = datos.maxOf { maxOf(it.ingresos, it.gastos) }.coerceAtLeast(1.0)
        val base = height - 50f
        val altoUtil = base - 16f
        val ancho = width / datos.size.toFloat()
        val barra = ancho / 4f

        canvas.drawLine(0f, base, width.toFloat(), base, pincelEje)

        datos.forEachIndexed { i, d ->
            val centro = ancho * i + ancho / 2f
            val altoIngresos = (d.ingresos / maximo * altoUtil).toFloat()
            val altoGastos = (d.gastos / maximo * altoUtil).toFloat()
            canvas.drawRect(centro - barra - 2f, base - altoIngresos, centro - 2f, base, pincelIngresos)
            canvas.drawRect(centro + 2f, base - altoGastos, centro + barra + 2f, base, pincelGastos)
            canvas.drawText(etiqueta(d.periodo), centro, height - 16f, pincelTexto)
        }
    }

    private fun etiqueta(p: String): String =
        if (p.length == 10) "${p.substring(8, 10)}/${p.substring(5, 7)}"
        else "${p.substring(5, 7)}/${p.substring(2, 4)}"
}