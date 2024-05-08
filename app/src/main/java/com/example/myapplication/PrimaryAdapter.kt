package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import java.util.UUID

class PrimaryAdapter(listener:SelectListener ,val categorylist : ArrayList<String>):RecyclerView.Adapter<PrimaryAdapter.ViewHolder>(){


    private var listener: SelectListener

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.primary_recycler_view,parent,false)
        return ViewHolder(v)
    }

    init {
        this.listener=listener
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data :String =categorylist[position]
        holder.text_name.text=data
        holder.cardview.setOnClickListener {
            listener.onItemClicked(categorylist.get(position))
        }
        }

    override fun getItemCount(): Int {
        return categorylist.size
    }





    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var text_name = itemView.findViewById(R.id.recycler_name) as TextView
        var cardview=itemView.findViewById(R.id.primary_card) as CardView

    }
}