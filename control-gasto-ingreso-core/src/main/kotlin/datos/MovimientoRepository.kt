package umg.edu.gt.progra2.datos

import umg.edu.gt.progra2.modelo.Movimiento

interface MovimientoRepository {

    suspend fun agregar(movimiento: Movimiento): Movimiento

    suspend fun listar(): List<Movimiento>

    suspend fun buscarPorId(id: Int): Movimiento?

    suspend fun actualizar(movimiento: Movimiento): Boolean

    suspend fun eliminar(id: Int): Boolean

}