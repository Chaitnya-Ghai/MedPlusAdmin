package com.example.medplusadmin.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.medplusadmin.MainActivity
import com.example.medplusadmin.MyApplication
import com.example.medplusadmin.R
import com.example.medplusadmin.adapters.CategoryAdapter
import com.example.medplusadmin.convertCategoryObject
import com.example.medplusadmin.dataClasses.CategoryModel
import com.example.medplusadmin.databinding.CustomDialogBinding
import com.example.medplusadmin.databinding.FragmentCategoriesBinding
import com.example.medplusadmin.interfaces.CategoryInterface
import com.example.medplusadmin.interfaces.ClickType
import com.example.medplusadmin.uriToByteArray
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.firestore
import com.google.gson.GsonBuilder
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.UploadStatus
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.uploadAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoriesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val binding by lazy { FragmentCategoriesBinding.inflate(layoutInflater) }
    private lateinit var mainActivity:MainActivity
    private lateinit var customDialogBinding: CustomDialogBinding
    private var categoryArray = mutableListOf<CategoryModel>()
    private val collectionName = "category"
    private val updateStr="update"
    private  lateinit var categoryAdapter : CategoryAdapter
    private val db = Firebase.firestore
    private lateinit var supabaseClient: SupabaseClient
    private lateinit var imgUri:Uri
    private lateinit var publicUrl:String
    private var imageSource: String? = null
