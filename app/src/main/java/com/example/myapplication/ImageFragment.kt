package com.example.myapplication

//okhttp3

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.*
import java.util.*
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth
import io.realm.Realm
import kotlinx.coroutines.delay
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var imageUri: Uri? = null
    private lateinit var view: View
    private lateinit var imageLauncher: ActivityResultLauncher<Intent>
    private lateinit var imagecropper: ActivityResultLauncher<Intent>
    private lateinit var dbref: DatabaseReference
    private  var date :String=""
    private  var marketname:String=""
    private  var city: String =""
    val arrlist: ArrayList<String> = ArrayList()

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
    ): View {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_image, container, false)

        returnsubdirectories()
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

        (view.findViewById(R.id.showAllBtn) as? Button)?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imageLauncher.launch(intent)
        }



        imageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    data?.data?.let {
                        imageUri = it
                        set_date_market_name()

                        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, null)
                        // Build the alert dialog using AlertDialog.Builder
                        val alertDialogBuilder = AlertDialog.Builder(requireContext())
                            .setView(dialogView)
                            .setPositiveButton("OK") { dialog, _ ->
                                val intent = CropImage.activity(it).getIntent(requireActivity())
                                imagecropper.launch(intent)
                            }

                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()


                        /*var destination : String = StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString()
                    val options:UCrop.Options=UCrop.Options()
                    uploadImage(view)
                    Toast.makeText(view.context,destination,Toast.LENGTH_LONG).show()
                    val intent = UCrop.of(it,Uri.fromFile(File(context?.cacheDir,destination)))
                        .getIntent(requireActivity())
                    imagecropper.launch(intent)*/

                        /*UCrop.of(Uri.parse(imageUri.toString()), Uri.fromFile(File(this.context?.cacheDir,destination)))
                        .withOptions(options)
                        .withAspectRatio(0F, 0F)
                        .useSourceImageAspectRatio()
                        .withMaxResultSize(2000, 2000)
                        .start(this.context as Activity)*/
                        /*cropLauncher.launch(UCrop.of(Uri.parse(imageUri.toString()), Uri.fromFile(File(requireContext().cacheDir, destination)))
                        .withOptions(options)
                        .withAspectRatio(0F, 0F)
                        .useSourceImageAspectRatio()
                        .withMaxResultSize(2000, 2000)
                        .getIntent(this.context as Activity))*/

                    }

                }
            }

        imagecropper =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if ((result.resultCode == Activity.RESULT_OK)) {

                    val output = CropImage.getActivityResult(result.data)
                    val resulturi = output.uri
                    if (resulturi != null) {
                        imageUri = resulturi
                        uploadImage(view)
                    } else {

                    }

                }
            }


        initVars()
        return view
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ImageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun initVars() {

        storageRef = FirebaseStorage.getInstance().reference.child("Images")
        firebaseFirestore = FirebaseFirestore.getInstance()
    }


    @SuppressLint("Range", "SuspiciousIndentation")
    private fun uploadImage(view: View) {
        val progressBar = view.findViewById(R.id.progressBar) as? ProgressBar
        val imageView = view.findViewById(R.id.imageView) as? ImageView

        if (progressBar != null) {
            progressBar.visibility = View.VISIBLE
        }



        storageRef = storageRef.child(System.currentTimeMillis().toString())
        imageUri?.let {
            storageRef.putFile(it).continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                // Get the download URL of the uploaded image
                storageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(view.context, "Image uploaded", Toast.LENGTH_SHORT).show()
                    //Toast.makeText(view.context, task.result.toString(), Toast.LENGTH_SHORT).show()
                    // Save the download URL to Firestore or perform any other desired action


                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val client_Primary = OkHttpClient()

                            val mediaType: MediaType? = "application/json".toMediaTypeOrNull()
                            val body: RequestBody =
                                "{\"response_as_dict\":true,\"attributes_as_list\":false,\"show_original_response\":false,\"providers\":\"microsoft\",\"language\":\"ar\",\"file_url\":\"${task.result.toString()}\"}"
                                    .toRequestBody(mediaType)
                            val request_Primary: Request = Request.Builder()
                                .url("https://api.edenai.run/v2/ocr/ocr_tables_async")
                                .post(body)
                                .addHeader("accept", "application/json")
                                .addHeader("content-type", "application/json")
                                .addHeader(
                                    "Authorization",
                                    "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZjUzMzRjNjktNjQwYi00YzkyLWE2MWYtN2E5YzQ4MWZhYTVlIiwidHlwZSI6ImFwaV90b2tlbiJ9.xMiTarGyjzTZABISEN-vDnoiAwLuxMxgpj9hcWSt5Q4"
                                )
                                .build()


                            val response_Primary = client_Primary.newCall(request_Primary).execute()
                            val result =
                                response_Primary.body?.string()?.let { it1 -> JSONObject(it1) }
                            val public_id = result?.getString("public_id")

                            delay(2000)

                            val client = OkHttpClient()

                            val request = Request.Builder()
                                .url("https://api.edenai.run/v2/ocr/ocr_tables_async/" + public_id + "?response_as_dict=true&show_original_response=false")
                                .get()
                                .addHeader("accept", "application/json")
                                .addHeader(
                                    "Authorization",
                                    "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZjUzMzRjNjktNjQwYi00YzkyLWE2MWYtN2E5YzQ4MWZhYTVlIiwidHlwZSI6ImFwaV90b2tlbiJ9.xMiTarGyjzTZABISEN-vDnoiAwLuxMxgpj9hcWSt5Q4"
                                )
                                .build()

                            val response = client.newCall(request).execute()


                            val jsonResponse = response.body?.string()
                                ?.let { it1 -> JSONObject(it1) }
                            val f = jsonResponse?.getJSONObject("results")
                                ?.getJSONObject("microsoft")
                                ?.getJSONArray("pages")
                                ?.getJSONObject(0)
                                ?.getJSONArray("tables")
                                ?.getJSONObject(0)
                                ?.getJSONArray("rows")
                            var num_cols = 0
                            num_cols = jsonResponse?.getJSONObject("results")
                                ?.getJSONObject("microsoft")
                                ?.getJSONArray("pages")
                                ?.getJSONObject(0)
                                ?.getJSONArray("tables")
                                ?.getJSONObject(0)
                                ?.getInt("num_cols")!!
                            val first_element = jsonResponse.getJSONObject("results")
                                .getJSONObject("microsoft")
                                .getJSONArray("pages")
                                .getJSONObject(0)
                                ?.getJSONArray("tables")
                                ?.getJSONObject(0)
                                ?.getJSONArray("rows")
                                ?.getJSONObject(1)
                                ?.getJSONArray("cells")
                                ?.getJSONObject(0)
                                ?.getString("text")
                            var name = ""
                            var price = 0.0
                            var quantity = 0.0
                            dbref = FirebaseDatabase.getInstance().getReference("المنتجات")
                            var subdirectoryName: String? = ""
                            if (!isNumeric(first_element.toString())) {
                                if (f != null) {
                                    for (i in 1 until f.length()) {
                                        println(first_element)
                                        println("size of f = " + f.length())
                                        val s = f.getJSONObject(i)
                                        val cells = s.getJSONArray("cells")
                                        val numCells = cells.length()
                                        println("size = $numCells")
                                        for (j in 0 until cells.length()) {

                                            val t = cells.getJSONObject(j)
                                            if (t["col_index"] == 0) {
                                                name = (t.getString("text"))

                                                if (arrlist.size != 0) {
                                                    for (h in arrlist) {
                                                        if (checkWordInSentence(
                                                                name,
                                                                "مكرونه"
                                                            )
                                                        ) {
                                                            subdirectoryName = "مكرونة"
                                                            break
                                                        }
                                                        if (checkWordInSentence(
                                                                name,
                                                                h
                                                            )
                                                        ) {
                                                            subdirectoryName = h
                                                            break
                                                        }
                                                        if (arrlist.indexOf(h) == arrlist.size - 1) {
                                                            subdirectoryName = "اخري"
                                                            break
                                                        }
                                                    }
                                                }

                                            } else if (t["col_index"] == 1) {
                                                if (isNumeric(t.getString("text")) == false) {
                                                    break
                                                }
                                                quantity = (t.getString("text")).toDouble()
                                            } else if (t["col_index"] == num_cols - 1) {
                                                if (isNumeric(t.getString("text")) == false) {
                                                    break
                                                }
                                                price = (t.getString("text")).toDouble()
                                            }
                                        }
                                        if (quantity != 0.0 && name != "" && marketname != "" && date != "" && price != 0.0) {
                                            val id: String =
                                                dbref.child(subdirectoryName.toString())
                                                    .push().key.toString()
                                            println("name of product : $name")
                                            val usermap = HashMap<String, Any>()
                                            usermap["id"] = id
                                            usermap["name"] = name
                                            usermap["price"] = price / quantity
                                            usermap["email"] =
                                                FirebaseAuth.getInstance().currentUser!!.email.toString()
                                            usermap["market"] = marketname
                                            usermap["date"] = date
                                            usermap["city"] = city
                                            dbref.child(subdirectoryName.toString()).child(
                                                id
                                            ).setValue(usermap)
                                            val realm: Realm = Realm.getDefaultInstance()
                                            realm.executeTransaction { r: Realm ->
                                                var item = r.createObject(
                                                    MyItem::class.java,
                                                    UUID.randomUUID().toString()
                                                )
                                                item.name = name
                                                item.date = date
                                                item.Item_id = id
                                                item.city = city
                                                item.market = marketname
                                                item.price = price / quantity
                                                item.type = subdirectoryName
                                                item.Email_Created =
                                                    FirebaseAuth.getInstance().currentUser!!.email.toString()
                                                realm.insertOrUpdate(item)
                                            }
                                            name = ""
                                            price = 0.0
                                            quantity = 0.0
                                        }
                                    }
                                }
                            } else {
                                if (f != null) {
                                    for (i in 1 until f.length()) {
                                        val s = f.getJSONObject(i)
                                        val cells = s.getJSONArray("cells")
                                        for (j in 0 until cells.length()) {

                                            val t = cells.getJSONObject(j)
                                            if (t["col_index"] == 0) {
                                                if (isNumeric(t.getString("text")) == false) {
                                                    println("col_index[0] isNumeric=false")
                                                    break
                                                }
                                                price = (t.getString("text")).toDouble()
                                            } else if (t["col_index"] == num_cols - 2) {
                                                if (isNumeric(t.getString("text")) == false) {
                                                    println("col_index[0] isNumeric=false")
                                                    break
                                                }
                                                quantity = (t.getString("text")).toDouble()
                                            } else if (t["col_index"] == num_cols - 1) {
                                                name = (t.getString("text"))

                                                if (arrlist.size != 0) {
                                                    for (h in arrlist) {
                                                        if (checkWordInSentence(
                                                                name,
                                                                "مكرونه"
                                                            )
                                                        ) {
                                                            subdirectoryName = "مكرونة"
                                                            break
                                                        }
                                                        if (checkWordInSentence(
                                                                name,
                                                                h
                                                            )
                                                        ) {
                                                            subdirectoryName = h
                                                            break
                                                        }
                                                        if (arrlist.indexOf(h) == arrlist.size - 1) {
                                                            subdirectoryName = "اخري"
                                                            break
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (quantity != 0.0 && name != "" && marketname != "" && date != "" && price != 0.0 && city != "") {
                                            val id: String =
                                                dbref.child(subdirectoryName.toString())
                                                    .push().key.toString()
                                            println("name of product : $name")
                                            val usermap = HashMap<String, Any>()
                                            usermap["id"] = id
                                            usermap["name"] = name
                                            usermap["price"] = price / quantity
                                            usermap["email"] =
                                                FirebaseAuth.getInstance().currentUser!!.email.toString()
                                            usermap["market"] = marketname
                                            usermap["date"] = date
                                            usermap["city"] = city
                                            dbref.child(subdirectoryName.toString()).child(
                                                id
                                            ).setValue(usermap)
                                            val realm: Realm = Realm.getDefaultInstance()
                                            realm.executeTransaction { r: Realm ->
                                                var item = r.createObject(
                                                    MyItem::class.java,
                                                    UUID.randomUUID().toString()
                                                )
                                                item.name = name
                                                item.date = date
                                                item.Item_id = id
                                                item.city = city
                                                item.market = marketname
                                                item.price = price / quantity
                                                item.type = subdirectoryName
                                                item.Email_Created =
                                                    FirebaseAuth.getInstance().currentUser!!.email.toString()
                                                realm.insertOrUpdate(item)
                                            }
                                            name = ""
                                            price = 0.0
                                            quantity = 0.0

                                        }
                                    }
                                }
                            }

                        } catch (e: Exception) {
                            println(e.toString())
                        }
                    }

                    if (progressBar != null) {
                        progressBar.visibility = View.GONE
                    }
                    // Create a Handler
                } else {
                    Toast.makeText(view.context, task.exception?.message, Toast.LENGTH_SHORT).show()
                    if (progressBar != null) {
                        progressBar.visibility = View.GONE
                    }
                    if (imageView != null) {
                        imageView.setImageResource(R.drawable.vector)
                    }
                }
            }
        }
    }
    private fun set_date_market_name() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val receiptOcrEndpoint =
                    "https://ocr.asprise.com/api/v1/receipt" // Receipt OCR API endpoint


                val imageFile = Uri.parse(imageUri.toString()).path?.let { it1 ->
                    File(
                        it1
                    )
                }
                val contentResolver: ContentResolver = view.context.contentResolver

                val imageStream =
                    contentResolver.openInputStream(Uri.parse(imageUri.toString()))
                val tempImageFile = File.createTempFile("temp_image", ".jpeg")
                tempImageFile.deleteOnExit()
                tempImageFile.outputStream().use { output ->
                    imageStream?.copyTo(output)
                }

                val client_asprise = OkHttpClient()

                val requestBody = imageFile?.let { it1 ->
                    MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(
                            "api_key",
                            "TEST"
                        ) // Use 'TEST' for testing purpose
                        .addFormDataPart(
                            "recognizer",
                            "auto"
                        ) // can be 'US', 'CA', 'JP', 'SG' or 'auto'
                        .addFormDataPart(
                            "ref_no",
                            "ocr_python_123"
                        ) // optional caller provided ref code
                        .addFormDataPart(
                            "file",
                            tempImageFile.name,
                            tempImageFile.asRequestBody("image/jpeg".toMediaType())
                        )
                        .build()
                }

                val request_asprise = requestBody?.let { it1 ->
                    Request.Builder()
                        .url(receiptOcrEndpoint)
                        .post(it1)
                        .build()
                }

                val response_asprise = request_asprise?.let { it1 ->
                    client_asprise.newCall(it1).execute()
                }
                val responseData = response_asprise?.body?.string()
                println(responseData)

                // Handle the JSON response
                val jsonResponse_response_asprise = JSONObject(responseData)
                date = jsonResponse_response_asprise.getJSONArray("receipts")
                    .getJSONObject(0)
                    .getString("date")
                val ocrText = jsonResponse_response_asprise.getJSONArray("receipts")
                    .getJSONObject(0)
                    .getString("ocr_text")

                var flag = false
                marketname = ""
                for (m in ocrText) {
                    if (m == ' ') {
                        if (flag) {
                            marketname += ' '
                            continue
                        }
                        continue
                    } else if (m == '\n') {
                        break
                    } else {
                        marketname += m
                        flag = true
                    }
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationListener = object : android.location.LocationListener {

            override fun onLocationChanged(p0: Location) {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                try {
                    val addresses: MutableList<Address>? =
                        geocoder.getFromLocation(p0.latitude, p0.longitude, 1)

                    if (addresses != null) {
                        if (addresses.isNotEmpty()) {
                            city=addresses[0].adminArea
                            // Perform any further actions with the city variable
                        }
                    }
                } catch (e: IOException) {
                    Toast.makeText(requireContext(),"failed to determine location",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        // Request location updates
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null)
    }
    fun checkWordInSentence(sentence: String, word: String): Boolean {
        val regex = Regex("\\b$word\\b")
        return regex.containsMatchIn(sentence)
    }
    private fun returnsubdirectories(){
        val databaseRef = FirebaseDatabase.getInstance().getReference("المنتجات")
        // Add a listener to the parent directory
        // Add a listener to the parent directory
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val subdirectoryName = childSnapshot.key
                    // Compare your attribute to the subdirectory name
                    if (subdirectoryName != null) {
                        arrlist.add(subdirectoryName)
                    }
                }
                println(arrlist[0])
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle potential errors here
                println("Error: " + databaseError.message)
            }

        })

    }

    private fun isNumeric(s: String): Boolean {
        return try {
            s.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
}