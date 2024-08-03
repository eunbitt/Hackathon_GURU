package com.example.hackathon_guru

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathon_guru.helpers.DatabaseHelper

class Login : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        val emailEditText: EditText = findViewById(R.id.email)
        val passwordEditText: EditText = findViewById(R.id.password)
        val passwordToggle: ImageView = findViewById(R.id.confirm_password_toggle)
        val loginButton: Button = findViewById(R.id.login_button)
        val registerText: TextView = findViewById(R.id.register_text)

        // Toggle password visibility
        var isPasswordVisible = false
        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD // Show password
                passwordToggle.setImageResource(R.drawable.ic_baseline_visibility_24)
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD // Hide password
                passwordToggle.setImageResource(R.drawable.ic_baseline_visibility_off_24)
            }
            passwordEditText.setSelection(passwordEditText.text.length) // Move cursor to the end
        }

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Perform login action
            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (dbHelper.getUser(email, password)) {
                    Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MapActivity::class.java)
                    intent.putExtra("USER_EMAIL", email)
                    startActivity(intent)
                    finish() // Optional: Close the login activity
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle register text click
        registerText.setOnClickListener {
            // Navigate to SignUp activity
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}
