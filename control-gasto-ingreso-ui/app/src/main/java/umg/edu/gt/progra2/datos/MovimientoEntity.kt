package umg.edu.gt.progra2.datos


import androidx.room.Entity
import androidx.room.PrimaryKey
import umg.edu.gt.progra2.modelo.Categoria
import umg.edu.gt.progra2.modelo.Movimiento


/**
 * Una FILA de la tabla "movimientos" en SQLite.
 */
@Entity(tableName = "movimientos")
data class MovimientoEntity(
    // 1. Llave primaria. autoGenerate = true es el AUTO_INCREMENT de MySQL:
    //    si llega id = 0, SQLite le asigna el siguiente numero.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descripcion: String,
    val monto: Double,
    // 2. SQLite no sabe que es un enum: guardamos su NOMBRE como texto ("ALIMENTACION").
    // Cuidado: no guardes toString() ("Alimentación", puede cambiar) ni ordinal (cambia si
    // alguien reordena el enum). name es el valor mas estable.
    val categoria: String,
    val fecha: String
) {
    /** Fila de la tabla -> objeto de la libreria. */
    fun aMovimiento(): Movimiento = Movimiento(
        id = id,
        descripcion = descripcion,
        monto = monto,
        categoria = Categoria.valueOf(categoria),  // "ALIMENTACION" -> Categoria.ALIMENTACION
        fecha = fecha
    )

    companion object {
        /** Objeto de la libreria -> fila de la tabla. */
        fun desde( movimineto: Movimiento):  MovimientoEntity = MovimientoEntity(
            id = movimineto.id,
            descripcion =  movimineto.descripcion,
            monto = movimineto.monto,
            categoria = movimineto.categoria.name,
            fecha = movimineto.fecha
        )
    }
}