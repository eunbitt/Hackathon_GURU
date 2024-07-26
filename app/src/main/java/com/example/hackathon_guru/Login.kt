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

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText: EditText = findViewById(R.id.email)
        val passwordEditText: EditText = findViewById(R.id.password)
        val passwordToggle: ImageView = findViewById(R.id.confirm_password_toggle)
        val loginButton: Button = findViewById(R.id.login_button)
        val forgotPasswordText: TextView = findViewById(R.id.forgot_password)
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
                Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show()
                // Add login logic here
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle forgot password click
        forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
            // Add forgot password logic here
        }

        // Handle register text click
        registerText.setOnClickListener {
            // Navigate to SignUp activity
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}
