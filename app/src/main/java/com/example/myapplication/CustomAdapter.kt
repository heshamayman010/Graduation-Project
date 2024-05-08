package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import io.realm.Realm

class CustomAdapter(val c: Context,
                    val userlist:ArrayList<MyItem> ,
                    val MyItemFragment: MyItemFragment
):RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
      inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView), View.OnClickListener,
        PopupMenu.OnMenuItemClickListener {
        val text_name:TextView
          val text_date: TextView
          val text_price: TextView
          val text_city: TextView
          val text_market: TextView
        val imageButton: ImageButton
        init {
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

        fun showPopMenu(view:View){
            val popmenu =PopupMenu(view.context,view)
            popmenu.inflate(R.menu.pop_menu)
            popmenu.setOnMenuItemClickListener(this)
            popmenu.show()

        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            val position=userlist[adapterPosition]
            realm =Realm.getDefaultInstance()
            if (item != null) {
                when(item.itemId){
                    R.id.action_popup_delete ->{
                        val builder = AlertDialog.Builder(c)
                        builder.setTitle("Alert")
                        builder.setMessage("Are you sure")

                        builder.setPositiveButton("Yes") { dialog, which ->

                            val item = realm.where(MyItem::class.java)
                                .equalTo("id", position.id)
                                .findFirst()

                            var path:String="/المنتجات/"+ (item?.type)

                            FirebaseDatabase.getInstance().reference.child(path).child(position.Item_id.toString()).removeValue()


                            realm.executeTransaction {
                                item!!.deleteFromRealm()
                            }
                            dialog.dismiss()
                            Toast.makeText(c,"User Information is Deleted",Toast.LENGTH_SHORT).show()
                            MyItemFragment.reloadfragment()
                        }

                        builder.setNegativeButton("No") { dialog, which ->
                            dialog.dismiss()
                        }
                        builder.show()
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
        val v=LayoutInflater.from(parent.context).inflate(R.layout.recycler_row,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recycler_data:MyItem=userlist[position]
        holder.text_name.text=recycler_data.name
        holder.text_date.text=recycler_data.date.toString()
        holder.text_price.text=recycler_data.price.toString()
        holder.text_city.text=recycler_data.city.toString()
        holder.text_market.text=recycler_data.market.toString()
        holder.itemView.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                var bundle = Bundle()
                bundle.putString("name",recycler_data.name)
                bundle.putSerializable("data",recycler_data)
                val fragment=ItemInfoFragment()
                fragment.arguments=bundle
                var a=v!!.context as AppCompatActivity
                a.supportFragmentManager.beginTransaction().replace(R.id.frameLayout,fragment)
                    .commitNow()
            }

        })
        //holder.recycler_img.setImageResource(recycler_data.image)
    }
}