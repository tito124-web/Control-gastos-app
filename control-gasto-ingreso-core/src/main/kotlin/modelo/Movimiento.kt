package umg.edu.gt.progra2.modelo

data class Movimiento(
    val id: Int = 0,
    val descipcion: String,
    val monto: Double,
    val categoria: Categoria,
    val fecha: String,

)
