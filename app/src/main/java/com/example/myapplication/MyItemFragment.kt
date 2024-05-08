package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import io.realm.Realm
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyItemFragment : Fragment() ,MenuProvider {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var user=FirebaseAuth.getInstance().currentUser!!.email
    private lateinit var items:ArrayList<MyItem>
    private lateinit var myrecycler_view: RecyclerView
    private lateinit var arrlist: ArrayList<MyItem>
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view:View=inflater.inflate(R.layout.fragment_my_item, container, false)

        activity?.addMenuProvider(this,viewLifecycleOwner, Lifecycle.State.RESUMED)
        toolbar = view.findViewById(R.id.toolbar_myitem)
        var activity: AppCompatActivity = getActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setTitle("MyItem")
        myrecycler_view = view.findViewById(R.id.myitemfragment_recycler)
        myrecycler_view.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        arrlist = arrayListOf<MyItem>()
        this.context?.let { getUserData(it) }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyItemFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun onetime(){
        var realm: Realm=Realm.getDefaultInstance()
        var check_items=ArrayList<MyItem>()
        val items_result =realm.where(MyItem::class.java).equalTo("Email_Created",user).findAll()
        check_items.addAll(realm.copyFromRealm(items_result))
        if(check_items.size==0){
            val query= FirebaseDatabase.getInstance().getReference("/المنتجات /كورن فليكس").orderByChild("email").equalTo(user)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(userSnapshot in snapshot.children){
                            val user_: RecyclerView_Data? =userSnapshot.getValue(RecyclerView_Data::class.java)
                            realm.executeTransaction {
                                    r: Realm ->
                                var item=r.createObject(MyItem::class.java, UUID.randomUUID().toString())
                                item.name=user_!!.name
                                item.date= user_.date
                                item.market=user_.market
                                item.price=user_.price
                                item.city=user_.city
                                item.Item_id=user_.id
                                item.Email_Created=user_.email
                                realm.insertOrUpdate(item)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    private fun getUserData(c: Context) {
        Realm.init(c)
        var realm: Realm = Realm.getDefaultInstance()
        items=ArrayList<MyItem>()
        items.clear()
        val items_result =realm.where(MyItem::class.java).equalTo("Email_Created",user).findAll()
        items.addAll(realm.copyFromRealm(items_result))
        myrecycler_view.adapter = CustomAdapter(c, items, this)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_item, menu)
        var searchview: SearchView = menu.findItem(R.id.searchName)?.actionView as SearchView
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
    }

    private fun searchByName(s: String) {
        var search_list=ArrayList<MyItem>()
        search_list.clear()
        for(el in items)
        {
            var flag=true
            var w =s.split(" ")
            for (word in w)
            {
                if(!wordExistsInSentence(word,el.name.toString()))
                {
                    flag=false
                    break
                }
            }
            if(flag)
            {
                search_list.add(el)
            }
        }
        myrecycler_view.adapter=CustomAdapter(myrecycler_view.context,search_list,this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

    public fun reloadfragment(){
        this.context?.let { getUserData(it) }
    }
    fun wordExistsInSentence(word: String, sentence: String): Boolean {
        // Split the sentence into words
        val words = sentence.split(" ")

        // Check if the target word exists as a part of any word in the sentence
        for (w in words) {
            if (w.contains(word)) {
                return true
            }
        }

        return false
    }
}