package umg.edu.gt.progra2.datos

import umg.edu.gt.progra2.modelo.Usuario

interface UsuarioRepository {

    suspend fun obtener(): Usuario?

    suspend fun guardar(usuario: Usuario)
}