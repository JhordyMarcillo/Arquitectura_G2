package ec.edu.monster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import ec.edu.monster.api.RetrofitClient
import ec.edu.monster.model.LoginRequest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var editUsuario: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnTogglePassword: ImageView
    private lateinit var btnLogin: com.google.android.material.button.MaterialButton
    private lateinit var btnSalir: com.google.android.material.button.MaterialButton
    private lateinit var textError: TextView
    private lateinit var progressBar: ProgressBar
    
    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editUsuario = findViewById(R.id.editUsuario)
        editPassword = findViewById(R.id.editPassword)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnSalir = findViewById(R.id.btnSalir)
        textError = findViewById(R.id.textError)
        progressBar = findViewById(R.id.progressBar)

        btnTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        btnLogin.setOnClickListener {
            attemptLogin()
        }

        btnSalir.setOnClickListener {
            finish()
        }
    }

    private fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
        val inputType = if (passwordVisible) {
            android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        editPassword.inputType = inputType
        editPassword.setSelection(editPassword.text.length)
    }

    private fun attemptLogin() {
        val usuario = editUsuario.text.toString().trim()
        val password = editPassword.text.toString().trim()

        if (usuario.isEmpty() || password.isEmpty()) {
            showError("Por favor, complete todos los campos")
            return
        }

        progressBar.visibility = View.VISIBLE
        btnLogin.isEnabled = false
        hideError()

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.login(
                    LoginRequest(usuario = usuario, contrasena = password)
                )

                progressBar.visibility = View.GONE
                btnLogin.isEnabled = true

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.autenticado == true) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        showError(body?.mensaje ?: "Credenciales inválidas")
                    }
                } else if (response.code() == 401) {
                    showError("Credenciales inválidas. Usuario: MONSTER, Contraseña: MONSTER9")
                } else {
                    showError("Error del servidor (${response.code()})")
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                btnLogin.isEnabled = true
                showError("No se pudo conectar al servidor. Verifica que esté encendido.")
            }
        }
    }

    private fun showError(message: String) {
        textError.text = message
        textError.visibility = View.VISIBLE
    }

    private fun hideError() {
        textError.visibility = View.GONE
    }
}

