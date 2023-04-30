package com.example.bicatakotline

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.delay
import java.util.Arrays
import java.util.concurrent.TimeUnit

class SplashScreenActivity : AppCompatActivity() {
    companion object{
        private val LOGIN_REQUEST_CODE = 7171
    }
    private lateinit var providers: List<AuthUI.IdpConfig>
    private  lateinit var firebaseAuth: FirebaseAuth
    private  lateinit var listener:FirebaseAuth.AuthStateListener

    private lateinit var database : FirebaseDatabase
    private lateinit var driverInfoRef:DatabaseReference

    override fun onStart(){
        super.onStart()
        delaySplashScreen();
    }
    override fun onStop(){
        if(firebaseAuth != null && listener != null) firebaseAuth.removeAuthStateListener(listener)
        super.onStop()
    }

    private fun delaySplashScreen() {
        Completable.timer(3, TimeUnit.SECONDS,AndroidSchedulers.mainThread())
            .subscribe({
                firebaseAuth.addAuthStateListener(listener);

            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        init()

    }
    private fun init(){

        database=FirebaseDatabase.getInstance()
        driverInfoRef=database.getReference(Common.DRIVER_INFO)


        providers = Arrays.asList(
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        firebaseAuth = FirebaseAuth.getInstance()
        listener  = FirebaseAuth.AuthStateListener { myFirebaseAuth ->
            val user = myFirebaseAuth.currentUser
            if(user !=null)
                checkUserFromFirebase()
            else
                showLoginLayout()
        }
    }

    private fun checkUserFromFirebase() {
        driverInfoRef.child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        Toast.makeText(this@SplashScreenActivity,"User Already Registered",Toast.LENGTH_SHORT).show()
                    }else{
                        showRegisterLayout()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SplashScreenActivity,error.message,Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun showRegisterLayout() {
        val builder=AlertDialog.Builder(this,R.style.DialogTheme)
        val itemView=LayoutInflater.from(this).inflate(R.layout.layout_register,null)

        val edt_first_name=itemView.findViewById<View>(R.id.edt_first_name) as TextInputEditText
        val edt_phone=itemView.findViewById<View>(R.id.edt_phone_number) as TextInputEditText
        val edt_last_name=itemView.findViewById<View>(R.id.edt_last_name) as TextInputEditText

        val btn_register=itemView.findViewById<View>(R.id.btn_register) as Button

        if(FirebaseAuth.getInstance().currentUser!!.phoneNumber!=null&&
                !TextUtils.isDigitsOnly(FirebaseAuth.getInstance().currentUser!!.phoneNumber))
            edt_phone.setText(FirebaseAuth.getInstance().currentUser!!.phoneNumber)

        builder.setView(itemView)
        val dialog=builder.create()
        dialog.show()

        btn_register.setOnClickListener{
            if(TextUtils.isEmpty((edt_first_name.text.toString()))) {
                Toast.makeText(this, "Please Enter First Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(TextUtils.isEmpty((edt_last_name.text.toString()))){
                Toast.makeText(this,"Please Enter Last Name",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                    //continue from video number 3
            }




        }

    }

    private fun showLoginLayout() {
        val authMethodPickerLayout = AuthMethodPickerLayout.Builder(R.layout.layout_sign_in)
            .setPhoneButtonId(R.id.btn_phone_sign_in)
            .setGithubButtonId(R.id.btn_google_sign_in)
            .build();
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(authMethodPickerLayout)
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()
            , LOGIN_REQUEST_CODE)



    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== LOGIN_REQUEST_CODE){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == RESULT_OK)
            {
                val user = FirebaseAuth.getInstance().currentUser
            }
            else
                Toast.makeText(this@SplashScreenActivity,""+response!!.error!!.message,Toast.LENGTH_SHORT).show()

        }
    }
}