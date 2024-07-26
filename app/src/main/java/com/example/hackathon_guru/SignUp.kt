package com.example.hackathon_guru

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val backButton: ImageView = findViewById(R.id.back_button)
        val nameEditText: EditText = findViewById(R.id.name)
        val emailEditText: EditText = findViewById(R.id.email)
        val passwordEditText: EditText = findViewById(R.id.password)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirm_password)
        val passwordToggle: ImageView = findViewById(R.id.password_toggle)
        val confirmPasswordToggle: ImageView = findViewById(R.id.confirm_password_toggle)
        val termsCheckbox: CheckBox = findViewById(R.id.terms_checkbox)
        val signUpButton: Button = findViewById(R.id.login_button)

        backButton.setOnClickListener {
            finish()
        }

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

        var isConfirmPasswordVisible = false
        confirmPasswordToggle.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            if (isConfirmPasswordVisible) {
                confirmPasswordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD // Show password
                confirmPasswordToggle.setImageResource(R.drawable.ic_baseline_visibility_24)
            } else {
                confirmPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD // Hide password
                confirmPasswordToggle.setImageResource(R.drawable.ic_baseline_visibility_off_24)
            }
            confirmPasswordEditText.setSelection(confirmPasswordEditText.text.length) // Move cursor to the end
        }

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val termsAccepted = termsCheckbox.isChecked

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else if (!termsAccepted) {
                Toast.makeText(this, "You must accept the terms and conditions", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                // Add sign up logic here
            }
        }
    }
}
