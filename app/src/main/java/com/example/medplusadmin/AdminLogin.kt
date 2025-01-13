package com.example.medplusadmin

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.medplusadmin.databinding.ActivityAdminLoginBinding
import com.google.firebase.auth.FirebaseAuth

class AdminLogin : AppCompatActivity() {
    val binding by lazy { ActivityAdminLoginBinding.inflate(layoutInflater) }
    val auth by lazy { FirebaseAuth.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.Login.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()
            if (email.isEmpty()||password.isEmpty()){
                Toast.makeText(this, "Enter Required Fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.emailEt.error = "Invalid Email"
                binding.emailEt.requestFocus()
                return@setOnClickListener
            }
            if (password.length<6){
                binding.passwordEt.error = "Password must be at least 6 characters"
                binding.passwordEt.requestFocus()
                return@setOnClickListener
            }
            else{
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        binding.progessBar.visibility = View.GONE
                        binding.Login.visibility = View.VISIBLE
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    else {
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                        binding.progessBar.visibility = View.GONE
                        binding.Login.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}