package com.adl.aplikasitim

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.adl.aplikasitim.databinding.LoginActivityBinding
import com.adl.aplikasitim.views.LibraryFragment
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.rpc.context.AttributeContext
import io.grpc.ManagedChannelProvider.NewChannelBuilderResult.error
import io.grpc.ServerProvider.NewServerBuilderResult.error
import kotlinx.android.synthetic.main.login_activity.*
import org.jetbrains.anko.startActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding:LoginActivityBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        auth = FirebaseAuth.getInstance()
//        checkIfAlreadyLogin()
        onClick()

    }

//    private fun checkIfAlreadyLogin() {
//        val currentUser = auth.currentUser
//        if(currentUser !=null){
//            startActivity<LoginActivity>()
//            finish()
//
//        }
//    }

    private fun onClick() {
        loginBinding.btnRegister.setOnClickListener{
            startActivity<RegisterActivity>()
        }
        loginBinding.btnLogin.setOnClickListener{
            val email = loginBinding.txtEmail.text.toString().trim()
            val pass = loginBinding.txtPassword.text.toString().trim()

            if (checkValidation(email,pass)){
                loginToServer(email,pass)
            }

        }
    }

    private fun checkValidation(email: String, pass: String): Boolean {
        if (email.isEmpty()){
            loginBinding.txtEmail.error = "Please Field Your Email"
            loginBinding.txtEmail.requestFocus()
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginBinding.txtEmail.error = "Please Use Valid Email"
            loginBinding.txtEmail.requestFocus()
        }else if (pass.isEmpty()){
            loginBinding.txtPassword.error = "Please Field Your Password"
            loginBinding.txtPassword.requestFocus()
        }else{
            return true
        }
        return false

    }

    private fun loginToServer(email: String, pass: String) {
        val firebaseUser = auth.currentUser
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                val ref = FirebaseDatabase.getInstance().getReference("users")
                ref.child(firebaseUser!!.uid)
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(it.message)
                    .show()
            }

    }



}
