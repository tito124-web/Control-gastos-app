package umg.edu.gt.progra2

import android.graphics.Color
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import umg.edu.gt.progra2.modelo.Movimiento
import umg.edu.gt.progra2.modelo.Tipo
import umg.edu.gt.progra2.servicio.formatearQuetzales

class MovimientoAdapter(
    private var datos: List<Movimiento>,
    private val alActualizar: (Movimiento) -> Unit
) : RecyclerView.Adapter<MovimientoAdapter.Fila>() {

    private var expandidoId: Int? = null
    private var recycler: RecyclerView? = null

    class Fila(vista: View) : RecyclerView.ViewHolder(vista) {
        val tvDescripcion: TextView = vista.findViewById(R.id.tvDescripcion)
        val tvCategoria: TextView = vista.findViewById(R.id.tvCategoria)
        val tvMonto: TextView = vista.findViewById(R.id.tvMonto)
        val tvFecha: TextView = vista.findViewById(R.id.tvFecha)
        val detalle: View = vista.findViewById(R.id.detalle)
        val tvDetalleDescripcion: TextView = vista.findViewById(R.id.tvDetalleDescripcion)
        val tvDetalleTipo: TextView = vista.findViewById(R.id.tvDetalleTipo)
        val btnActualizar: Button = vista.findViewById(R.id.btnActualizar)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recycler = recyclerView
    }

    override fun getItemCount() = datos.size

    fun movimientoEn(posicion: Int): Movimiento = datos[posicion]

    fun actualizarDatos(nuevos: List<Movimiento>) {
        datos = nuevos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Fila =
        Fila(LayoutInflater.from(parent.context).inflate(R.layout.item_movimiento, parent, false))

    override fun onBindViewHolder(f: Fila, posicion: Int) {
        val m = datos[posicion]
        val esIngreso = m.categoria.tipo == Tipo.INGRESO

        f.tvDescripcion.text = m.descripcion
        f.tvCategoria.text = m.categoria.toString()
        f.tvFecha.text = m.fecha
        f.tvMonto.text = (if (esIngreso) "+" else "−") + formatearQuetzales(m.monto)
        f.tvMonto.setTextColor(Color.parseColor(if (esIngreso) "#0F7B6C" else "#C4332F"))

        f.tvDetalleDescripcion.text = m.descripcion
        f.tvDetalleTipo.text = if (esIngreso) "Tipo: Ingreso" else "Tipo: Gasto"
        f.detalle.visibility = if (expandidoId == m.id) View.VISIBLE else View.GONE

        f.itemView.setOnClickListener {
            val pos = f.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            recycler?.let { TransitionManager.beginDelayedTransition(it) }
            val anterior = expandidoId
            expandidoId = if (anterior == m.id) null else m.id

            notifyItemChanged(pos)
            val posAnterior = datos.indexOfFirst { it.id == anterior }
            if (anterior != null && posAnterior >= 0) notifyItemChanged(posAnterior)
        }

        f.btnActualizar.setOnClickListener { alActualizar(m) }
    }
}