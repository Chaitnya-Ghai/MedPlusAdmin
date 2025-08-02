package com.example.medplusadmin.presentation.screens.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.medplusadmin.presentation.screens.activities.MainActivity
import com.example.medplusadmin.R
import com.example.medplusadmin.presentation.adapters.MedicineAdapter
import com.example.medplusadmin.domain.models.Medicine
import com.example.medplusadmin.databinding.FragmentMedicinesBinding
import com.example.medplusadmin.presentation.interfaces.MedicineInterface
import com.example.medplusadmin.presentation.interfaces.medicineCLick
import com.example.medplusadmin.presentation.viewModels.CatalogViewModel
import com.example.medplusadmin.utils.Resource
import com.example.medplusadmin.utils.collectFlowSafely
import com.example.medplusadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class MedicinesFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private val catalogViewModel: CatalogViewModel by viewModels()

    private var _binding: FragmentMedicinesBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity
    private val medicineList = mutableListOf<Medicine>()
    private  lateinit var medicineAdapter: MedicineAdapter

    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("DeleteCategory", "Coroutine error: ${exception.localizedMessage}")
        showToast("Something went wrong: ${exception.localizedMessage}")
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

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
    ): View?{
        _binding = FragmentMedicinesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Search-Bar Setup
        binding.searchCategory.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                catalogViewModel.updateSearchQueryMedicine(query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                catalogViewModel.updateSearchQueryMedicine(newText.orEmpty())
                return true
            }

        })

        collectFlowSafely(handler) {
            catalogViewModel.filteredMedicines.collect { state ->
                binding.loader.visibility = View.GONE
                medicineList.clear()
                when (state) {
                    is Resource.Loading -> {
                        binding.loader.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.loader.visibility = View.GONE
                        val data = state.data

                        if (!data.isNullOrEmpty()) {
                            medicineList.addAll(data)
                            Log.d("MedicineFragment", "Fetched ${medicineList.size} categories.")
                        } else {
                            Log.e("MedicineFragment", "No categories found.")
                        }

                        medicineAdapter.notifyDataSetChanged()
                    }

                    is Resource.Error -> {
                        binding.loader.visibility = View.GONE
                        mainActivity.showToast(state.message ?: "Unknown Error")
                    }
                }
            }
        }

        medicineAdapter = MedicineAdapter(mainActivity, medicineList, object : MedicineInterface {
            override fun onMedClick(position: Int, model: Medicine, onMedicineClickType: medicineCLick?) {
                when (onMedicineClickType) {

                    medicineCLick.delete -> {
                        AlertDialog.Builder(mainActivity).apply {
                            setTitle("Are you sure?")
                            setPositiveButton("Delete") { _, _ ->
                                lifecycleScope.launch(handler) {
                                    val id = medicineList[position].medId
                                    val deletedItem = medicineList[position]

                                    Log.d("DeleteMedicine", "Trying to delete position: $id, name: ${deletedItem.medicineName}")

                                    val result = catalogViewModel.deleteMedicine(id)

                                    if (result is Resource.Success) {
                                        Log.e("DeleteMedicine", "Successfully deleted: $deletedItem")

                                        mainActivity.showToast("${deletedItem.medicineName} deleted")
                                    } else if (result is Resource.Error) {
                                        mainActivity.showToast("Failed to delete: ${result.message}")
                                        Log.e("DeleteMedicine", "Error: ${result.message}")
                                    }
                                }
                            }
                            setNegativeButton("Cancel", null)
                            setCancelable(true)
                            show()
                        }
                    }

                    medicineCLick.onclick -> {
                        val bundle = Bundle().apply {
                            putString("medicineId", medicineList[position].medId)
                        }
                        findNavController().navigate(
                            R.id.action_medicinesFragment_to_showSingleMedicineFragment,
                            bundle
                        )
                    }

                    medicineCLick.update -> {
                        val bundle = Bundle().apply {
                            putString("medicineId", medicineList[position].medId)
                        }
                        findNavController().navigate(
                            R.id.action_medicinesFragment_to_medicineDetailsFragment,
                            bundle
                        )
                    }

                    else -> { /* Do nothing */ }
                }
            }
        })
        binding.MedicinesRv.adapter = medicineAdapter
        binding.MedicinesRv.layoutManager = GridLayoutManager(mainActivity,2)

        binding.loader.visibility = View.VISIBLE
        binding.addMedicine.setOnClickListener {
            findNavController().navigate(R.id.action_medicinesFragment_to_medicineDetailsFragment)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
