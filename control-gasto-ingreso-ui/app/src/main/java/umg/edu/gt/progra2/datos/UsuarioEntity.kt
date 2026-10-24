package umg.edu.gt.progra2.datos

import androidx.room.Entity
import umg.edu.gt.progra2.modelo.Usuario


@Entity(tableName = "usuario")
data class UsuarioEntity(
    val nombre: String


) {


    fun aUsuario(): Usuario = Usuario(
        nombre = nombre
    )

    companion object {

    fun desde(usuario: Usuario): UsuarioEntity = UsuarioEntity(

        nombre  = usuario.nombre


    )
}

}
