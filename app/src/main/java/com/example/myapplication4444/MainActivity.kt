package com.example.myapplication4444

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var buttonLogin: ImageView
    private lateinit var buttonRegister: ImageView
    private lateinit var switchTerms: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Инициализация компонентов
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonRegister = findViewById(R.id.buttonRegister)
        switchTerms = findViewById(R.id.switchTerms)

        // Обработчик нажатия на изображение для Войти
        buttonLogin.setOnClickListener {
            if (switchTerms.isChecked) {
                performLogin()
            } else {
                Toast.makeText(this, "Вы должны согласиться с условиями использования", Toast.LENGTH_SHORT).show()
            }
        }

        // Обработчик нажатия на изображение для Регистрации
        buttonRegister.setOnClickListener {
            if (switchTerms.isChecked) {
                navigateToRegistration()
            } else {
                Toast.makeText(this, "Вы должны согласиться с условиями использования", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performLogin() {
        // Переход к экрану входа
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToRegistration() {
        // Переход к экрану регистрации
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }
}
