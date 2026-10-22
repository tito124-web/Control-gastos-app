package umg.edu.gt.progra2.servicio

import umg.edu.gt.progra2.datos.UsuarioRepository
import umg.edu.gt.progra2.modelo.Usuario

class ControlUsuario(private val repositorio: UsuarioRepository) {

    fun validar(usuario: Usuario): String? {
        if (usuario.nombre.isBlank()) return "El nombre no puede ir vacío"
        if (usuario.nombre.trim().length > 30) return "El nombre no puede pasar de 30 caracteres"
        return null
    }

    suspend fun existeUsuario(): Boolean = repositorio.obtener() != null

    suspend fun registrar(usuario: Usuario): String? {
        val error = validar(usuario)
        if (error != null) return error
        if (existeUsuario()) return "Ya existe un usuario"
        repositorio.aguardar(usuario.copy(nombre = usuario.nombre.trim()))
        return null
    }

    suspend fun iniciarSesion(nombre: String): Boolean {
        val usuario = repositorio.obtener() ?: return false
        return usuario.nombre.equals(nombre.trim(), ignoreCase = true)
    }

    suspend fun nombre(): String? = repositorio.obtener()?.nombre
}