package com.adl.aplikasitim

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        btnRegistrasi.setOnClickListener({
            register()
        })
    }

    private fun register() {
        auth.createUserWithEmailAndPassword(regEmail.text.toString(),regPassword.text.toString())
            .addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    finish()
                }
            }
    }
}
