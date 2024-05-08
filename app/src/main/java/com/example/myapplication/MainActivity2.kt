package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView

class MainActivity2 : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val show_name=findViewById(R.id.show_name_edittext) as TextView
        val name :String? = intent.extras?.getString("Name","None")
        val intent2=Intent(this,MainActivity3 :: class.java)
        startActivity(intent2)
        show_name.text=name
    }
}