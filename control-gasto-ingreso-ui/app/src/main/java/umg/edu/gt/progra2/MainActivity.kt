package umg.edu.gt.progra2

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var adapter: MovimientoAdapter
    private val control = Dependencias.control
    private val usuarios = Dependencias.usuarios
    private val prefs by lazy { getSharedPreferences("config", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MovimientoAdapter(emptyList()) { m -> editar(m) }
        binding.rvMovimientos.layoutManager = LinearLayoutManager(this)
        binding.rvMovimientos.adapter = adapter
        binding.rvMovimientos.itemAnimator = null
        activarDeslizar()

        binding.btnNuevo.setOnClickListener {
            startActivity(Intent(this, FormularioActivity::class.java))
        }
        binding.btnEstado.setOnClickListener {
            startActivity(Intent(this, EstadoFinancieroActivity::class.java))
        }
        binding.tvLimite.setOnClickListener { pedirLimite() }
    }

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
            adapter.actualizarDatos(lista)
            binding.tvVacio.visibility =
                if (lista.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE

            revisarLimite()
        }
    }

    private fun editar(m: Movimiento) {
        startActivity(Intent(this, FormularioActivity::class.java).putExtra("id", m.id))
    }

    // Deslizar una fila hacia la izquierda la elimina, con opción de deshacer
    private fun activarDeslizar() {
        val densidad = resources.displayMetrics.density
        val fondo = Paint().apply { color = Color.parseColor("#C4332F") }
        val texto = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 13f * densidad
            textAlign = Paint.Align.RIGHT
        }

        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                rv: RecyclerView,
                v: RecyclerView.ViewHolder,
                t: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(holder: RecyclerView.ViewHolder, direction: Int) {
                val pos = holder.bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return
                eliminarConDeshacer(adapter.movimientoEn(pos))
            }

            override fun onChildDraw(
                c: Canvas, rv: RecyclerView, holder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                val v = holder.itemView
                c.drawRect(v.right + dX, v.top.toFloat(), v.right.toFloat(), v.bottom.toFloat(), fondo)
                if (dX < 0) {
                    c.drawText(
                        "Eliminar", v.right - 16f * densidad,
                        v.top + v.height / 2f + texto.textSize / 3f, texto
                    )
                }
                super.onChildDraw(c, rv, holder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.rvMovimientos)
    }

    private fun eliminarConDeshacer(m: Movimiento) {
        lifecycleScope.launch {
            control.eliminar(m.id)
            actualizarPantalla()
            Snackbar.make(binding.root, "Movimiento eliminado", Snackbar.LENGTH_LONG)
                .setAction("Deshacer") {
                    lifecycleScope.launch {
                        control.registrar(m)
                        actualizarPantalla()
                    }
                }.show()
        }
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
            setTextColor(Color.parseColor("#37352F"))
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

        binding.tvAviso.text = control.avisoLimiteDiario(limite, hoy()) ?: ""
    }
}