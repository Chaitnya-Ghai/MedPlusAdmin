package com.example.medplusadmin.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medplusadmin.MainActivity
import com.example.medplusadmin.R
import com.example.medplusadmin.adapters.MedicineAdapter
import com.example.medplusadmin.dataClasses.MedicineModel
import com.example.medplusadmin.databinding.FragmentMedicinesBinding
import com.example.medplusadmin.interfaces.MedicineInterface
import com.example.medplusadmin.interfaces.medicineCLick

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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val binding by lazy { FragmentMedicinesBinding.inflate(layoutInflater) }
    lateinit var mainActivity: MainActivity
    var allMedicines= mutableListOf<MedicineModel>()
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
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.MedicinesRv.layoutManager=LinearLayoutManager(mainActivity)
        binding.MedicinesRv.adapter = MedicineAdapter(mainActivity,allMedicines,object :
            MedicineInterface {
            override fun onMedClick(
                position: Int,
                model: MedicineModel,
                onMedicineClickType: medicineCLick?
            ) {
                when(onMedicineClickType){
                    medicineCLick.delete->{}
                    medicineCLick.onclick->{}
                    medicineCLick.update->{}
                    else->{/*NOTHING*/}
                }
            }
        })
        binding.addMedicine.setOnClickListener {
            findNavController().navigate(R.id.action_medicinesFragment_to_medicineDetailsFragment)
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MedicinesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MedicinesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}