package com.developer.marianamotta.keeptask.ui.login

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.developer.marianamotta.keeptask.R
import com.developer.marianamotta.keeptask.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        buttonLogin.setOnClickListener {
            logIn()
        }
        buttonCadastro.setOnClickListener {
            cadastro()
        }

        auth = Firebase.auth
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun logIn() {
        val email = emailTextView.text.toString()
        val password = senhaTextView.text.toString()

        if (email.isEmpty()) {
            showErro(emailTextView, "ERRO:Entre com um email válido..")
            emailTextView.requestFocus()
        }

        if (password.isEmpty()) {
            showErro(senhaTextView, "ERRO:Entre com uma senha válida")
            senhaTextView.requestFocus()
        }

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val mainIntent = Intent(this, MainActivity::class.java)
                        startActivity(mainIntent)
                        finish()
                    }
                }.addOnFailureListener {
                    showSnack(this@LoginActivity,"ERRO:Email ou senha inválida, tente novamente...")
                    Log.d("LOGIN_ACTIVITY", "ERRO:: ${it.message}")
                }
        }
    }

    private fun cadastro() {
        val cadastroIntent = Intent(this, RegistryActivity::class.java)
        startActivity(cadastroIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


    private fun showSnack(activity: Activity?, text:String, duration:Int = Snackbar.LENGTH_SHORT) {
        if (activity != null) {
            Snackbar.make(activity.findViewById(android.R.id.content),text,duration).show()
        }
    }

    private fun showErro(str: EditText, error: String) {
        return str.setError(error)
    }
}