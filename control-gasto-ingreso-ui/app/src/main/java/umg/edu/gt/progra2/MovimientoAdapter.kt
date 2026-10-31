package umg.edu.gt.progra2

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import umg.edu.gt.progra2.modelo.Movimiento
import umg.edu.gt.progra2.modelo.Tipo
import umg.edu.gt.progra2.servicio.formatearQuetzales

class MovimientoAdapter(context: Context, private val datos: List<Movimiento>) :
    ArrayAdapter<Movimiento>(context, 0, datos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val vista = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_movimiento, parent, false)

        val m = datos[position]
        val esIngreso = m.categoria.tipo == Tipo.INGRESO

        vista.findViewById<TextView>(R.id.tvDescripcion).text = m.descripcion
        vista.findViewById<TextView>(R.id.tvCategoria).text = m.categoria.toString()
        vista.findViewById<TextView>(R.id.tvFecha).text = m.fecha

        val monto = vista.findViewById<TextView>(R.id.tvMonto)
        monto.text = (if (esIngreso) "+" else "−") + formatearQuetzales(m.monto)
        monto.setTextColor(Color.parseColor(if (esIngreso) "#0F7B6C" else "#C4332F"))

        return vista
    }
}