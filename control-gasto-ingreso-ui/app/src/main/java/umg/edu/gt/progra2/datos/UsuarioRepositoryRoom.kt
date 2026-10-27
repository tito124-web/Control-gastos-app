package umg.edu.gt.progra2.datos

import umg.edu.gt.progra2.modelo.Usuario

class UsuarioRepositoryRoom(private val dao: UsuarioDao) : UsuarioRepository {

    override suspend fun obtener(): Usuario? = dao.obtener()?.aUsuario()

    override suspend fun aguardar(usuario: Usuario) = dao.guardar(UsuarioEntity.desde(usuario))
}