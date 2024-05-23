package com.example.myapplication4444

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: TextView // Используем TextView вместо Button
    private lateinit var textViewGoBack: TextView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPasswordLogin)
        buttonLogin = findViewById(R.id.textViewLogin) // Текстовая кнопка "Войти"
        textViewGoBack = findViewById(R.id.textViewGoBack) // Текст "Я впервые в MonsterMap"

        findViewById<ImageView>(R.id.back).setOnClickListener {
            onBackPressed() // Возвращает на предыдущий экран
        }

        buttonLogin.setOnClickListener {
            performLogin()
        }

        textViewGoBack.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите электронную почту и пароль", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, CitySelectionActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Ошибка аутентификации: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

