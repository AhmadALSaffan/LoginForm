package com.example.loginform

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.loginform.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var firebaseAuth:FirebaseAuth
    private lateinit var databaseReference:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isUserLoggedIn()) {
            val intent = Intent(this, MainPage::class.java)
            startActivity(intent)
            finish()
        }

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnLogin.setOnClickListener{
            val email=binding.EdtEmail.text.toString()
            val passowrd=binding.EdtPassword.text.toString()
            if(email.isNotEmpty()&&passowrd.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email,passowrd).addOnCompleteListener {
                    if (it.isSuccessful) {
                        saveLoginState()
                        val intent=Intent(this,MainPage::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this,"Fields cannot be empty!!",Toast.LENGTH_SHORT).show()
            }
        }
        binding.SignUpT.setOnClickListener {
            val intent=Intent(this,SignUp::class.java)
            startActivity(intent)
        }
        binding.ForgotPasswordT.setOnClickListener {
            val builder=AlertDialog.Builder(this)
            val view=layoutInflater.inflate(R.layout.dialog_forgot,null)
            builder.setView(view)
            val dialog = builder.create()
            val UserEmail=view.findViewById<EditText>(R.id.edtEmailForgot)
            view.findViewById<Button>(R.id.btnReset).setOnClickListener{
                compareEmail(UserEmail)
                dialog.dismiss()
            }
            view.findViewById<Button>(R.id.btnCancel).setOnClickListener{
                dialog.dismiss()
            }
            if(dialog.window!=null){
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()
        }
    }
    private fun compareEmail(email:EditText){
        val emailStr=email.text.toString()
        if(emailStr.isEmpty()){
            Toast.makeText(this,"Please enter a Valid Email ! ",Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()){
            Toast.makeText(this,"Please enter a valid Email !",Toast.LENGTH_SHORT).show()
            return
        }
        firebaseAuth.sendPasswordResetEmail(emailStr).addOnCompleteListener {task->
            if(task.isSuccessful) {
                Toast.makeText(this, "Check Your email For Reset Your Password", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun saveLoginState() {
        val sharedPreferences = getSharedPreferences("LoginForm", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }
    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("LoginForm", MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
}