//
private var categoryPairsList = mutableListOf<Pair<String,String>>()//list of category id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity= activity as MainActivity
        supabaseClient = (mainActivity.application as MyApplication).supabaseClient
        customDialogBinding= CustomDialogBinding.inflate(layoutInflater)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        binding.categoryBtn
        categoryAdapter= CategoryAdapter(mainActivity,categoryArray,
            object : CategoryInterface{
                override fun onClick(position: Int, model: CategoryModel, onClickType: ClickType?) {
                    when(onClickType){
                        ClickType.update->{
                            openDialog(position)
                        }
                        ClickType.delete->{
                            AlertDialog.Builder(mainActivity).apply {
                                setTitle(" Are you sure?")
                                setPositiveButton("Delete") { _, _ ->
                                    db.collection(collectionName).document(model.id!!).delete()
                                }
                                setNegativeButton("No") { _, _ -> }
                                setCancelable(true)
                                show()
                            }
                        }
                        ClickType.onClick ->{
                            val bundle=Bundle()
                            bundle.putString("categorySelected",
                                GsonBuilder().create().toJson(categoryPairsList[position]))
                            Toast.makeText(mainActivity, "${categoryPairsList[position].second} is clicked", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_categoriesFragment_to_filterMedicinesFragment,bundle)
                        }
                        else -> {/*nothing*/}
                    }
                }
            }
        )
        binding.categoryRv.layoutManager = GridLayoutManager(mainActivity,1)
        binding.categoryRv.adapter = categoryAdapter
        categoryArray.clear()
        db.collection(collectionName).addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("TAG", "listen:error", e)
                return@addSnapshotListener
            }
            for(snapshot in snapshots!!.documentChanges) {
                val model = convertCategoryObject(snapshot.document)
                when(snapshot.type){
                    DocumentChange.Type.ADDED ->{model.let { categoryArray.add(it)
                        categoryPairsList.add(it.id.toString() to it.categoryName.toString()) }}
                    DocumentChange.Type.MODIFIED->{
                        model.let {
                            val index = getIndex(model)
                            if (index>-1){
                                categoryArray[index]=model
                            }
                        }
                    }
                    DocumentChange.Type.REMOVED->{
                        model.let {
                            val index = getIndex(model)
                            if (index>-1){
                                categoryArray.removeAt(index)
                            }
                        }
                    }
                }
                categoryAdapter.notifyDataSetChanged()
            }
        }
        binding.categoryBtn.setOnClickListener { openDialog() }
    }
    private fun getIndex(userModel: CategoryModel): Int {
        var index = -1
        index = categoryArray.indexOfFirst { element ->element.id?.equals(userModel.id) == true }
        return index
    }
    private fun openDialog(position:Int = -1){
        customDialogBinding= CustomDialogBinding.inflate(layoutInflater)
        val dialog = Dialog(mainActivity).apply {
            setContentView(customDialogBinding.root)
            setCancelable(false)
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            show()
        }
        customDialogBinding.cancelBtn.setOnClickListener { dialog.dismiss() }
//      select image from gallery
        customDialogBinding.imgDialog.setOnClickListener {
            if (mainActivity.arePermissionsGranted()){ openImagePicker() }
            else{
                mainActivity.checkAndRequirePermission()
            }
        }
        if (position > -1){
            customDialogBinding.saveBtn.text = updateStr
            customDialogBinding.name.setText(categoryArray[position].categoryName)
            Glide.with(mainActivity)
                .load(categoryArray[position].imageUrl)
                .centerCrop()
                .into(customDialogBinding.imgDialog)
            imageSource = categoryArray[position].imageUrl
        }
        customDialogBinding.saveBtn.setOnClickListener {
                if( customDialogBinding.name.text.isNullOrBlank() ){
                    customDialogBinding.name.error="Enter Category Name"
                }
                else if(imageSource==null){
                    Toast.makeText(mainActivity, "Select Image", Toast.LENGTH_SHORT).show()
                }
                else { /*if(imageSource !=null)*/
                    if (imageSource!!.startsWith("http")) {
                        Log.e("img not updated ", "Image is in from a URL: $imageSource")
                        storeDataToFireStore(url = imageSource!!, position)
                        dialog.dismiss()
                    } else {
                        uploadImageToSupabase(imgUri,position)
                        dialog.dismiss()
                    }
                }
        }
    }
    private fun storeDataToFireStore(url:String,position: Int){
        binding.loader.visibility=View.VISIBLE
        customDialogBinding.saveBtn.visibility=View.GONE
        if (position>-1){
            val data = CategoryModel(
                id = categoryArray[position].id,
                categoryName = customDialogBinding.name.text.toString(),
                imageUrl = url
            )
            db.collection(collectionName).document(categoryArray[position].id!!).set(data).addOnCompleteListener{
                if (it.isSuccessful){
                    binding.loader.visibility=View.GONE
                    customDialogBinding.saveBtn.visibility=View.VISIBLE
                    Toast.makeText(mainActivity, "Category Updated", Toast.LENGTH_SHORT).show()
                    categoryAdapter.notifyDataSetChanged()
                }
            }.addOnFailureListener {
                binding.loader.visibility=View.GONE
                customDialogBinding.saveBtn.visibility=View.VISIBLE
                Toast.makeText(mainActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }

        }
        else{
            val data = CategoryModel(
                categoryName = customDialogBinding.name.text.toString(),
                imageUrl = url
            )
            db.collection(collectionName).add(data).addOnCompleteListener{
                if (it.isSuccessful){
                    binding.loader.visibility=View.GONE
                    customDialogBinding.saveBtn.visibility=View.VISIBLE
                    Toast.makeText(mainActivity, "Category Added", Toast.LENGTH_SHORT).show()
                    categoryAdapter.notifyDataSetChanged()
                }
            }.addOnFailureListener {
                binding.loader.visibility=View.GONE
                customDialogBinding.saveBtn.visibility=View.VISIBLE
                Toast.makeText(mainActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                db.collection("medicines").document()
            }
        }

    }
    private fun uploadImageToSupabase(uri: Uri,position: Int){
        val byteArr = uriToByteArray(mainActivity,uri)
        val fileName ="categories/${System.currentTimeMillis()}.jpg"
        val bucket = supabaseClient.storage.from("MedPlus Admin")
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                bucket.uploadAsFlow(fileName,byteArr).collect{
                    status ->
                    withContext(Dispatchers.Main){
                        when(status){
                            is UploadStatus.Progress -> {
                                binding.loader.visibility=View.VISIBLE
                            }
                            is UploadStatus.Success ->{
                                val imageUrl = bucket.publicUrl(fileName)
                                val img = customDialogBinding.imgDialog
                                publicUrl=imageUrl
//                                call fun storeDataToFireStore
                                storeDataToFireStore(imageUrl,position)
                                Glide.with(mainActivity)
                                    .load(imageUrl)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .into(img)
                                binding.loader.visibility=View.GONE
                                imageSource=null// so that for next time if we add category without picking any img ,
                            // if imageSource!=null , it will pass last selected img , so they store it again
                            }
                        }
                    }
                }
            }
            catch (e:Exception){
                Toast.makeText(mainActivity, "upload to supabase Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // Step 3: Handle the selected image URI
                imgUri=uri
                Toast.makeText(context, "Image selected: $uri", Toast.LENGTH_SHORT).show()
                customDialogBinding.imgDialog.setImageURI(uri)
                imageSource = uri.toString()
            }
        }
    }
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"  // Filter for image files , file kai access bhi es le skte hai
        }
        imagePickerLauncher.launch(intent)  // Launch the image picker
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CategoriesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CategoriesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}