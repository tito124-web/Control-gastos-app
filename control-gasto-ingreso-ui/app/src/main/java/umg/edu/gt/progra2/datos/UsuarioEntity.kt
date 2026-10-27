package umg.edu.gt.progra2.datos

import androidx.room.Entity
import androidx.room.PrimaryKey
import umg.edu.gt.progra2.modelo.Usuario

@Entity(tableName = "usuario")
data class UsuarioEntity(
    @PrimaryKey val id: Int = 1,      // siempre 1: la app es de un solo usuario
    val nombre: String
) {
    fun aUsuario(): Usuario = Usuario(nombre = nombre)

    companion object {
        fun desde(usuario: Usuario): UsuarioEntity = UsuarioEntity(nombre = usuario.nombre)
    }
}