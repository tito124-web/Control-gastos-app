package umg.edu.gt.progra2.servicio

import umg.edu.gt.progra2.datos.MovimientoRepository
import umg.edu.gt.progra2.modelo.Categoria
import umg.edu.gt.progra2.modelo.Movimiento
import umg.edu.gt.progra2.modelo.Tipo

class ControlMovimiento(private  val repositorio: MovimientoRepository) {

    fun valida(movimiento: Movimiento): String? {
        if (movimiento.descripcion.isBlank())
            return "La descripción no puede estar vacía"
        if (movimiento.monto <= 0)
            return "El monto debe der ser mayor a 0"
        if (!FORMATO_FECHA.matches(movimiento.fecha))
            return "La fecha debe tener el formato yyyy-MM-dd"

        return null
    }

    suspend fun registrar(movimiento: Movimiento): String? {
        val error = valida(movimiento)
        if (error != null) {
            return error
        }
        repositorio.agregar(movimiento)
        return null
    }

    suspend fun bucar(id: Int): Movimiento? = repositorio.buscarPorId(id)


    suspend fun actualizar(movimiento: Movimiento): String? {
        val error = valida(movimiento)
        if (error != null) {
            return error
        }
        if(!repositorio.actualizar(movimiento)){
            return "no existe el movimiento  #${movimiento.id}"
        }
        return null
    }

    suspend fun eliminar(id: Int): Boolean? = repositorio.eliminar(id)




    suspend fun listar(): List<Movimiento> =
        repositorio.listar().reversed().sortedByDescending { it.fecha }

    suspend fun totalPorTipo(tipo: Tipo): Double {
        var suma = 0.0
        for (m in repositorio.listar()) {
            if (m.categoria.tipo == tipo ) suma += m.monto
        }

        return suma
    }

    suspend fun balance(): Double = totalPorTipo(Tipo.INGRESO) - totalPorTipo(Tipo.GASTO)


    suspend fun verificarLimite(limite: Double): String? {
        if (limite <= 0.0) return  "El limite debe der ser mayor no ser mayor que 0"
        return null
    }


    suspend fun gastadoEnMes( mes: String): Double {
        var suma = 0.0
        for (m in repositorio.listar()) {
            if (m.categoria.tipo == Tipo.GASTO && m.fecha.startsWith(mes)) suma += m.monto
        }
        return suma
    }

    suspend fun validarlimite(limite: Double?, mes: String): String? {
        if (limite == null) return null
        val gastado = gastadoEnMes(mes)
       return when {
           gastado > limite -> "Has superado el limite de gastos"
           gastado >= limite * 0.08  -> "Estas cerca del limite de gastos"
           else -> null
       }
       }










    private companion object {
        val FORMATO_FECHA = Regex("""\d{4}-\d{2}-\d{2}""")


    }
}
        fun formatearQuetzales(monto: Double): String = "Q %.2f".format(monto)

