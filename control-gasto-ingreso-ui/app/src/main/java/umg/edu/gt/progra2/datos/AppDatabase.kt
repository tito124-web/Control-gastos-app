package umg.edu.gt.progra2.datos

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MovimientoEntity::class, UsuarioEntity::class],
    version = 1,
    exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movimientoDao(): MovimientoDao
    abstract fun usuarioDao(): UsuarioDao
}

