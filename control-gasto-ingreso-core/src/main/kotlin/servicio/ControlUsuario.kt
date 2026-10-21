package umg.edu.gt.progra2.servicio

import umg.edu.gt.progra2.datos.UsuarioRepository
import umg.edu.gt.progra2.modelo.Usuario

class ControlUsuario(private val repository: UsuarioRepository) {

    suspend fun validar(usuario: Usuario): String? {
        if (usuario.nombre.isBlank()) return "El nombre no puede ir vacío"


        return null
    }

    suspend fun registrar(usuario: Usuario): String? {
        val error = validar(usuario)
        if (error != null) {
            return error
        }
        repository.aguardar(usuario)

        return null
    }

}