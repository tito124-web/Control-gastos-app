package umg.edu.gt.progra2.datos

import umg.edu.gt.progra2.modelo.Movimiento

class MovimientoRepositoryRoom(private val dao: MovimientoDao) : MovimientoRepository {

    override suspend fun agregar(movimiento: Movimiento): Movimiento {
        val id = dao.insertar(MovimientoEntity.desde(movimiento.copy(id = 0)))
        return movimiento.copy(id = id.toInt())
    }

    override suspend fun listar(): List<Movimiento> = dao.listar().map { it.aMovimiento() }

    override suspend fun buscarPorId(id: Int): Movimiento? = dao.buscarPorId(id)?.aMovimiento()

    override suspend fun actualizar(movimiento: Movimiento): Boolean =
        dao.actualizar(MovimientoEntity.desde(movimiento)) > 0

    override suspend fun eliminar(id: Int): Boolean = dao.eliminar(id) > 0
}