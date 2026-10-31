package umg.edu.gt.progra2

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import umg.edu.gt.progra2.databinding.ActivityEstadoFinancieroBinding
import umg.edu.gt.progra2.servicio.formatearQuetzales

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
        lifecycleScope.launch {
            val resumen = if (porDia) control.resumenPorDia().takeLast(7)
            else control.resumenPorMes().takeLast(6)

            binding.grafica.mostrar(resumen)

            val textos = resumen.reversed().map { d ->
                "${d.periodo}\n" +
                        "Ingresos ${formatearQuetzales(d.ingresos)}  ·  Gastos ${formatearQuetzales(d.gastos)}\n" +
                        "Balance ${formatearQuetzales(d.balance)}"
            }
            binding.lvResumen.adapter =
                ArrayAdapter(this@EstadoFinancieroActivity, R.layout.item_resumen, textos)
        }
    }
}