package com.example.loginform

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.loginform.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {
    private lateinit var binding:ActivitySignUpBinding
    private lateinit var firebaseAuth:FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth=FirebaseAuth.getInstance()
        databaseReference=FirebaseDatabase.getInstance().reference

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnSingUp.setOnClickListener{
            val email=binding.EdtsignEmail.text.toString()
            val password=binding.edtSignPassword.text.toString()
            val confirmP=binding.edtSignConfirm.text.toString()
            if(email.isNotEmpty()&&password.isNotEmpty()&&confirmP.isNotEmpty()){
                if(password==confirmP){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task->
                        if(task.isSuccessful){
                            firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                                Toast.makeText(this,"Please Verify Your Email !",Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }?.addOnFailureListener{
                                Toast.makeText(this,"Failed To Verify !",Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this,"Password Is Not Match !",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Fields cannot be empty !! ",Toast.LENGTH_SHORT).show()
            }
        }
        binding.LoginT.setOnClickListener {
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}