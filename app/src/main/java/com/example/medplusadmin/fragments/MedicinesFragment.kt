package com.example.medplusadmin.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.medplusadmin.Constants.Companion.medicines
import com.example.medplusadmin.MainActivity
import com.example.medplusadmin.R
import com.example.medplusadmin.adapters.MedicineAdapter
import com.example.medplusadmin.convertMedicineObject
import com.example.medplusadmin.dataClasses.MedicineModel
import com.example.medplusadmin.databinding.FragmentMedicinesBinding
import com.example.medplusadmin.interfaces.MedicineInterface
import com.example.medplusadmin.interfaces.medicineCLick
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.firestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MedicinesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MedicinesFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val binding by lazy { FragmentMedicinesBinding.inflate(layoutInflater) }
    lateinit var mainActivity: MainActivity
    private val medicineList = mutableListOf<MedicineModel>()
    private val db = Firebase.firestore

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
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.MedicinesRv.layoutManager = GridLayoutManager(mainActivity,2)
        binding.MedicinesRv.adapter = MedicineAdapter(mainActivity, medicineList, object : MedicineInterface {
            override fun onMedClick(position: Int, model: MedicineModel, onMedicineClickType: medicineCLick?) {
                when (onMedicineClickType) {
                    medicineCLick.delete -> {
                        AlertDialog.Builder(mainActivity).apply {
                            setTitle("Are you sure?")
                            setPositiveButton("Delete") { _, _ ->
                                db.collection(medicines).document(model.medId!!).delete()
                                    .addOnSuccessListener {
                                        medicineList.remove(model)
                                        binding.MedicinesRv.adapter?.notifyDataSetChanged() // Force update UI
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("DeleteMedicine", "Error deleting: ${e.message}")
                                    }
                            }

                            setNegativeButton("No") { _, _ -> }
                            show() // Show dialog
                        }
                    }
                    medicineCLick.onclick->{
                        val bundle=Bundle()
                        bundle.putString("medicineId", medicineList[position].medId!!)
                        findNavController().navigate(R.id.action_medicinesFragment_to_showSingleMedicineFragment,bundle)
                    }
                    medicineCLick.update->{
                        val bundle=Bundle()
                        bundle.putString("medicineId", medicineList[position].medId!!)
                        findNavController().navigate(R.id.action_medicinesFragment_to_medicineDetailsFragment ,bundle)}
                    else->{/*NOTHING*/}
                }
            }
        })
        binding.loader.visibility = View.VISIBLE // Show loader while fetching data
        db.collection(medicines).addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.e("Firestore", "Error fetching data: ${e.message}")
                return@addSnapshotListener
            }
            for (snapshot in snapshots!!.documentChanges) {
                val model = convertMedicineObject(snapshot.document)
                when (snapshot.type) {
                    DocumentChange.Type.ADDED -> {
                        if (!medicineList.any { it.medId == model.medId }) { // ✅ Prevents duplicates
                            medicineList.add(model)
                        }
                    }
                    DocumentChange.Type.MODIFIED -> {
                        val index = getIndex(model)
                        if (index != -1) {
                            medicineList[index] = model
                        }
                    }
                    DocumentChange.Type.REMOVED -> {
                        val index = getIndex(model)
                        if (index != -1) {
                            medicineList.removeAt(index) // ✅ Removes only the deleted item
                        }
                    }
                }
            }
            binding.MedicinesRv.adapter?.notifyDataSetChanged() // Updates only necessary changes
            binding.loader.visibility = View.GONE
        }
        binding.addMedicine.setOnClickListener {
            findNavController().navigate(R.id.action_medicinesFragment_to_medicineDetailsFragment)
        }
    }
    private fun getIndex(model: MedicineModel): Int {
        return medicineList.indexOfFirst { it.medId == model.medId }
    }
}
