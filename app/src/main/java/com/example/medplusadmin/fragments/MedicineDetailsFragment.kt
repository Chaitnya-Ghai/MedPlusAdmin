package com.example.medplusadmin.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.medplusadmin.Constants.Companion.medicines
import com.example.medplusadmin.MainActivity
import com.example.medplusadmin.MyApplication
import com.example.medplusadmin.R
import com.example.medplusadmin.adapters.ShowCategoryAdapter
import com.example.medplusadmin.dataClasses.MedicineModel
import com.example.medplusadmin.dataClasses.ProductDetail
import com.example.medplusadmin.databinding.FragmentMedicineDetailsBinding
import com.example.medplusadmin.interfaces.ShowCategoryInterface
import com.example.medplusadmin.uriToByteArray
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.UploadStatus
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.uploadAsFlow
import io.ktor.util.toLowerCasePreservingASCIIRules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MedicineDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MedicineDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val binding by lazy { FragmentMedicineDetailsBinding.inflate(layoutInflater) }
    lateinit var mainActivity: MainActivity
    val db = Firebase.firestore
    lateinit var supabaseClient:SupabaseClient
    var selectedCategoryIdsList = mutableListOf<Pair<String, String>>()
    var showCategoryAdapter:ShowCategoryAdapter?=null
    var imageSource:String?=null
    var medicinId:String?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
        supabaseClient=(mainActivity.application as MyApplication).supabaseClient
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            medicinId=it.getString("medicineId")
        }
