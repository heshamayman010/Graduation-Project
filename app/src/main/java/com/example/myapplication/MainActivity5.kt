package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity5 : AppCompatActivity() {
    private lateinit var dbref : DatabaseReference
    private lateinit var myrecycler_view : RecyclerView
    private lateinit var arrlist : ArrayList<RecyclerView_Data>
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main5)
        myrecycler_view = findViewById(R.id.myrealm_recycler_view) as RecyclerView
        myrecycler_view.layoutManager=LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        val addsBtns=findViewById(R.id.addingBtn) as?FloatingActionButton
        addsBtns?.setOnClickListener{addInfo()}
        arrlist = arrayListOf<RecyclerView_Data>()
        getUserData(this)
        /*val arr_list=ArrayList<RecyclerView_Data>()
        arr_list.add(RecyclerView_Data("Mohamed Magdy",30,R.drawable.avatar1))
        arr_list.add(RecyclerView_Data("Samir",88,R.drawable.avatar1))
        arr_list.add(RecyclerView_Data("Mohamed Magdy",30,R.drawable.avatar1))
        arr_list.add(RecyclerView_Data("Samir",88,R.drawable.avatar1))
        arr_list.add(RecyclerView_Data("Mohamed Magdy",30,R.drawable.avatar1))
        arr_list.add(RecyclerView_Data("Samir",88,R.drawable.avatar1))
        arr_list.add(RecyclerView_Data("Mohamed Magdy",30,R.drawable.avatar1))
        arr_list.add(RecyclerView_Data("Samir",88,R.drawable.avatar1))
        arr_list.add(RecyclerView_Data("Mohamed Magdy",30,R.drawable.avatar1))
        arr_list.add(RecyclerView_Data("Samir",88,R.drawable.avatar1))
        arr_list.add(RecyclerView_Data("Mohamed Magdy",30,R.drawable.avatar1))
        arr_list.add(RecyclerView_Data("Samir",88,R.drawable.avatar1))
        arr_list.add(RecyclerView_Data("Mohamed Magdy",30,R.drawable.avatar1))
        arr_list.add(RecyclerView_Data("Samir",88,R.drawable.avatar1))
        arr_list.add(RecyclerView_Data("Mohamed Magdy",30,R.drawable.avatar1))
        arr_list.add(RecyclerView_Data("Samir",88,R.drawable.avatar1))
        val adapter=CustomAdapter(arr_list)
        myrecycler_view.adapter=adapter*/
    }
    private fun addInfo(){
        val inflater=LayoutInflater.from(this)
        val v=inflater.inflate(R.layout.add_item,null)
        val text_name=v.findViewById(R.id.userName) as? EditText
        val text_age=v.findViewById(R.id.userNo) as? EditText
        val addDialog=AlertDialog.Builder(this)
        addDialog.setView(v)

        addDialog.setPositiveButton("OK"){
            dialog,_->
            val name:String= text_name?.text.toString()
            val age: Int = text_age?.text.toString().toInt()
            dbref=FirebaseDatabase.getInstance().reference.child("Users")
            var usermap= HashMap<String, Any>()
            var id: String=dbref.push().key.toString()
            usermap["name"] = name
            usermap["age"] = age
            usermap["id"]=id
            usermap["email"]=mAuth!!.currentUser?.email.toString()
            dbref.child("$id").setValue(usermap)
            Toast.makeText(this,"User $name",Toast.LENGTH_LONG).show()
            dialog.dismiss()
            Toast.makeText(this,"UserAdded Sucessfully",Toast.LENGTH_SHORT).show()
        }
        addDialog.setNegativeButton("Cancel"){
            dialog,_->
            dialog.dismiss()
            Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show()
        }
        addDialog.create()
        addDialog.show()

    }

    private fun getUserData(c:Context){
        dbref=FirebaseDatabase.getInstance().getReference("Users")
        dbref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    arrlist.clear()
                    for(userSnapshot in snapshot.children){
                        val user: RecyclerView_Data? =userSnapshot.getValue(RecyclerView_Data::class.java)
                        arrlist.add(user!!)
                    }
                    //myrecycler_view.adapter=CustomAdapter(c,arrlist)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.searchmenu,menu)
        var item: MenuItem? = menu?.findItem(R.id.search)
        var searchview:SearchView= item?.actionView as SearchView
        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchByName(query)
                };
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchByName(newText)
                };
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
    private fun searchByName(s:String){
        // adding a value listener to database reference to perform search
        dbref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // Checking if the value exists
                if (snapshot.exists()){
                    arrlist.clear()
                    // looping through the values
                    for (i in snapshot.children){
                        val student = i.getValue(RecyclerView_Data::class.java)
                        // checking if the name searched is available and adding it to the array list
                        if (student!!.name.toString().lowercase().startsWith(s.lowercase())){
                            arrlist.add(student)
                        }
                    }
                    //setting data to RecyclerView
                    //myrecycler_view.adapter=CustomAdapter(myrecycler_view.context,arrlist)
                } else{
                    Toast.makeText(applicationContext, "Data does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}