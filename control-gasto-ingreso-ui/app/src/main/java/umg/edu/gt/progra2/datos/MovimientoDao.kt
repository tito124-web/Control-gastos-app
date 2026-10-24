package umg.edu.gt.progra2.datos

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

interface MovimientoDao {


    // Devuelve el id que SQLite genero para la fila nueva (Long).
    @Insert
    suspend fun insertar(movimiento: MovimientoEntity): Long

    // Cuidado: ordenamos por id (orden de llegada), igual que la lista en memoria.
    // El orden "mas reciente primero" lo decide ControlGastos.listar(), no el DAO.
    @Query("SELECT * FROM movimientos ORDER BY id")
    suspend fun listar(): List<MovimientoEntity>

    // :id se reemplaza por el parametro id. Es el "?" del PreparedStatement de JDBC.
    @Query("SELECT * FROM movimientos WHERE id = :id")
    suspend fun buscarPorId(id: Int): MovimientoEntity?

    // Busca la fila por la llave primaria y la reemplaza. Devuelve cuantas filas cambio.
    @Update
    suspend fun actualizar(movimiento:  MovimientoEntity): Int

    // Devuelve cuantas filas borro (0 si el id no existia).
    @Query("DELETE FROM movimientos WHERE id = :id")
    suspend fun eliminar(id: Int): Int



}