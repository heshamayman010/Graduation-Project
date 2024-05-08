package com.example.myapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults

class MainActivity6 : AppCompatActivity() {
    private lateinit var arrlist : ArrayList<Person>
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrlist = arrayListOf<Person>()
        Realm.init(this)
        val config=RealmConfiguration.Builder()
            .name("person.realm").build()
        val realm=Realm.getInstance(config)
        setContentView(R.layout.activity_main6)
        val myrecycler_view = findViewById(R.id.myrealm_recycler_view) as RecyclerView
        myrecycler_view.layoutManager= LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        val all:RealmResults<Person> =realm.where(Person::class.java).findAll()
        for(el in all)
        {
            arrlist.add(el)
        }
        myrecycler_view.adapter=Realm_Adapter(arrlist)
    }
}