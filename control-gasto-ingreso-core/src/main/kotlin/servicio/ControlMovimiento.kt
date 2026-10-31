package umg.edu.gt.progra2.servicio

import umg.edu.gt.progra2.datos.MovimientoRepository
import umg.edu.gt.progra2.modelo.Categoria
import umg.edu.gt.progra2.modelo.Movimiento
import umg.edu.gt.progra2.modelo.ResumenPeriodo
import umg.edu.gt.progra2.modelo.Tipo
import java.util.Locale

class ControlMovimiento(private val repositorio: MovimientoRepository) {

    // ---------- Reglas ----------

    fun valida(movimiento: Movimiento): String? {
        if (movimiento.descripcion.isBlank())
            return "La descripción no puede estar vacía"
        if (movimiento.monto <= 0)
            return "El monto debe ser mayor a 0"
        if (!FORMATO_FECHA.matches(movimiento.fecha))
            return "La fecha debe tener el formato yyyy-MM-dd"
        return null
    }

    // null significa "sin límite" y es válido
    fun validarLimite(limite: Double?): String? {
        if (limite != null && limite <= 0) return "El límite debe ser mayor que 0"
        return null
    }

    // ---------- Movimientos ----------

    suspend fun registrar(movimiento: Movimiento): String? {
        val error = valida(movimiento)
        if (error != null) return error
        repositorio.agregar(movimiento)
        return null
    }

    suspend fun buscar(id: Int): Movimiento? = repositorio.buscarPorId(id)

    suspend fun actualizar(movimiento: Movimiento): String? {
        val error = valida(movimiento)
        if (error != null) return error
        if (!repositorio.actualizar(movimiento)) {
            return "No existe el movimiento #${movimiento.id}"
        }
        return null
    }

    suspend fun eliminar(id: Int): Boolean = repositorio.eliminar(id)

    suspend fun listar(): List<Movimiento> =
        repositorio.listar().reversed().sortedByDescending { it.fecha }

    // ---------- Totales ----------

    suspend fun totalPorTipo(tipo: Tipo): Double {
        var suma = 0.0
        for (m in repositorio.listar()) {
            if (m.categoria.tipo == tipo) suma += m.monto
        }
        return suma
    }

    suspend fun balance(): Double = totalPorTipo(Tipo.INGRESO) - totalPorTipo(Tipo.GASTO)

    // ---------- Límite diario ----------

    suspend fun gastadoEnDia(fecha: String): Double {
        var suma = 0.0
        for (m in repositorio.listar()) {
            if (m.categoria.tipo == Tipo.GASTO && m.fecha == fecha) suma += m.monto
        }
        return suma
    }

    // Devuelve el aviso, o null si no hay límite o no hay nada que avisar
    suspend fun avisoLimiteDiario(limite: Double?, hoy: String): String? {
        if (limite == null) return null
        val gastado = gastadoEnDia(hoy)
        return when {
            gastado > limite -> "Superaste tu límite de hoy"
            gastado >= limite * 0.8 -> "Estás cerca de tu límite de hoy"
            else -> null
        }
    }

    // ---------- Estado financiero ----------

    suspend fun resumenPorDia(): List<ResumenPeriodo> = resumenPor(10)

    suspend fun resumenPorMes(): List<ResumenPeriodo> = resumenPor(7)

    // take(10) deja "yyyy-MM-dd" y take(7) deja "yyyy-MM"
    private suspend fun resumenPor(largo: Int): List<ResumenPeriodo> {
        val grupos = repositorio.listar().groupBy { it.fecha.take(largo) }
        return grupos.map { (periodo, lista) ->
            ResumenPeriodo(
                periodo = periodo,
                ingresos = lista.filter { it.categoria.tipo == Tipo.INGRESO }.sumOf { it.monto },
                gastos = lista.filter { it.categoria.tipo == Tipo.GASTO }.sumOf { it.monto }
            )
        }.sortedBy { it.periodo }
    }

    // Total de gastos de cada categoría en un mes ("2026-10"), de mayor a menor
    suspend fun gastosPorCategoria(mes: String): List<Pair<Categoria, Double>> {
        val totales = mutableMapOf<Categoria, Double>()
        for (m in repositorio.listar()) {
            if (m.categoria.tipo != Tipo.GASTO || !m.fecha.startsWith(mes)) continue
            totales[m.categoria] = (totales[m.categoria] ?: 0.0) + m.monto
        }
        return totales.toList().sortedByDescending { it.second }
    }

    private companion object {
        val FORMATO_FECHA = Regex("""\d{4}-\d{2}-\d{2}""")
    }
}

fun formatearQuetzales(monto: Double): String = "Q %,.2f".format(Locale.US, monto)