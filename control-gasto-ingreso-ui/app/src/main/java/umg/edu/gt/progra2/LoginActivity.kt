package umg.edu.gt.progra2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import umg.edu.gt.progra2.databinding.ActivityLoginBinding
import umg.edu.gt.progra2.modelo.Usuario

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val usuarios = Dependencias.usuarios

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            prepararPantalla(usuarios.existeUsuario())
        }
    }

    private fun prepararPantalla(existe: Boolean) {
        if (existe) {
            binding.tvTitulo.text = "Bienvenido de nuevo"
            binding.tvSubtitulo.text = "Escribe tu nombre para entrar"
            binding.btnEntrar.text = "Entrar"
        } else {
            binding.tvTitulo.text = "Bienvenido"
            binding.tvSubtitulo.text = "¿Cómo te llamas?"
            binding.btnEntrar.text = "Crear cuenta"
        }
        binding.btnEntrar.setOnClickListener { entrar(existe) }
    }

    private fun entrar(existe: Boolean) {
        val nombre = binding.etNombre.text.toString()
        lifecycleScope.launch {
            if (existe) {
                if (usuarios.iniciarSesion(nombre)) irAlBalance()
                else binding.etNombre.error = "Nombre incorrecto"
            } else {
                val error = usuarios.registrar(Usuario(nombre = nombre))
                if (error != null) binding.etNombre.error = error else irAlBalance()
            }
        }
    }

    private fun irAlBalance() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}