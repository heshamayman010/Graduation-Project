package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import io.realm.Realm
import java.util.UUID

class NewAdapter(val c: Context,val userlist:ArrayList<RecyclerView_Data>): RecyclerView.Adapter<NewAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener,
        PopupMenu.OnMenuItemClickListener {
        val text_name: TextView
        val text_date: TextView
        val text_price: TextView
        val text_city: TextView
        val text_market: TextView
        val imageButton: ImageButton
        init {
            Realm.init(c)
            text_name=itemView.findViewById(R.id.recycler_name_realm)
            text_date=itemView.findViewById(R.id.recycler_date_realm)
            text_price=itemView.findViewById(R.id.recycler_price_realm)
            text_city=itemView.findViewById(R.id.recycler_city_realm)
            text_market=itemView.findViewById(R.id.recycler_market_realm)
            imageButton=itemView.findViewById(R.id.imageButton4)
            imageButton.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            if (v != null) {
                showPopMenu(v)
            }
        }
        val user = FirebaseAuth.getInstance().currentUser
        fun showPopMenu(view: View){
            val popmenu = PopupMenu(view.context,view)
            popmenu.inflate(R.menu.pop_menu_home)
            popmenu.setOnMenuItemClickListener(this)
            popmenu.show()
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            val position=userlist[adapterPosition]
            var realm: Realm=Realm.getDefaultInstance()
            var items=ArrayList<FollowItem>()
            val items_result = realm.where(FollowItem::class.java).findAll()
            items.addAll(realm.copyFromRealm(items_result))

            if (item != null) {
                when(item.itemId){
                    R.id.follow_home -> {
                        for(el in items){
                            if((el.Item_id.equals(position.id,ignoreCase = true)) &&(el.Added_Email.equals(user!!.email,ignoreCase = true)) ){
                                Toast.makeText(c,"Product Information is already in History", Toast.LENGTH_SHORT).show()
                                return false
                            }
                        }

                        val v = LayoutInflater.from(c).inflate(R.layout.add_item,null)
                                realm.executeTransaction {
                                        r: Realm ->
                                    var item=r.createObject(FollowItem::class.java,UUID.randomUUID().toString())
                                    item.name=position.name
                                    item.date=position.date
                                    item.Item_id=position.id
                                    item.city=position.city
                                    item.market=position.market
                                    item.price=position.price.toString()
                                    item.Email_Created=position.email
                                    item.Added_Email=user!!.email
                                    realm.insertOrUpdate(item)
                                }
                                Toast.makeText(c,"Product  Information is in History now", Toast.LENGTH_SHORT).show()

                        return true
                    }

                    else ->
                        return false
                }
            }
            else{
                return false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.recycler_row,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recycler_data:RecyclerView_Data=userlist[position]
        holder.text_name.text=recycler_data.name
        holder.text_date.text=recycler_data.date.toString()
        holder.text_price.text=recycler_data.price.toString()
        holder.text_city.text=recycler_data.city.toString()
        holder.text_market.text=recycler_data.market.toString()
        //holder.recycler_img.setImageResource(recycler_data.image)
    }
}