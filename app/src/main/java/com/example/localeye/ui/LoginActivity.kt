package com.example.localeye.ui

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.localeye.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var emailEt:TextInputEditText
    lateinit var passwordEt: TextInputEditText
    private lateinit var signUp:TextView
    lateinit var loginBtn:Button
    lateinit var auth:FirebaseAuth
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        progressDialog= ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        auth=FirebaseAuth.getInstance()
        signUp=findViewById(R.id.register)
        emailEt=findViewById(R.id.email)
        passwordEt=findViewById(R.id.password)
        loginBtn=findViewById(R.id.loginBtn)
        signUp.setOnClickListener {
            val intent=Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {
            val email=emailEt.text.toString()
            val password=passwordEt.text.toString()
            progressDialog.show()
            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(this,"Please enter credentials",Toast.LENGTH_SHORT).show()
            }
            else if(password.length<6){
                passwordEt.error = "Invalid password "
                Toast.makeText(this,"PPassword must at least 6 characters",Toast.LENGTH_SHORT).show()
            }
            else{
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                    if(it.isSuccessful){
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        progressDialog.dismiss()
                    }
                    else{
                        Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }
}