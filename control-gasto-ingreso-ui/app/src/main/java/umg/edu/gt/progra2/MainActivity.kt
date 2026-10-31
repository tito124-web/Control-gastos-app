package umg.edu.gt.progra2

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import umg.edu.gt.progra2.databinding.ActivityMainBinding
import umg.edu.gt.progra2.modelo.Movimiento
import umg.edu.gt.progra2.modelo.Tipo
import umg.edu.gt.progra2.servicio.formatearQuetzales
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val control = Dependencias.control
    private val usuarios = Dependencias.usuarios
    private val prefs by lazy { getSharedPreferences("config", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lvMovimientos.emptyView = binding.tvVacio

        binding.btnNuevo.setOnClickListener {
            startActivity(Intent(this, FormularioActivity::class.java))
        }
       // binding.btnEstado.setOnClickListener {
            //startActivity(Intent(this, EstadoFinancieroActivity::class.java))
        //}
        binding.tvLimite.setOnClickListener { pedirLimite() }
    }

    // Se ejecuta cada vez que la pantalla vuelve a verse, por ejemplo al regresar del formulario
    override fun onResume() {
        super.onResume()
        actualizarPantalla()
    }

    private fun actualizarPantalla() {
        lifecycleScope.launch {
            binding.tvSaludo.text = "Hola, ${usuarios.nombre() ?: ""}"
            binding.tvIngresos.text = formatearQuetzales(control.totalPorTipo(Tipo.INGRESO))
            binding.tvGastos.text = formatearQuetzales(control.totalPorTipo(Tipo.GASTO))
            binding.tvBalance.text = formatearQuetzales(control.balance())

            val lista = control.listar()

            binding.lvMovimientos.adapter = MovimientoAdapter(this@MainActivity, lista)

            binding.lvMovimientos.setOnItemClickListener { _, _, posicion, _ ->
                mostrarDetalle(lista[posicion])
            }
            binding.lvMovimientos.setOnItemLongClickListener { _, _, posicion, _ ->
                confirmarEliminar(lista[posicion])
                true
            }

            revisarLimite()
        }
    }


    private fun confirmarEliminar(m: Movimiento) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar movimiento")
            .setMessage("¿Eliminar \"${m.descripcion}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    control.eliminar(m.id)
                    actualizarPantalla()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    private fun leerLimite(): Double? = prefs.getString("limite_diario", null)?.toDoubleOrNull()

    private fun guardarLimite(limite: Double?) {
        val editor = prefs.edit()
        if (limite == null) editor.remove("limite_diario")
        else editor.putString("limite_diario", limite.toString())
        editor.apply()
    }

    private fun hoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private fun pedirLimite() {
        val campo = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Ej. 100"
            leerLimite()?.let { setText(it.toString()) }
        }

        AlertDialog.Builder(this)
            .setTitle("Límite diario de gastos")
            .setMessage("Déjalo vacío si no quieres límite")
            .setView(campo)
            .setPositiveButton("Guardar") { _, _ ->
                val texto = campo.text.toString().trim()
                val limite = if (texto.isEmpty()) null else texto.toDoubleOrNull()

                if (texto.isNotEmpty() && limite == null) {
                    Toast.makeText(this, "Ingresa un número válido", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val error = control.validarLimite(limite)
                if (error != null) {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                guardarLimite(limite)
                lifecycleScope.launch { revisarLimite() }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private suspend fun revisarLimite() {
        val limite = leerLimite()
        binding.tvLimite.text =
            if (limite == null) "Sin límite diario · Poner límite"
            else "Límite diario: ${formatearQuetzales(limite)} · Cambiar"

        binding.tvAviso.text = control.avisoLimite(limite, hoy()) ?: ""
    }


    private fun mostrarDetalle(m: Movimiento) {
        val tipo = if (m.categoria.tipo == Tipo.INGRESO) "Ingreso" else "Gasto"
        val detalle = "Descripción: ${m.descripcion}\n" +
                "Tipo: $tipo\n" +
                "Categoría: ${m.categoria}\n" +
                "Monto: ${formatearQuetzales(m.monto)}\n" +
                "Fecha: ${m.fecha}"

        AlertDialog.Builder(this)
            .setTitle("Detalle del movimiento")
            .setMessage(detalle)
            .setPositiveButton("Cerrar", null)
            .setNegativeButton("Eliminar") { _, _ -> confirmarEliminar(m) }
            .show()
    }
}
