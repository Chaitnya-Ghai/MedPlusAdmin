package com.example.medplusadmin.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.medplusadmin.Constants.Companion.medicines
import com.example.medplusadmin.MainActivity
import com.example.medplusadmin.R
import com.example.medplusadmin.adapters.MedicineAdapter
import com.example.medplusadmin.dataClasses.MedicineModel
import com.example.medplusadmin.databinding.FragmentFilterMedicinesBinding
import com.example.medplusadmin.interfaces.MedicineInterface
import com.example.medplusadmin.interfaces.medicineCLick
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FilterMedicinesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FilterMedicinesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var categoryId:String?=null
    val db= Firebase.firestore
    val binding by lazy { FragmentFilterMedicinesBinding.inflate(layoutInflater) }
    lateinit var mainActivity: MainActivity
    val filterList = mutableListOf<MedicineModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
        arguments?.let {
            categoryId = it.getString("categoryId")
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.selectedRv.layoutManager = GridLayoutManager(mainActivity, 2)
        binding.selectedRv.adapter = MedicineAdapter(mainActivity, filterList, object : MedicineInterface {
            override fun onMedClick(
                position: Int,
                model: MedicineModel,
                onMedicineClickType: medicineCLick?
            ) {
                when (onMedicineClickType) {
                    medicineCLick.delete -> {
                        AlertDialog.Builder(mainActivity).apply {
                            setTitle("Are you sure?")
                            setPositiveButton("Delete") { _, _ ->
                                if (!model.id.isNullOrEmpty()) {
                                    db.collection(medicines).document(model.id!!)
                                        .update("belongingCategory", FieldValue.arrayRemove(categoryId))
                                        .addOnSuccessListener {
                                            model.belongingCategory?.remove(categoryId) // Update local model
                                            if (model.belongingCategory.isNullOrEmpty()) {
                                                filterList.removeAt(position) // Remove item from list only if array is empty
                                            }
                                            binding.selectedRv.adapter?.notifyItemRemoved(position) // Notify adapter
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("DeleteMedicine", "Error updating: ${e.message}")
                                        }
                                } else {
                                    Log.e("DeleteMedicine", "Invalid medicine ID") // Debugging log
                                }
                            }
                            setNegativeButton("No") { _, _ -> }
                            show() // Show dialog
                        }
                    }
                    medicineCLick.onclick -> {
                        val bundle = Bundle().apply {
                            putString("medicineId", model.id)
                        }
                        findNavController().navigate(R.id.action_filterMedicinesFragment_to_showSingleMedicineFragment, bundle)
                    }
                    medicineCLick.update -> {
                        val bundle = Bundle().apply {
                            putString("medicineId", model.id)
                        }
                        findNavController().navigate(R.id.action_filterMedicinesFragment_to_medicineDetailsFragment, bundle)
                    }
                    else -> { /* Do nothing */ }
                }
            }
        })
        binding.loader.visibility = View.VISIBLE // Show loader while fetching data
        categoryId?.let {
            db.collection(medicines)
                .whereArrayContains("belongingCategory", it)
                .get()
                .addOnSuccessListener { snapshot ->
                    filterList.clear() // Prevent duplication
                    for (document in snapshot) {
                        filterList.add(document.toObject(MedicineModel::class.java))
                    }
                    binding.selectedRv.adapter?.notifyDataSetChanged() // Refresh RecyclerView
                    binding.loader.visibility = View.GONE // Hide loader
                    Toast.makeText(mainActivity, "Data fetched successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    binding.loader.visibility = View.GONE // Hide loader on failure
                    Toast.makeText(mainActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
                }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FilterMedicinesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FilterMedicinesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}