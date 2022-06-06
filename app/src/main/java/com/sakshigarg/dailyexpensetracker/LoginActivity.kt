package com.sakshigarg.dailyexpensetracker

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var email:EditText
    private lateinit var pass:EditText
    private lateinit var loginbtn:Button
    private lateinit var loginQn:TextView
    private lateinit var forgotPass:TextView
    private lateinit var mAuth:FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.email)
        pass = findViewById(R.id.pass)
        loginbtn = findViewById(R.id.login_btn)
        loginQn= findViewById(R.id.signup)
        mAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        forgotPass = findViewById(R.id.forgot_pass)

        authStateListener = FirebaseAuth.AuthStateListener {

            try{
                var user:FirebaseUser = mAuth.currentUser!!
                if(user!=null){
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            } catch (e: Exception){

            }

        }

        forgotPass.setOnClickListener {
            startActivity(Intent(this,ForgotPass::class.java))
        }

        loginQn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        loginbtn.setOnClickListener {
            var emailString = email.text.toString()
            var passwordString = pass.text.toString()

            if(TextUtils.isEmpty(emailString))
                email.setError("Email is required")
            if(TextUtils.isEmpty(passwordString))
                pass.setError("Password is required")
            else{
                progressDialog.setMessage("Login in progress")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                mAuth.signInWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(this){
                    if(it.isSuccessful){
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                        progressDialog.dismiss()
                    }else{
                        Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener (authStateListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(authStateListener)
    }
}