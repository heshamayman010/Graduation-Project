package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.*
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
class Second_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val enter_edit_text=findViewById(R.id.input_name) as EditText
        val show_button=findViewById(R.id.show_name_button) as Button
        val tv_name=findViewById(R.id.tv_name) as TextView
        var database:FirebaseDatabase =FirebaseDatabase.getInstance()

        val textview4=findViewById(R.id.textView4) as TextView
        textview4.text=getPackageName()

        var name=enter_edit_text.text

        /*Realm.init(this)
        val config:RealmConfiguration=RealmConfiguration.Builder()
            .name("person.realm").build()
        val realm:Realm =Realm.getInstance(config)
        realm.beginTransaction()

        val person: Person =realm.createObject(Person::class.java,1)
        person.name="samir"
        person.age=33
        person.job="engineer"
        realm.commitTransaction()
        val all:RealmResults<Person> =realm.where(Person::class.java).findAll()*/


        show_button.setOnClickListener(){

            tv_name.text="Kosom "+name
            var mRef:DatabaseReference=database.getReference()
            var savingname:String=tv_name.text.toString()
            mRef.child("name").setValue(savingname)
        }
        val gotoactivity5_btn=findViewById(R.id.recycler_btn) as Button
        gotoactivity5_btn.setOnClickListener{
            var intent=Intent(this,MainActivity5::class.java)
            startActivity(intent)
        }
        val gotoactivity_btn=findViewById(R.id.Go_To_Second_Activity_btn) as Button
        gotoactivity_btn.setOnClickListener{
            var intent=Intent(this,MainActivity2 :: class.java)
            intent.putExtra("Name",tv_name.text)
            startActivity(intent)

        }
        /*val gotomyitem=findViewById(R.id.myitembtn) as Button
        gotomyitem.setOnClickListener{
            var intent=Intent(this,MyItemActivity :: class.java)
            startActivity(intent)

        }*/
        val go_torealm_activity=findViewById(R.id.go_torealm_btn) as Button
        go_torealm_activity.setOnClickListener{
            var intent=Intent(this,MainActivity6 :: class.java)
            startActivity(intent)

        }
    }
    fun checked(view: View) {
        view as CheckBox
        val check_btn1=findViewById(R.id.checkb1) as CheckBox
        val check_btn2=findViewById(R.id.checkb2) as CheckBox
        var i=view.isChecked
        val tv_name=findViewById(R.id.tv_name) as TextView
        when(view.id){
            R.id.checkb1 ->if(i){ tv_name.text=view.text; check_btn2.isChecked=false } else tv_name.text=""
            R.id.checkb2 ->if(i) {tv_name.text=view.text ; check_btn1.isChecked=false  } else tv_name.text=""
        }
    }
    inner class ReadData :ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            var name =snapshot?.getValue()
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }
}