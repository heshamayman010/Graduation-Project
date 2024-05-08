package com.example.myapplication


import android.Manifest
import com.example.myapplication.MyItem
import com.example.myapplication.NewAdapter
import com.example.myapplication.RecyclerView_Data



import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() ,MenuProvider{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var dbref: DatabaseReference
    private lateinit var myrecycler_view: RecyclerView
    private lateinit var arrlist: ArrayList<RecyclerView_Data>
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private  var city: String =""


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

        activity?.addMenuProvider(this,viewLifecycleOwner,Lifecycle.State.RESUMED)
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)



        toolbar = view.findViewById(R.id.toolbar)
        var activity: AppCompatActivity = getActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setTitle("Main")
        myrecycler_view = view.findViewById(R.id.my_fragment_recycler)
        myrecycler_view.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        arrlist = arrayListOf<RecyclerView_Data>()
        /*val addsBtns=view.findViewById(R.id.addingBtn_Home) as? FloatingActionButton
        addsBtns?.setOnClickListener{addInfo()}*/
        this.context?.let { getUserData(it) }
        Toast.makeText(requireContext(),param2,Toast.LENGTH_SHORT).show()

        return view.rootView
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String,param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_item, menu)
        var searchview:SearchView= menu.findItem(R.id.searchName)?.actionView as SearchView
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

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId==R.id.searchName){
           /* searchView = menuItem.actionProvider as SearchView
            searchView.setIconifiedByDefault(true)
            val SearchManger: SearchManager =
                activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView.setSearchableInfo(SearchManger.getSearchableInfo(activity?.componentName))
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
            })*/
            return true
        }

        return true
    }
    private fun searchByName(s: String) {
        // adding a value listener to database reference to perform search
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Checking if the value exists
                if (snapshot.exists()) {
                    arrlist.clear()
                    // looping through the values
                    for (i in snapshot.children) {
                        val student = i.getValue(RecyclerView_Data::class.java)
                        // checking if the name searched is available and adding it to the array list

                        if (student != null) {
                            var flag=true
                            if (student.city?.lowercase()  == param2?.lowercase()) {
                                var w =s.split(" ")
                                for (word in w)
                                {
                                    if(!wordExistsInSentence(word,student.name.toString()))
                                    {
                                        flag=false
                                        break
                                    }
                                }
                                if(flag)
                                {
                                    arrlist.add(student)
                                }

                            }
                        }

                    }
                    //setting data to RecyclerView
                    myrecycler_view.adapter = NewAdapter(myrecycler_view.context, arrlist)
                } else {
                    Toast.makeText(context, "Data does not exist", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    private fun addInfo(){
        val inflater=LayoutInflater.from(this.context)
        val v=inflater.inflate(R.layout.add_item,null)
        val text_name=v.findViewById(R.id.userName) as? EditText
        val text_age=v.findViewById(R.id.userNo) as? EditText
        val addDialog= this.context?.let { AlertDialog.Builder(it) }
        addDialog?.setView(v)

        addDialog?.setPositiveButton("OK"){
                dialog,_->
            val name:String= text_name?.text.toString()
            val age: Int = text_age?.text.toString().toInt()
            dbref=FirebaseDatabase.getInstance().reference.child("Users")
            var usermap= HashMap<String, Any>()
            var id: String=dbref.push().key.toString()
            usermap["name"] = name
            usermap["age"] = age
            usermap["id"]=id
            usermap["email"]=FirebaseAuth.getInstance().currentUser!!.email.toString()
            dbref.child("$id").setValue(usermap)
            Toast.makeText(this.context,"User $name",Toast.LENGTH_LONG).show()
            dialog.dismiss()
            Toast.makeText(this.context,"UserAdded Sucessfully",Toast.LENGTH_SHORT).show()
        }
        addDialog?.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(this.context,"Cancel",Toast.LENGTH_SHORT).show()
        }
        addDialog?.create()
        addDialog?.show()

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

    private fun getUserData(c: Context) {

        var path:String="/المنتجات/"+param1
        dbref = FirebaseDatabase.getInstance().getReference(path)
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    arrlist.clear()
                    for (userSnapshot in snapshot.children) {
                        val item: RecyclerView_Data? =
                            userSnapshot.getValue(RecyclerView_Data::class.java)
                        if (item != null) {
                            if (item.city?.lowercase().equals(param2,ignoreCase = true))
                            {
                                arrlist.add(item)
                            }
                        }
                    }
                    myrecycler_view.adapter = NewAdapter(c, arrlist)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}
