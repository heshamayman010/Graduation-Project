package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity4 : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)
        val name :String? = intent.extras?.getString("Name","None")
        val age :String? = intent.extras?.getString("Age","None")
        val textview_name=findViewById(R.id.textView_name) as TextView
        val textview_age=findViewById(R.id.textView_age) as TextView
        textview_name.text=name
        textview_age.text=age
    }
}