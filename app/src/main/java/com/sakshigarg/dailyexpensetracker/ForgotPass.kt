package com.sakshigarg.dailyexpensetracker

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase


class ForgotPass : AppCompatActivity() {
    private lateinit var reset:AppCompatButton
    private lateinit var email:EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)

        reset = findViewById(R.id.reset)
        email = findViewById(R.id.email)
        auth = FirebaseAuth.getInstance();
        progressDialog = ProgressDialog(this)

        reset.setOnClickListener {
            val emailId:String = email.getText().toString().trim();
            if (TextUtils.isEmpty(emailId)) {
                Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            progressDialog.setMessage("In progress")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            auth.sendPasswordResetEmail(emailId)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Please check your mail. Don't forget to check the SPAM folder too.!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss()
                    } else {
                        Toast.makeText(this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss()
                    }
                }
        }
    }
}