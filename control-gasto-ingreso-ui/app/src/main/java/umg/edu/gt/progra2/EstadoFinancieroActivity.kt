package umg.edu.gt.progra2

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import umg.edu.gt.progra2.databinding.ActivityEstadoFinancieroBinding
import umg.edu.gt.progra2.servicio.formatearQuetzales
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EstadoFinancieroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEstadoFinancieroBinding
    private val control = Dependencias.control

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEstadoFinancieroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDia.setOnClickListener { cargar(porDia = true) }
        binding.btnMes.setOnClickListener { cargar(porDia = false) }
        cargar(porDia = true)
    }

    private fun cargar(porDia: Boolean) {
        marcarBoton(
            activo = if (porDia) binding.btnDia else binding.btnMes,
            inactivo = if (porDia) binding.btnMes else binding.btnDia
        )

        lifecycleScope.launch {
            val resumen = if (porDia) control.resumenPorDia().takeLast(7)
            else control.resumenPorMes().takeLast(6)

            val unidad = if (porDia) "días" else "meses"
            binding.tvRango.text =
                if (resumen.isEmpty()) "Aún no hay movimientos"
                else "Últimos ${resumen.size} $unidad con movimientos"

            val ingresos = resumen.sumOf { it.ingresos }
            val gastos = resumen.sumOf { it.gastos }
            binding.tvTotIngresos.text = formatearQuetzales(ingresos)
            binding.tvTotGastos.text = formatearQuetzales(gastos)
            binding.tvTotBalance.text = formatearQuetzales(ingresos - gastos)
            binding.tvTotBalance.setTextColor(
                Color.parseColor(if (ingresos - gastos >= 0) "#0F7B6C" else "#C4332F")
            )

            binding.grafica.mostrar(resumen)

            binding.contResumen.removeAllViews()
            resumen.reversed().forEach { d ->
                agregarFila(
                    binding.contResumen, d.periodo,
                    "Ingresos ${formatearQuetzales(d.ingresos)} · " +
                            "Gastos ${formatearQuetzales(d.gastos)} · " +
                            "Balance ${formatearQuetzales(d.balance)}"
                )
            }

            mostrarCategorias()
        }
    }

    private suspend fun mostrarCategorias() {
        val mes = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
        val lista = control.gastosPorCategoria(mes)
        val total = lista.sumOf { it.second }

        binding.contCategorias.removeAllViews()
        if (lista.isEmpty()) {
            agregarFila(binding.contCategorias, "Sin gastos este mes", "")
            return
        }
        lista.forEach { (categoria, monto) ->
            val porcentaje = "%.0f".format(monto / total * 100)
            agregarFila(binding.contCategorias, "$categoria · $porcentaje%", formatearQuetzales(monto))
        }
    }

    private fun marcarBoton(activo: Button, inactivo: Button) {
        activo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1B6FC2"))
        activo.setTextColor(Color.WHITE)
        inactivo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F1F0EE"))
        inactivo.setTextColor(Color.parseColor("#37352F"))
    }

    private fun dp(valor: Int) = (valor * resources.displayMetrics.density).toInt()

    // Agrega una fila de dos líneas (título y detalle) con una línea separadora
    private fun agregarFila(contenedor: LinearLayout, titulo: String, detalle: String) {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(8), 0, dp(8))
        }
        fila.addView(TextView(this).apply {
            text = titulo
            textSize = 13f
            setTextColor(Color.parseColor("#37352F"))
            setTypeface(typeface, Typeface.BOLD)
        })
        if (detalle.isNotEmpty()) {
            fila.addView(TextView(this).apply {
                text = detalle
                textSize = 12f
                setTextColor(Color.parseColor("#6B6A66"))
            })
        }
        contenedor.addView(fila)
        contenedor.addView(View(this).apply {
            setBackgroundColor(Color.parseColor("#E9E9E7"))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1))
        })
    }
}