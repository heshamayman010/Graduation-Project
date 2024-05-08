package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        val myname_arr= arrayOf("Ali","samy","3B3OUL")
        val myage_arr= arrayOf(30,22,40)
        val myimage_arr= arrayOf(R.drawable.avatar1,R.drawable.avatar1,R.drawable.avatar1)
        val myadapter=ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,myname_arr)
        val mylist=findViewById(R.id.simple_list_view1) as ListView
        mylist.adapter=MyAdapter(this,myname_arr,myage_arr,myimage_arr)
        mylist.setOnItemClickListener { _, _, position, _ ->
            var myintent=Intent(this,MainActivity4 ::class.java)
            myintent.putExtra("Name",myname_arr[position])
            myintent.putExtra("Age",myage_arr[position].toString())
            startActivity(myintent)
        }
    }
}
private class MyAdapter(context:Context,myname_arr:Array<String>,myage_arr:Array<Int>,myimage_arr:Array<Int>):BaseAdapter(){
    val context:Context
    val myname_arr:Array<String>
    val myage_arr:Array<Int>
    val myimage_arr:Array<Int>
    init {
        this.context=context
        this.myname_arr=myname_arr
        this.myage_arr=myage_arr
        this.myimage_arr=myimage_arr
    }
    override fun getCount(): Int {
        return  myname_arr.size
    }

    override fun getItem(position: Int): Any {
        return  ""
    }

    override fun getItemId(position: Int): Long {
       return  position.toLong()
    }

    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var mylayout_inflat=LayoutInflater.from(this.context).inflate(R.layout.layout_list_row,parent,false)
        val myimage=mylayout_inflat.findViewById(R.id.my_image) as ImageView
        val myname=mylayout_inflat.findViewById(R.id.my_name) as TextView
        val myage=mylayout_inflat.findViewById(R.id.my_age) as TextView
        myname.text=this.myname_arr[position]
        myage.text=this.myage_arr[position].toString()
        myimage.setImageResource(this.myimage_arr[position])

        return mylayout_inflat
    }

}