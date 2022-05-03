package com.adl.aplikasitim

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener({
            login()
    })
        btnRegister.setOnClickListener({
            val intent = Intent(this@LoginActivity,RegisterActivity::class.java)
            startActivity(intent)
        })
}

    private fun login() {
        auth.signInWithEmailAndPassword(txtEmail.text.toString(),txtPassword.text.toString()).addOnCompleteListener { it ->
            if(it.isSuccessful){
                val intent = Intent(this,Home::class.java)
                startActivity(intent)
                finish()
            }

        }.addOnFailureListener { exception -> Toast.makeText(applicationContext,exception.localizedMessage,
            Toast.LENGTH_LONG).show() }
    }

    }
