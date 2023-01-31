package com.example.localeye.ui

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.localeye.R
import com.example.localeye.data.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class RegisterActivity : AppCompatActivity() {
    lateinit var login:TextView
    lateinit var emailEt: TextInputEditText
    lateinit var nameEt:TextInputEditText
    lateinit var passwordEt:TextInputEditText
    lateinit var confirmPassET:TextInputEditText
    lateinit var signUpBtn:Button
    lateinit var userImg:CircleImageView
    lateinit var auth:FirebaseAuth
    lateinit var database:FirebaseDatabase
    lateinit var storage:FirebaseStorage
    lateinit var imageUri:Uri
    lateinit var imageUrl:String
    lateinit var progressDialog:ProgressDialog
    lateinit var token:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        progressDialog= ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()
        storage=FirebaseStorage.getInstance()
        login=findViewById(R.id.login)
        signUpBtn=findViewById(R.id.signup)
        emailEt=findViewById(R.id.email)
        nameEt=findViewById(R.id.name)
        passwordEt=findViewById(R.id.password)
        confirmPassET=findViewById(R.id.confirmPassword)
        userImg=findViewById(R.id.profile_image)


        login.setOnClickListener{
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }

        signUpBtn.setOnClickListener {
            val name=nameEt.text.toString()
            val email=emailEt.text.toString()
            val password=passwordEt.text.toString()
            val confirm=confirmPassET.text.toString()
            val status="Hey! I am using this app."

            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result != null && !TextUtils.isEmpty(task.result)) {
                            token= task.result!!
                        }
                    }
                }
            progressDialog.show()
            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm)){
                Toast.makeText(this,"Please enter credentials", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
            else if(password.length<6 && confirm.length<6){
                passwordEt.error = "Invalid password "
                Toast.makeText(this,"PPassword must at least 6 characters", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
            else if(password!=confirm){
                confirmPassET.error = "Password did not match"
                Toast.makeText(this,"Password did not match",Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
            else{
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                    if(it.isSuccessful){
                        progressDialog.dismiss()
                        //Toast.makeText(this,"Registration successfully",Toast.LENGTH_SHORT).show()
                        val databaseReference=database.reference.child("user").child(auth.uid!!)
                        val storageReference=storage.reference.child("upload").child(auth.uid!!)
                        if(imageUri!=null){
                            storageReference.putFile(imageUri).addOnCompleteListener{ it ->
                                if(it.isSuccessful){
                                    storageReference.downloadUrl.addOnSuccessListener {
                                        imageUrl=it.toString()
                                        val user=
                                            User(auth.uid!!,name,email,password,imageUrl,status,token)
                                        databaseReference.setValue(user).addOnCompleteListener {
                                            if (it.isSuccessful){
                                                Toast.makeText(this,"Registration successfully",Toast.LENGTH_SHORT).show()
                                                progressDialog.dismiss()
                                                startActivity(Intent(this@RegisterActivity,
                                                    HomeActivity::class.java))
                                            }
                                            else{
                                                progressDialog.dismiss()
                                                Toast.makeText(this,"Something went wrong at send data at firebase",Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else{
                            storageReference.putFile(imageUri).addOnCompleteListener{ it ->
                                if(it.isSuccessful){
                                    progressDialog.dismiss()
                                    storageReference.downloadUrl.addOnSuccessListener {
                                        imageUrl="https://firebasestorage.googleapis.com/v0/b/localeye-2f05d.appspot.com/o/upload%2Fwp-1458272855973.jpg?alt=media&token=9cf8fa21-9fe8-4311-b5fa-50c65cb9d09e"
                                        val user=
                                            User(auth.uid!!,name,email,password,imageUrl,status,token)
                                        databaseReference.setValue(user).addOnCompleteListener {
                                            if (it.isSuccessful){
                                                Toast.makeText(this,"Registration successfully",Toast.LENGTH_SHORT).show()
                                                startActivity(Intent(this@RegisterActivity,
                                                    HomeActivity::class.java))
                                            }
                                            else{
                                                progressDialog.dismiss()
                                                Toast.makeText(this,"Something went wrong from no image",Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        progressDialog.dismiss()
                        Toast.makeText(this,"Something went wrong not able to createUser",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        userImg.setOnClickListener {
            selectImageInAlbum()
        }



    }

    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }
    companion object {
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1){
            if(data!=null){
                imageUri= data.data!!
                userImg.setImageURI(imageUri)

            }
        }
    }
}