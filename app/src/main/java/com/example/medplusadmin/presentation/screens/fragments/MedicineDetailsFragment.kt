package com.example.medplusadmin.presentation.screens.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.medplusadmin.presentation.screens.activities.MainActivity
import com.example.medplusadmin.R
import com.example.medplusadmin.presentation.adapters.ShowCategoryAdapter
import com.example.medplusadmin.domain.models.Medicine
import com.example.medplusadmin.domain.models.ProductDetail
import com.example.medplusadmin.databinding.FragmentMedicineDetailsBinding
import com.example.medplusadmin.presentation.interfaces.ShowCategoryInterface
import com.example.medplusadmin.presentation.viewModels.CatalogViewModel
import com.example.medplusadmin.utils.Resource
import com.example.medplusadmin.utils.collectSafelyWithFlow
import com.example.medplusadmin.utils.showToast
import com.example.medplusadmin.utils.uriToByteArray
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.UploadStatus
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.uploadAsFlow
import io.ktor.util.toLowerCasePreservingASCIIRules
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.getValue


@AndroidEntryPoint
class MedicineDetailsFragment : Fragment() {

    @Inject lateinit var supabaseClient: SupabaseClient
    private val binding by lazy { FragmentMedicineDetailsBinding.inflate(layoutInflater) }

    lateinit var mainActivity: MainActivity
    private val viewModel: CatalogViewModel by viewModels()

    var medicineId: String? = null
    var imageSource: String? = null

