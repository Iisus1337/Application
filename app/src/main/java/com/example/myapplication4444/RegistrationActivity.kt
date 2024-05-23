package com.example.myapplication4444

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegistrationActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var buttonRegister: ImageView
    private lateinit var textViewAlreadyRegistered: TextView
    private lateinit var backImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        mAuth = FirebaseAuth.getInstance()

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        buttonRegister = findViewById(R.id.buttonRegister)
        textViewAlreadyRegistered = findViewById(R.id.textViewAlreadyRegistered)
        backImageView = findViewById(R.id.back)

        buttonRegister.setOnClickListener {
            registerUser()
        }

        textViewAlreadyRegistered.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        backImageView.setOnClickListener {
            onBackPressed()
        }
    }

    private fun registerUser() {
        val username = editTextUsername.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val confirmPassword = editTextConfirmPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = mAuth.currentUser
                updateUI(user)
            } else {
                Toast.makeText(this, "Ошибка регистрации: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                updateUI(null)
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        user?.let {
            startActivity(Intent(this, CitySelectionActivity::class.java))
            finish()
        }
    }
}
