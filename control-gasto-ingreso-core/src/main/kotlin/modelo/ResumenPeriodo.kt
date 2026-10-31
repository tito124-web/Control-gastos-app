package umg.edu.gt.progra2.modelo

data class ResumenPeriodo(
    val periodo: String,      // "2026-10-30" (día) o "2026-10" (mes)
    val ingresos: Double,
    val gastos: Double
) {
    val balance: Double get() = ingresos - gastos
}