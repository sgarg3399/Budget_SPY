package com.sakshigarg.dailyexpensetracker

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter.ERROR
import android.bluetooth.BluetoothDevice.ERROR
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private  var email: EditText?= null
    private  var name: EditText?= null
    private  var pass: EditText?= null
    private  var confirmPass: EditText?= null
    private  var registerbtn: Button?= null
    private  var registerQn: TextView?= null
    private lateinit var userDbRef: DatabaseReference

    private  var mAuth: FirebaseAuth?=null

    private  var progressDialog: ProgressDialog?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        email = findViewById(R.id.enter_email)
        pass = findViewById(R.id.enter_pass)
        name = findViewById(R.id.name)
        registerbtn = findViewById(R.id.signup_btn)
        confirmPass= findViewById(R.id.confirm_pass)
      registerQn= findViewById(R.id.signup)
        mAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)

        registerQn!!.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        registerbtn!!.setOnClickListener {
            val nameString= name!!.text.toString()
            val emailString = email!!.text.toString().trim()
            val passwordString = pass!!.text.toString().trim()
            val confirmString = confirmPass!!.text.toString().trim()



            if(TextUtils.isEmpty(emailString)){
                email?.setError("Email is required")
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(nameString)){
                name?.setError("Please enter your name")
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(passwordString)){
                pass?.setError("Password is required")
                return@setOnClickListener
            }

            if (passwordString != confirmString) {
                Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            else{
                progressDialog!!.setMessage("Registration in progress")
                progressDialog!!.setCanceledOnTouchOutside(false)
                progressDialog!!.show()

                mAuth!!.createUserWithEmailAndPassword(emailString,passwordString)
                    .addOnCompleteListener(this, OnCompleteListener {
                            task ->
                        //Toast.makeText(this@RegisterActivity,"createUserWithEmail:onComplete"+task.isSuccessful,Toast.LENGTH_SHORT).show()
                        //progressBar!!.setVisibility(View.VISIBLE)

                        if (!task.isSuccessful){
                            Toast.makeText(this,task.exception.toString(),Toast.LENGTH_LONG).show()
                            progressDialog!!.dismiss()


                        }else{

                            val  currentUserId:String = mAuth!!.currentUser!!.uid
                            userDbRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId)
                            val userInfo= HashMap<String,Any>()
                            userInfo.put("name",nameString)
                            userInfo.put("email",emailString)
                            userDbRef.setValue(userInfo).addOnCompleteListener {
                                if(task.isSuccessful){
                                    startActivity(Intent(this, MainActivity::class.java))
                                    progressDialog!!.dismiss()
                                    finish()
                                }else{
                                    Toast.makeText(this,task.exception.toString(), Toast.LENGTH_LONG).show()
                                    progressDialog!!.dismiss()
                                }
                            }

                        }


                    })
            }
        }
    }
}