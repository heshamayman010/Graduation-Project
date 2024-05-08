package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MenuMainActivity : AppCompatActivity() {
    lateinit var toggle :ActionBarDrawerToggle
    var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_main)
        mAuth = FirebaseAuth.getInstance()
        var drawerLayout:DrawerLayout=findViewById(R.id.drawerLayout)
        val navView:NavigationView=findViewById(R.id.nav_view)
        toggle= ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        var nav_header=navView.getHeaderView(0)
        val headerTextView = nav_header.findViewById<TextView>(R.id.email_address)
        headerTextView.text= mAuth!!.currentUser?.email.toString()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout,PrimaryFragment()).
            commitNow()
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home ->{
                    drawerLayout.closeDrawers()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout,PrimaryFragment()).
                        commitNow()
                    true
                }
                R.id.nav_history ->{
                    drawerLayout.closeDrawers()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout,HistoryFragment()).
                        commitNow()
                    true
                }
                R.id.nav_logout -> {

                    mAuth!!.signOut()
                    var intent= Intent(this,LoginActivity :: class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_MyItem ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout,MyItemFragment()).
                        commitNow()
                true
                }

                R.id.nav_AddItem ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout,ImageFragment()).
                        commitNow()
                    true
                }
                else ->{
                    false
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser
        if (user == null) {
            startActivity(Intent(this@MenuMainActivity, LoginActivity::class.java))
        }
    }
}