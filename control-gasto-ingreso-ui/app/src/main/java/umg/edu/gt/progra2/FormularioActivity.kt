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
    private var editandoId: Int? = null      // null = movimiento nuevo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etFecha.setText(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()))
        mostrarCategorias(Tipo.GASTO)

        binding.rgTipo.setOnCheckedChangeListener { _, id ->
            mostrarCategorias(if (id == R.id.rbIngreso) Tipo.INGRESO else Tipo.GASTO)
        }
        binding.btnGuardar.setOnClickListener { guardar() }
        binding.btnCancelar.setOnClickListener { finish() }

        val id = intent.getIntExtra("id", -1)
        if (id != -1) cargarParaEditar(id)
    }

    private fun mostrarCategorias(tipo: Tipo) {
        val categorias = Categoria.entries.filter { it.tipo == tipo }
        val adaptador = ArrayAdapter(this, R.layout.item_spinner, categorias)
        adaptador.setDropDownViewResource(R.layout.item_spinner)
        binding.spCategoria.adapter = adaptador
    }

    private fun cargarParaEditar(id: Int) {
        lifecycleScope.launch {
            val m = control.buscar(id)
            if (m == null) {
                finish()
                return@launch
            }
            editandoId = id
            binding.tvTitulo.text = "Editar movimiento"
            binding.btnGuardar.text = "Guardar cambios"

            val tipo = m.categoria.tipo
            binding.rgTipo.check(if (tipo == Tipo.INGRESO) R.id.rbIngreso else R.id.rbGasto)
            mostrarCategorias(tipo)
            val posicion = Categoria.entries.filter { it.tipo == tipo }.indexOf(m.categoria)
            binding.spCategoria.setSelection(posicion)

            binding.etDescripcion.setText(m.descripcion)
            binding.etMonto.setText(m.monto.toString())
            binding.etFecha.setText(m.fecha)
        }
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

        val id = editandoId
        lifecycleScope.launch {
            val error = if (id == null) control.registrar(movimiento)
            else control.actualizar(movimiento.copy(id = id))
            if (error != null) {
                Toast.makeText(this@FormularioActivity, error, Toast.LENGTH_SHORT).show()
                return@launch
            }
            finish()
        }
    }
}