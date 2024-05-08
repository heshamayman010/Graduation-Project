package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    var btnLogOut: Button? = null
    var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        var intent= Intent(this,MenuMainActivity :: class.java)
        startActivity(intent)
        /*btnLogOut = findViewById(R.id.btnLogout)
        mAuth = FirebaseAuth.getInstance()

        btnLogOut!!.setOnClickListener(View.OnClickListener { view: View? ->
            mAuth!!.signOut()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        })
        val gotosecond=findViewById(R.id.gotosecond) as Button
        gotosecond.setOnClickListener{
            var intent=Intent(this,Second_Activity :: class.java)
            startActivity(intent)

        }*/
    }
    override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser
        if (user == null) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }

}