    var selectedCategoryIdsList = mutableListOf<Pair<String, String>>()
    var showCategoryAdapter: ShowCategoryAdapter? = null
    private val categoryArray = mutableListOf<Pair<String, String>>()

    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("MedicineDetails", "Coroutine error: ${exception.localizedMessage}")
        showToast("Something went wrong: ${exception.localizedMessage}")
    }
    private var categoryMap: Map<String, String> = emptyMap()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        medicineId = arguments?.getString("medicineId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryAdapter()
        observeCategories()
        observeMedicineState()
        observeUpsertMedicineState()
        medicineId?.let { medId ->
            viewModel.getMedicineById(medId)
        }
        setupClickListeners()
    }

    /** Setup RecyclerView adapter for categories */
    private fun setupCategoryAdapter() {
        showCategoryAdapter = ShowCategoryAdapter(categoryArray, object : ShowCategoryInterface {
            override fun tick(position: Int) {
                selectedCategoryIdsList.add(categoryArray[position])
                Toast.makeText(mainActivity, "Added", Toast.LENGTH_SHORT).show()
            }

            override fun unTick(position: Int) {
                selectedCategoryIdsList.remove(categoryArray[position])
                Toast.makeText(mainActivity, "Removed", Toast.LENGTH_SHORT).show()
            }
        })
        binding.categoriesSelected.adapter = showCategoryAdapter
        binding.categoriesSelected.layoutManager = GridLayoutManager(mainActivity, 3)
    }

    /** Collect categories flow and update adapter */
    private fun observeCategories() {
        collectSafelyWithFlow(viewModel.categories, handler) { state ->
            binding.loader.visibility = View.GONE
            when (state) {
                is Resource.Loading -> binding.loader.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.loader.visibility = View.GONE
                    state.data?.let { list ->
                        categoryMap = emptyMap()
                        categoryMap = list.associateBy({ it.id }, { it.categoryName })

                        categoryArray.clear()
                        categoryArray.addAll(list.map { it.id to it.categoryName })
                        showCategoryAdapter?.updateData(categoryArray)
                    }
                }
                is Resource.Error -> {
                    binding.loader.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message ?: "Unknown Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /** Collect medicine flow and update UI */
    private fun observeMedicineState() {
        collectSafelyWithFlow(viewModel.medicineState, handler) { resource ->
            when (resource) {
                is Resource.Loading -> binding.loader.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.loader.visibility = View.GONE
                    resource.data?.let { medicines ->
                        if (medicines.isNotEmpty()) {
                            updateUI(medicines.first())
                        }
                    }
                }
                is Resource.Error -> {
                    binding.loader.visibility = View.GONE
                    Toast.makeText(mainActivity , resource.message ?: "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /** Collect medicine upsert state */
    private fun observeUpsertMedicineState() {
        lifecycleScope.launch {
            viewModel.upsertMedicineState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> binding.loader.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding.loader.visibility = View.GONE
                        Toast.makeText(requireContext(), "Medicine saved!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    is Resource.Error -> {
                        binding.loader.visibility = View.GONE
                        Toast.makeText(requireContext(), resource.message ?: "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /** Setup button and image click listeners */
    private fun setupClickListeners() {
        binding.saveMed.setOnClickListener {
            if (validateFields()) {
                imageSource?.let { src ->
                    if (src.startsWith("http")) {
                        storeDataToFireStore(src)
                    } else {
                        src.toUri().let { uploadImageToSupabase(it) }
                    }
                }
            }
        }

        binding.circularImageView.setOnClickListener {
            if (mainActivity.arePermissionsGranted()) openImagePicker()
            else mainActivity.checkAndRequirePermission()
        }
    }

    /** Image picker launcher */
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let {
                binding.circularImageView.setImageURI(it)
                imageSource = it.toString()
            }
        }
    }

    /** Validate all fields before saving */
    private fun validateFields(): Boolean {
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

        if (imageSource.isNullOrEmpty()) {
            Toast.makeText(mainActivity, "Select Image", Toast.LENGTH_SHORT).show()
            return false
        }

        for ((editText, msg) in fields) {
            if (editText.text?.toString()?.trim().isNullOrEmpty()) {
                editText.error = msg
                return false
            }
        }

        if (binding.etExpiryDate.text.isNullOrEmpty()) {
            Toast.makeText(mainActivity, "Enter Expiry Date", Toast.LENGTH_SHORT).show()
            return false
        }

        if (selectedCategoryIdsList.isEmpty()) {
            Toast.makeText(mainActivity, "Select Category", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    /** Upload image to Supabase */
    private fun uploadImageToSupabase(uri: Uri) {
        val byteArr = uriToByteArray(mainActivity, uri)
        val fileName = "medicines/${System.currentTimeMillis()}.jpg"
        val bucket = supabaseClient.storage.from("MedPlus Admin")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                bucket.uploadAsFlow(fileName, byteArr).collect { status ->
                    withContext(Dispatchers.Main) {
                        when (status) {
                            is UploadStatus.Progress -> binding.loader.visibility = View.VISIBLE
                            is UploadStatus.Success -> {
                                val imageUrl = bucket.publicUrl(fileName)
                                imageSource = imageUrl
                                storeDataToFireStore(imageUrl)
                                Glide.with(mainActivity)
                                    .load(imageUrl)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .into(binding.circularImageView)
                                binding.loader.visibility = View.GONE
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

    /** Store medicine data in Firestore */
    private fun storeDataToFireStore(imageSource: String) {
        binding.loader.visibility = View.VISIBLE
        binding.saveMed.isClickable = false

        val medIdToUse = medicineId ?: ""
        val medicine = Medicine(
            medId = medIdToUse,
            medicineName = binding.etMedName.text.toString().toLowerCasePreservingASCIIRules(),
            medicineImg = imageSource,
            description = binding.etDescription.text.toString(),
            ingredients = binding.etIngredients.text.toString(),
            howToUse = binding.etHowToUse.text.toString(),
            precautions = binding.etPrecautions.text.toString(),
            storageInfo = binding.etStorage.text.toString(),
            sideEffects = binding.etSideEffect.text.toString(),
            unit = binding.etUnits.text.toString(),
            dosageForm = binding.etDosage.text.toString(),
            belongingCategory = selectedCategoryIdsList.map { it.first }.toMutableList(),
            productDetail = ProductDetail(
                originalPrice = binding.etPrice.text.toString(),
                brandName = binding.etUnits.text.toString(),
                expiryDate = binding.etExpiryDate.text.toString()
            )
        )
        viewModel.upsertMedicine(medicine)
    }

    /** Open image picker */
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        imagePickerLauncher.launch(intent)
    }

    /** Update UI with fetched medicine */
    private fun updateUI(medicine: Medicine) {
        Glide.with(mainActivity)
            .load(medicine.medicineImg)
            .placeholder(R.mipmap.ic_launcher)
            .into(binding.circularImageView)

        binding.etMedName.setText(medicine.medicineName)
        binding.etPrice.setText(medicine.productDetail.originalPrice ?: "")
        binding.etDescription.setText(medicine.description)
        binding.etIngredients.setText(medicine.ingredients)
        binding.etDosage.setText(medicine.dosageForm)
        binding.etUnits.setText(medicine.unit)
        binding.etHowToUse.setText(medicine.howToUse)
        binding.etPrecautions.setText(medicine.precautions)
        binding.etStorage.setText(medicine.storageInfo)
        binding.etSideEffect.setText(medicine.sideEffects)
        binding.etExpiryDate.setText(medicine.productDetail.expiryDate ?: "")

        // Update adapter with all categories
        showCategoryAdapter?.updateData(categoryMap.map { it.key to it.value })

        // Preselect categories that belong to this medicine
        showCategoryAdapter?.setSelectedItems(medicine.belongingCategory)

        imageSource = medicine.medicineImg
    }
}
