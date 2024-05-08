package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UploadImage.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadImage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var firebaseFirestore: FirebaseFirestore
    private var mList = mutableListOf<String>()
    private lateinit var adapter: ImagesAdapter
    private lateinit var view:View

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
        view = inflater.inflate(R.layout.fragment_upload_image, container, false)
        initVars()
        getImages()
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UploadImage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UploadImage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("MissingInflatedId")
    private fun initVars() {
        firebaseFirestore = FirebaseFirestore.getInstance()
        val inflater=LayoutInflater.from(this.context)
        val v=inflater.inflate(R.layout.fragment_image,null)
        val recyclerView=v.findViewById(R.id.recyclerView) as? RecyclerView
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true)
        }
        if (recyclerView != null) {
            recyclerView.layoutManager = LinearLayoutManager(this.context)
        }
        adapter = ImagesAdapter(mList)
        if (recyclerView != null) {
            recyclerView.adapter = adapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getImages(){
        val inflater=LayoutInflater.from(this.context)
        val v=inflater.inflate(R.layout.fragment_image,null)
        val progressBar=v.findViewById(R.id.progressBar) as? ProgressBar
        if (progressBar != null) {
            progressBar.visibility = View.VISIBLE
        }
        firebaseFirestore.collection("images")
            .get().addOnSuccessListener {
                for(i in it){
                    mList.add(i.data["pic"].toString())
                }
                adapter.notifyDataSetChanged()
                if (progressBar != null) {
                    progressBar.visibility = View.GONE
                }
            }
    }
}