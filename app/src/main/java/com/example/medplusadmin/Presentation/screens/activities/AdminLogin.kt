package com.example.medplusadmin.Presentation.screens.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import app.rive.runtime.kotlin.core.Rive
import com.example.medplusadmin.R
import com.example.medplusadmin.databinding.ActivityAdminLoginBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminLogin : AppCompatActivity() {
    @Inject
    lateinit var auth: FirebaseAuth
    val binding by lazy { ActivityAdminLoginBinding.inflate(layoutInflater) }
    private val stateMachineName = "Login Machine"
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
//        AndroidBug5497Workaround(this).addListener()
        supportActionBar?.hide()
        //Rive Initialization
        Rive.init(this)

        binding.btnLogin.setOnClickListener {

            binding.etPassword.clearFocus()

            Handler(mainLooper).postDelayed({
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                if (email.isEmpty()||password.isEmpty()){
                    Toast.makeText(this, "Enter Required Fields", Toast.LENGTH_SHORT).show()
                    binding.loginCharacter.controller.fireState(stateMachineName,"trigFail")
                    return@postDelayed
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.etEmail.error = "Invalid Email"
                    binding.loginCharacter.controller.fireState(stateMachineName,"trigFail")
                    binding.etEmail.requestFocus()
                    return@postDelayed
                }
                if (password.length<6){
                    binding.etPassword.error = "Password must be at least 6 characters"
                    binding.loginCharacter.controller.fireState(stateMachineName,"trigFail")
                    return@postDelayed
                }
                else{
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            binding.loginCharacter.controller.fireState(stateMachineName,"trigSuccess")
//                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
//                        binding.progessBar.visibility = View.GONE
                            binding.btnLogin.visibility = View.VISIBLE

                            Handler(mainLooper).postDelayed({
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            },3000)
                        }
                        else {
                            Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
//                            binding.progessBar.visibility = View.GONE
                            binding.loginCharacter.controller.fireState(stateMachineName,"trigFail")
                            binding.btnLogin.visibility = View.VISIBLE
                        }
                    }
                }
            },1000)

        }

        binding.etEmail.setOnFocusChangeListener{
                _, hasFocus ->
            if (hasFocus){
                binding.loginCharacter.controller.setBooleanState(stateMachineName,"isChecking",true)
            }else{
                binding.loginCharacter.controller.setBooleanState(stateMachineName,"isChecking",false)
            }
        }
        binding.etPassword.setOnFocusChangeListener{
                _, hasFocus ->
            if (hasFocus){
                binding.loginCharacter.controller.setBooleanState(stateMachineName,"isHandsUp",true)
            }else{
                binding.loginCharacter.controller.setBooleanState(stateMachineName,"isHandsUp",false)
            }
        }

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) { }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) { }

            override fun afterTextChanged(p0: Editable?) {
                try {
                    binding.loginCharacter.controller.setNumberState(stateMachineName,"numLook",p0!!.length.toFloat())
                }catch (e: Exception){
                    Log.e("TAG", "afterTextChanged: $e", )
                    binding.loginCharacter.controller.setNumberState(stateMachineName,"numLook",0f)
                }
            }
        })

        binding.etPassword.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableRight = 2 // index for drawableEnd
                val drawable = binding.etPassword.compoundDrawables[drawableRight]
                if (drawable != null) {
                    val bounds = drawable.bounds
                    val x = event.rawX.toInt()
                    val right = binding.etPassword.right
                    if (x >= right - bounds.width()) {

                        // Toggle password visibility manually
                        val isPasswordVisible = binding.etPassword.transformationMethod == null

                        if (isPasswordVisible) {
                            // Hide password
                            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                            binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)

                            //Show hands
                            binding.loginCharacter.controller.setBooleanState(stateMachineName, "isHandsUp", true)
                            binding.loginCharacter.controller.setBooleanState(stateMachineName,"isChecking",false)

                        } else {
                            //Show password
                            binding.etPassword.transformationMethod = null
                            binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)

                            //Hide hands
                            binding.loginCharacter.controller.setBooleanState(stateMachineName,"isChecking",true)
                            binding.loginCharacter.controller.setBooleanState(stateMachineName, "isHandsUp", false)
                        }
                        binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }
}