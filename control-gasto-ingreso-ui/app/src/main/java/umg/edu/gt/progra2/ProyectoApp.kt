package umg.edu.gt.progra2

import android.app.Application

class ProyectoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Dependencias.iniciar(this)
    }
}