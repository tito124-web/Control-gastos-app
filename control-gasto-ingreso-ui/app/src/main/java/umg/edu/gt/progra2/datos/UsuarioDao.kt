package umg.edu.gt.progra2.datos

import androidx.room.Insert
import androidx.room.Query
import umg.edu.gt.progra2.modelo.Usuario

interface UsuarioDao {

@Insert
suspend fun insertar(usuario: Usuario): Long

@Query("SELECT * FROM usuario ")
suspend fun obtenerusuario(): UsuarioEntity?

}