//        This ensures that fetchMedicineDetails() is only called when medicineId is not null.
        arguments?.getString("medicineId")?.let { fetchMedicineDetails(it) } ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.getDataByCategory { categoryList ->
            showCategoryAdapter = ShowCategoryAdapter(categoryList, object : ShowCategoryInterface {
                override fun tick(position: Int) {
                    categoryList[position].let { selectedCategoryIdsList.add(it) }
                    Toast.makeText(mainActivity, "added", Toast.LENGTH_SHORT).show()
                }
                override fun unTick(position: Int) {
                    categoryList[position].let { selectedCategoryIdsList.remove(it) }
                    Toast.makeText(mainActivity, "removed", Toast.LENGTH_SHORT).show()
                }
            })
            binding.categoriesSelected.adapter = showCategoryAdapter
            binding.categoriesSelected.layoutManager = GridLayoutManager(mainActivity, 4)
        }
        // Fetch categories and update adapter dynamically
        mainActivity.getDataByCategory { categoryList ->
            showCategoryAdapter?.updateData(categoryList)  // Update adapter when data is fetched
        }
        binding.saveMed.setOnClickListener {
            if (validation()){
                if (imageSource!!.startsWith("http")) {
                    Log.e("img not updated ", "Image is in from a URL: $imageSource")
                                storeDataToFireStore(imageSource!!)
                } else {
                    imageSource?.toUri()?.let { uploadImageToSupabase(it) }
                }
                Toast.makeText(mainActivity, "Medicine Added", Toast.LENGTH_SHORT).show() }
        }

        binding.circularImageView.setOnClickListener {
            if (mainActivity.arePermissionsGranted()){ openImagePicker() }
            else{
                mainActivity.checkAndRequirePermission()
            }
        }
    }
    val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if (result.resultCode== Activity.RESULT_OK){
            result.data?.data.let { it->
                binding.circularImageView.setImageURI(it)
                imageSource=it.toString()
            }
        }
    }
    fun validation(): Boolean {
        val fields = listOf(
            binding.etMedName to "Enter Medicine Name",
            binding.etPrice to "Enter Price",
            binding.etDescription to "Enter Description",
            binding.etIngredients to "Enter Ingredients",
            binding.etDosage to "Enter Dosage",
            binding.etUnits to "Enter Units",
            binding.etHowToUse to "Fill How to Use Medicine",
            binding.etPrecautions to "Fill the detail",
            binding.etStorage to "Fill Storage detail",
            binding.etSideEffect to "Fill the Side Effects of Medicine"
        )
        // Check if image is selected
        if (imageSource == null) {
            Toast.makeText(mainActivity, "Select Image", Toast.LENGTH_SHORT).show()
            return false
        }
        // Validate all fields
        for ((editText, errorMessage) in fields) {
            if (editText.text?.toString()?.trim().isNullOrEmpty()) {
                editText.error = errorMessage
                return false
            }
        }
        // Check expiry date
        if (binding.etExpiryDate.text.isNullOrEmpty()) {
            Toast.makeText(mainActivity, "Enter Expiry Date", Toast.LENGTH_SHORT).show()
            return false
        }
        // Check category selection
        if (selectedCategoryIdsList.isEmpty()) {
            Toast.makeText(mainActivity, "Select Category", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    fun uploadImageToSupabase(uri: Uri) {
        val byteArr = uriToByteArray(mainActivity, uri)
        val fileName = "medicines/${System.currentTimeMillis()}.jpg"
        val bucket = supabaseClient.storage.from("MedPlus Admin")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                bucket.uploadAsFlow(fileName, byteArr).collect { status ->
                    withContext(Dispatchers.Main) {
                        when (status) {
                            is UploadStatus.Progress -> {
                                binding.loader.visibility = View.VISIBLE
                            }
                            is UploadStatus.Success -> {
                                val imageUrl = bucket.publicUrl(fileName)
                                imageSource = imageUrl
                                storeDataToFireStore(imageUrl)
                                // Load image into ImageView
                                Glide.with(mainActivity)
                                    .load(imageUrl)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .into(binding.circularImageView)

                                binding.loader.visibility = View.GONE
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(mainActivity, "Upload Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun storeDataToFireStore(imageSource: String) {
        binding.loader.visibility = View.VISIBLE
        binding.saveMed.isClickable = false

        val medicineModel = MedicineModel(
            medId = db.collection(medicines).document().id,
            medicineName = binding.etMedName.text.toString().toLowerCasePreservingASCIIRules(),
            medicineImg = imageSource,
            description = binding.etDescription.text.toString(),
            ingredients = binding.etIngredients.text.toString(),
            howToUse = binding.etHowToUse.text.toString(),
            precautions = binding.etPrecautions.text.toString(),
            storageInfo = binding.etStorage.text.toString(),
            sideEffects = binding.etSideEffect.text.toString(),
            unit =  binding.etUnits.text.toString(),
            dosageForm =  binding.etDosage.text.toString(),
            belongingCategory = selectedCategoryIdsList.map { it.first }.toMutableList(),
            productDetail = ProductDetail(
                originalPrice = binding.etPrice.text.toString(),
                brandName = binding.etUnits.text.toString(),
                expiryDate = binding.etExpiryDate.text.toString()
            )
        )
        if (medicinId != null){
//            update item code
            db.collection(medicines).document(medicinId!!).set(medicineModel)
                .addOnSuccessListener {
                    binding.loader.visibility = View.GONE
                    Handler(Looper.getMainLooper()).post {
                        if (isAdded) {
                            findNavController().popBackStack()
                            Toast.makeText(requireContext(), "Medicine Updated", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener {
                    binding.loader.visibility = View.GONE
                    binding.saveMed.isClickable = true
                    Toast.makeText(mainActivity, "Failed to update medicine", Toast.LENGTH_SHORT).show()
                }
        }
        else{
            db.collection(medicines).add(medicineModel)
                .addOnSuccessListener {
                    binding.loader.visibility = View.GONE
                    Handler(Looper.getMainLooper()).post {
                        if (isAdded) {
                            findNavController().popBackStack()
                            Toast.makeText(requireContext(), "Medicine Added", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener {
                    binding.loader.visibility = View.GONE
                    binding.saveMed.isClickable = true
                    Toast.makeText(mainActivity, "Failed to add medicine", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"  // Filter for image files , file kai access bhi es le skte hai
        }
        imagePickerLauncher.launch(intent)  // Launch the image picker
    }



    fun updateUI(medicine: MedicineModel) {
        selectedCategoryIdsList.clear()
        Glide.with(mainActivity).apply {
            load(medicine.medicineImg).into(binding.circularImageView)
        }
        binding.etMedName.setText(medicine.medicineName)
        binding.etPrice.setText(medicine.productDetail?.originalPrice)
        binding.etDescription.setText(medicine.description)
        binding.etIngredients.setText(medicine.ingredients)
        binding.etDosage.setText(medicine.dosageForm)
        binding.etUnits.setText(medicine.unit)
        binding.etHowToUse.setText(medicine.howToUse)
        binding.etPrecautions.setText(medicine.precautions)
        binding.etStorage.setText(medicine.storageInfo)
        binding.etSideEffect.setText(medicine.sideEffects)
        binding.etExpiryDate.setText(medicine.productDetail?.expiryDate)
        medicine.belongingCategory?.let { selectedCategoryIdsList.addAll(it.map { Pair(it, it) }) }
//        add krliya hai but ui pr set karana hai.
        imageSource=medicine.medicineImg
    }

    private fun fetchMedicineDetails(medicineId: String) {
        Firebase.firestore.collection(medicines).document(medicineId)
            .get()
            .addOnSuccessListener { document ->
                val medicine = document.toObject(MedicineModel::class.java)
                if (medicine != null) {
                    updateUI(medicine)
                }
            }
            .addOnFailureListener { e ->
                Log.e("MedicineDetails", "Error fetching data: ${e.message}")
            }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MedicineDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MedicineDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}