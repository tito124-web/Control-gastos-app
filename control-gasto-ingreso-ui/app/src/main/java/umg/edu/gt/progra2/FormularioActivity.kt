package umg.edu.gt.progra2

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import umg.edu.gt.progra2.databinding.ActivityFormularioBinding
import umg.edu.gt.progra2.modelo.Categoria
import umg.edu.gt.progra2.modelo.Movimiento
import umg.edu.gt.progra2.modelo.Tipo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FormularioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioBinding
    private val control = Dependencias.control

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etFecha.setText(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()))
        mostrarCategorias(Tipo.GASTO)

        // Al cambiar entre Gasto e Ingreso, el Spinner solo muestra las categorías de ese tipo
        binding.rgTipo.setOnCheckedChangeListener { _, id ->
            mostrarCategorias(if (id == R.id.rbIngreso) Tipo.INGRESO else Tipo.GASTO)
        }

        binding.btnGuardar.setOnClickListener { guardar() }
        binding.btnCancelar.setOnClickListener { finish() }
    }

    private fun mostrarCategorias(tipo: Tipo) {
        val categorias = Categoria.entries.filter { it.tipo == tipo }
        binding.spCategoria.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)
    }

    private fun guardar() {
        val monto = binding.etMonto.text.toString().toDoubleOrNull()
        if (monto == null) {
            binding.etMonto.error = "Ingresa un monto válido"
            return
        }

        val movimiento = Movimiento(
            descripcion = binding.etDescripcion.text.toString().trim(),
            monto = monto,
            categoria = binding.spCategoria.selectedItem as Categoria,
            fecha = binding.etFecha.text.toString().trim()
        )

        lifecycleScope.launch {
            val error = control.registrar(movimiento)      // las reglas viven en el core
            if (error != null) {
                Toast.makeText(this@FormularioActivity, error, Toast.LENGTH_SHORT).show()
                return@launch
            }
            finish()      // vuelve a la pantalla principal
        }
    }
}