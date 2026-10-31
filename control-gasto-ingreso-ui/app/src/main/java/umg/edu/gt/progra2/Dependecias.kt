package umg.edu.gt.progra2

import android.content.Context
import androidx.room.Room
import umg.edu.gt.progra2.datos.AppDatabase
import umg.edu.gt.progra2.datos.MovimientoRepositoryRoom
import umg.edu.gt.progra2.datos.UsuarioRepositoryRoom
import umg.edu.gt.progra2.servicio.ControlMovimiento
import umg.edu.gt.progra2.servicio.ControlUsuario

object Dependencias {

    lateinit var control: ControlMovimiento
        private set

    lateinit var usuarios: ControlUsuario
        private set

    fun iniciar(context: Context) {
        val baseDeDatos = Room.databaseBuilder(context, AppDatabase::class.java, "gastos.db")
            .build()

        control = ControlMovimiento(MovimientoRepositoryRoom(baseDeDatos.movimientoDao()))
        usuarios = ControlUsuario(UsuarioRepositoryRoom(baseDeDatos.usuarioDao()))
    }
}