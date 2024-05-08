package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm

class Realm_History(
    val c: Context,
    val userlist: ArrayList<FollowItem>,
    val historyFragment: HistoryFragment
): RecyclerView.Adapter<Realm_History.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener,
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
        lateinit var realm: Realm
        override fun onClick(v: View?) {
            if (v != null) {
                showPopMenu(v)
            }
        }
        private lateinit var popmenu:PopupMenu
        fun showPopMenu(view: View){
            popmenu = PopupMenu(view.context,view)
            popmenu.inflate(R.menu.history_pop_menu)
            popmenu.setOnMenuItemClickListener(this)
            popmenu.show()
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            val position=userlist[adapterPosition]
            if (item != null) {
                when(item.itemId){
                    R.id.delete_item_history ->{
                            realm =Realm.getDefaultInstance()
                            val item = realm.where(FollowItem::class.java)
                                .equalTo("id", position.id)
                                .findFirst()

                            realm.executeTransaction {
                                item!!.deleteFromRealm()
                            }
                            Toast.makeText(c,"Product Information is Deleted", Toast.LENGTH_SHORT).show()
                        historyFragment.reloadfragment()
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
        val recycler_data:FollowItem=userlist[position]
        holder.text_name.text=recycler_data.name
        holder.text_date.text=recycler_data.date.toString()
        holder.text_price.text=recycler_data.price.toString()
        holder.text_city.text=recycler_data.city.toString()
        holder.text_market.text=recycler_data.market.toString()
        //holder.recycler_img.setImageResource(recycler_data.image)
    }


}