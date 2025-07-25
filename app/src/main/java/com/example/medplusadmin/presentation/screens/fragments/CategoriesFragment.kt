package com.example.medplusadmin.presentation.screens.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog.*
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.medplusadmin.presentation.screens.activities.MainActivity
import com.example.medplusadmin.R
import com.example.medplusadmin.presentation.adapters.CategoryAdapter
import com.example.medplusadmin.domain.models.Category
import com.example.medplusadmin.databinding.CustomDialogBinding
import com.example.medplusadmin.databinding.FragmentCategoriesBinding
import com.example.medplusadmin.presentation.interfaces.CategoryInterface
import com.example.medplusadmin.presentation.interfaces.ClickType
import com.example.medplusadmin.presentation.viewModels.CatalogViewModel
import com.example.medplusadmin.utils.CatalogUIEvent
import com.example.medplusadmin.utils.Resource
import com.example.medplusadmin.utils.collectFlowSafely
import com.example.medplusadmin.utils.uriToByteArray
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.UploadStatus
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.uploadAsFlow
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class CategoriesFragment @Inject constructor() : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    @Inject
    lateinit var supabaseClient: SupabaseClient // till logic not implemented in mvvm
    private val catalogViewModel: CatalogViewModel by viewModels()
    private val binding by lazy { FragmentCategoriesBinding.inflate(layoutInflater) }
    private lateinit var mainActivity:MainActivity
    private lateinit var customDialogBinding: CustomDialogBinding
    private var categoryArray = mutableListOf<Category>()
    private  lateinit var categoryAdapter : CategoryAdapter
    private lateinit var imgUri:Uri
    private lateinit var publicUrl:String
    private var imageSource: String? = null
    private var categoryPairsList = mutableListOf<Pair<String,String>>()
    //        CoroutineExceptionHandler
    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("DeleteCategory", "Coroutine error: ${exception.localizedMessage}")
        Toast.makeText(
            context,
            "Something went wrong: ${exception.localizedMessage}",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity= activity as MainActivity
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
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Adapter setup
        categoryAdapter = CategoryAdapter(mainActivity, categoryArray, object : CategoryInterface {
            override fun onClick(position: Int, model: Category, onClickType: ClickType?) {
                when (onClickType) {
                    ClickType.update -> {
                        openDialog(position)
                        Log.e("open dialog", "update onClick: $position ->, ${model.categoryName} ", )
                    }

                    ClickType.delete -> {
                        Builder(mainActivity).apply {
                            setTitle("Are you sure?")
                            setPositiveButton("Delete") { _, _ ->
                                lifecycleScope.launch(handler) {
                                    val id = categoryArray[position].id
                                    val deletedItem = categoryArray[position]
                                    Log.d("DeleteCategory", "Trying to delete position: $position, name: ${deletedItem.categoryName}")

                                    val result = catalogViewModel.deleteCategory(id)

                                    if (result is Resource.Success) {
                                        Log.e("DeleteCategory", "Successfully deleted: $deletedItem")
                                       /* // Optionally show a toast// */ Toast.makeText(mainActivity, "${deletedItem.categoryName} deleted", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            }
                            setNegativeButton("No", null)
                            setCancelable(true)
                            show()
                        }
                    }

                    ClickType.onClick -> {
                        val bundle = Bundle().apply {
                            putString("categoryId", categoryPairsList[position].first)
                        }
                        findNavController().navigate(
                            R.id.action_categoriesFragment_to_filterMedicinesFragment,
                            bundle
                        )
                    }

                    else -> {}
                }
            }
        })
        binding.categoryRv.layoutManager = GridLayoutManager(mainActivity, 1)
        binding.categoryRv.adapter = categoryAdapter

        collectFlowSafely {
            catalogViewModel.categories.collect { state ->
                binding.loader.visibility = View.GONE
                categoryArray.clear()
                when (state) {
                    is Resource.Loading -> {
                        binding.loader.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.loader.visibility = View.GONE
                        val data = state.data

                        if (!data.isNullOrEmpty()) {
                            categoryArray.addAll(data)
                            Log.d("CategoryFragment", "Fetched ${categoryArray.size} categories.")

                            categoryPairsList.clear()
                            categoryArray.forEach {
                                categoryPairsList.add(it.id.toString() to it.categoryName.toString())
                            }
                        } else {
                            Log.e("CategoryFragment", "No categories found.")
                        }

                        categoryAdapter.notifyDataSetChanged()
                    }

                    is Resource.Error -> {
                        binding.loader.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            state.message ?: "Unknown Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        //   add category
        lifecycleScope.launch(handler) {
            catalogViewModel.upsertCategoryState.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.loader.visibility = View.VISIBLE
                        customDialogBinding.saveBtn.isClickable = false
                    }

                    is Resource.Success -> {
                        binding.loader.visibility = View.GONE
                        customDialogBinding.saveBtn.isClickable = true

                    }

                    is Resource.Error -> {
                        binding.loader.visibility = View.GONE
                        customDialogBinding.saveBtn.isClickable = true
                    }
                }
            }
        }
        binding.categoryBtn.setOnClickListener {
            openDialog()
        }
        lifecycleScope.launchWhenStarted {
            catalogViewModel.uiEvent.collect { event ->
                when (event) {
                    is CatalogUIEvent.OpenCategoryImagePicker -> openImagePicker() // In CategoriesFragment
                    is CatalogUIEvent.OpenMedicineImagePicker -> openImagePicker() // In MedicineFragment
                }
            }
        }


    }


    private fun openDialog(position:Int = -1){
        Log.e("open dialog", "onClick: $position " )
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
            if (mainActivity.arePermissionsGranted()){ catalogViewModel.onCategoryImageClick() }
            else{
                mainActivity.checkAndRequirePermission()
            }
        }
        if (position > -1){
            customDialogBinding.saveBtn.text = resources.getString(R.string.update)
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

    private fun storeDataToFireStore(url: String, position: Int) {
        binding.loader.visibility=View.VISIBLE
        customDialogBinding.saveBtn.visibility=View.GONE
        val data = if (position > -1) {
            Category(
                id = categoryArray[position].id,
                categoryName = customDialogBinding.name.text.toString().lowercase(Locale.ROOT),
                imageUrl = url
            )
        } else {
            Category(
                id = "", // will be set in repo
                categoryName = customDialogBinding.name.text.toString().lowercase(Locale.ROOT),
                imageUrl = url
            )
        }

        lifecycleScope.launch(handler) {
            catalogViewModel.upsertCategories(data)
        }
    }
    private fun uploadImageToSupabase(uri: Uri,position: Int){
        val byteArr = uriToByteArray(mainActivity,uri)
        val fileName ="categories/${System.currentTimeMillis()}.jpg"
        val bucket = supabaseClient.storage.from("MedPlus Admin")
        lifecycleScope.launch(handler + Dispatchers.IO) {
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
                                imageSource=null
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
                // Handle the selected image URI
                imgUri=uri
                Toast.makeText(context, "Image selected: $uri", Toast.LENGTH_SHORT).show()
                customDialogBinding.imgDialog.setImageURI(uri)
                imageSource = uri.toString()
            }
        }
    }
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }
}

