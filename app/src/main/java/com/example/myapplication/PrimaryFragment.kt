package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PrimaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PrimaryFragment : Fragment() , MenuProvider,SelectListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var dbref: DatabaseReference
    private lateinit var myrecycler_view: RecyclerView
    private lateinit var arrlist: ArrayList<String>
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private var city: String=""
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, start retrieving location
            getCurrentLocation()
        }
        activity?.addMenuProvider(this,viewLifecycleOwner, Lifecycle.State.RESUMED)
        val view: View = inflater.inflate(R.layout.fragment_primary, container, false)
        toolbar = view.findViewById(R.id.toolbar)
        var activity: AppCompatActivity = getActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setTitle("Main")
        myrecycler_view = view.findViewById(R.id.my_fragment_recycler)
        myrecycler_view.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        arrlist = arrayListOf<String>()
        this.context?.let { getUserData(it) }




        return view.rootView

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PrimaryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PrimaryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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
                        val category = i.key
                        // checking if the name searched is available and adding it to the array list
                        if (category!!.lowercase().startsWith(s.lowercase())) {
                            arrlist.add(category)
                        }
                    }
                    //setting data to RecyclerView
                    myrecycler_view.adapter = PrimaryAdapter(this@PrimaryFragment,arrlist)
                } else {
                    Toast.makeText(context, "Data does not exist", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getUserData(c: Context) {
        dbref = FirebaseDatabase.getInstance().getReference("المنتجات")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val subdirectoryName = childSnapshot.key
                    // Compare your attribute to the subdirectory name
                    arrlist.add(subdirectoryName.toString())
                }
                myrecycler_view.adapter = PrimaryAdapter(this@PrimaryFragment,arrlist)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
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
        TODO("Not yet implemented")
    }

    @SuppressLint("ResourceType")
    override fun onItemClicked(name: String) {
        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()
        if (transaction != null) {
            transaction.replace(com.example.myapplication.R.id.frameLayout, HomeFragment.newInstance(name,city))
        }
        if (transaction != null) {
            transaction.addToBackStack(null)
        }
        if (transaction != null) {
            transaction.commit()
        }
    }

    private fun getCurrentLocation() {
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setNumUpdates(1)
            .setInterval(0)

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                try {
                    val addresses: List<Address>? =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)

                    if (addresses != null && addresses.isNotEmpty()) {
                        city = addresses[0].adminArea
                        // Perform any further actions with the city variable
                    }
                } catch (e: IOException) {
                    Toast.makeText(requireContext(), "Failed to determine location", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Request location updates
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }


}