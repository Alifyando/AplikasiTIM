package com.adl.aplikasitim

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.adl.aplikasitim.databinding.ActivityRegisterBinding
import com.adl.aplikasitim.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Transaction.success
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity
import kotlin.Result.Companion.success


class RegisterActivity : AppCompatActivity() {

    private lateinit var registerBinding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        auth = FirebaseAuth.getInstance()


        init()
        onClick()
    }

    private fun onClick() {
        registerBinding.btnRegistrasi.setOnClickListener {
            val fullName = registerBinding.etFullName.text.toString().trim()
            val email = registerBinding.regEmail.text.toString().trim()
            val pass = registerBinding.regPassword.text.toString().trim()

            if (checkValidation(fullName, email, pass)){
                registerToServer(fullName, email, pass)
            }
        }
    }

    private fun registerToServer(fullName: String, email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { authResult ->
                if (authResult.isSuccessful){
                    val uid = auth.currentUser?.uid
                    val user = User(fullName = fullName, email = email, uid = uid)

                    userDatabase.child(uid.toString()).setValue(user)
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                AlertDialog.Builder(this)
                                    .setTitle("success")
                                    .setMessage("your_account_has_been_created")
                                    .show()

                                Handler(Looper.getMainLooper()).postDelayed({
                                    startActivity<LoginActivity>()
                                }, 1500)
                            }
                        }
                        .addOnFailureListener {

                            AlertDialog.Builder(this)
                                .setTitle("error")
                                .setMessage(it.message)
                                .show()
                        }
                }
            }
            .addOnFailureListener {

                AlertDialog.Builder(this)
                    .setTitle("error")
                    .setMessage(it.message)
                    .show()
            }
    }
    private fun checkValidation(fullName: String, email: String, pass: String): Boolean {
        if (fullName.isEmpty()){
            registerBinding.etFullName.error = "please_field_your_full_name"
            registerBinding.etFullName.requestFocus()
        }else if (email.isEmpty()){
            registerBinding.regEmail.error = "please_field_your_email"
            registerBinding.regEmail.requestFocus()
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            registerBinding.regEmail.error = "please_field_your_email"
            registerBinding.regEmail.requestFocus()
        }else if (pass.isEmpty()){
            registerBinding.regPassword.error =  "please_field_your_password"
            registerBinding.regPassword.requestFocus()

        }else{
            return true
        }
        return false
    }

    private fun init(){
        //Setup Support Action Bar


        auth = FirebaseAuth.getInstance()
        userDatabase = FirebaseDatabase.getInstance().getReference("users")
    }
}

