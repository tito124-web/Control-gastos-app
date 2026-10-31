package umg.edu.gt.progra2

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class ProyectoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Dependencias.iniciar(this)
    }
}