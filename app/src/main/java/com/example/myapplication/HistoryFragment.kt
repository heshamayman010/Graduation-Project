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
import com.google.firebase.auth.FirebaseUser
import io.realm.Realm

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() ,MenuProvider {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var myrecycler_view: RecyclerView
    private lateinit var arrlist: ArrayList<RecyclerView_Data>
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var items:ArrayList<FollowItem>
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
        val view:View=inflater.inflate(R.layout.fragment_history,container,false)
        activity?.addMenuProvider(this,viewLifecycleOwner, Lifecycle.State.RESUMED)
        toolbar = view.findViewById(R.id.toolbar_history)
        var activity: AppCompatActivity = getActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setTitle("History")
        myrecycler_view = view.findViewById(R.id.my_fragment_recycler_history)
        myrecycler_view.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        arrlist = arrayListOf<RecyclerView_Data>()
        this.context?.let { getUserData(it) }
        return view
    }
    val user = FirebaseAuth.getInstance().currentUser
    private fun getUserData(c: Context) {
        Realm.init(c)
        var realm: Realm = Realm.getDefaultInstance()
        items=ArrayList<FollowItem>()
        val items_result =realm.where(FollowItem::class.java).equalTo("Added_Email",user!!.email).findAll()
        items.addAll(realm.copyFromRealm(items_result))
        myrecycler_view.adapter = Realm_History(c, items, this)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

    private fun searchByName(s: String) {
        var search_list=ArrayList<FollowItem>()
        search_list.clear()
        for(el in items)
        {
            if(el.name?.startsWith(s,ignoreCase = true)==true)
            {
                search_list.add(el)
            }
        }
        myrecycler_view.adapter=Realm_History(myrecycler_view.context,search_list,this)
    }

    public fun reloadfragment(){
        this.context?.let { getUserData(it) }
    }
}