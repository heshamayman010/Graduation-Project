package com.example.myapplication

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Realm_Adapter (val userlist:ArrayList<Person>): RecyclerView.Adapter<Realm_Adapter.ViewHolder>() {
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),OnClickListener,
         PopupMenu.OnMenuItemClickListener {
         val text_name:TextView
         val text_age:TextView
         val text_id:TextView
         val text_job:TextView
         val imageButton:ImageButton
         init {
             text_name=itemView.findViewById(R.id.recycler_name_realm)
             text_age=itemView.findViewById(R.id.recycler_date_realm)
             text_id=itemView.findViewById(R.id.recycler_id_realm)
             text_job=itemView.findViewById(R.id.recycler_job_realm)
             imageButton=itemView.findViewById(R.id.imageButton)
             imageButton.setOnClickListener(this)
         }
         override fun onClick(v: View?) {
             if (v != null) {
                 showPopMenu(v)
             }
         }
         fun showPopMenu(view:View){
             val popmenu =PopupMenu(view.context,view)
             popmenu.inflate(R.menu.pop_menu)
             popmenu.setOnMenuItemClickListener(this)
             popmenu.show()
         }

         override fun onMenuItemClick(item: MenuItem?): Boolean {
             val position=userlist
             if (item != null) {
                 when(item.itemId){
                     R.id.action_popup_delete ->
                         return true
                     else ->
                         return false
                 }
             }
             else{
                 return false
             }
         }

     }
     override fun onCreateViewHolder(
         parent: ViewGroup,
         viewType: Int
     ): ViewHolder {
         val v= LayoutInflater.from(parent.context).inflate(R.layout.realm_recycler_view,parent,false)
         return ViewHolder(v)
     }

     override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         val recycler_data:Person=userlist[position]
         holder.text_name.text=recycler_data.name
         holder.text_age.text=recycler_data.age.toString()
         holder.text_id.text=recycler_data.id.toString()
         holder.text_job.text=recycler_data.job
         //holder.recycler_img.setImageResource(recycler_data.image)
     }

     override fun getItemCount(): Int {
         return userlist.size
     }
 }