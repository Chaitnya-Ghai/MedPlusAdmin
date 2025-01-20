package com.example.medplusadmin.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.medplusadmin.Constants.Companion.medicines
import com.example.medplusadmin.MainActivity
import com.example.medplusadmin.MyApplication
import com.example.medplusadmin.adapters.ShowCategoryAdapter
import com.example.medplusadmin.dataClasses.MedicineModel
import com.example.medplusadmin.databinding.FragmentMedicineDetailsBinding
import com.example.medplusadmin.interfaces.ShowCategoryInterface
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import io.github.jan.supabase.SupabaseClient

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
    var selectedCategoryIdsList = mutableListOf<Pair<String,String>>()
    var showCategoryAdapter:ShowCategoryAdapter?=null

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
        val categoryArrayPairs = mainActivity.categoryArrayPair
        showCategoryAdapter = ShowCategoryAdapter(categoryArrayPairs, object : ShowCategoryInterface {
            override fun tick(position: Int) {
                categoryArrayPairs[position].let { selectedCategoryIdsList.add(it) }
                Toast.makeText(mainActivity, "added", Toast.LENGTH_SHORT).show()
            }

            override fun unTick(position: Int) {
                categoryArrayPairs[position].let { selectedCategoryIdsList.remove(it) }
                Toast.makeText(mainActivity, "removed", Toast.LENGTH_SHORT).show()
            }
        })
        binding.categoriesSelected.layoutManager = GridLayoutManager(mainActivity,3)
        binding.categoriesSelected.adapter= showCategoryAdapter
        binding.saveMed.setOnClickListener {
            val medicineModel = MedicineModel()
            medicineModel.medicineName = binding.etMedName.text.toString()
            medicineModel.productDetail?.originalPrice = binding.etPrice.text.toString()
            medicineModel.description = binding.etDescription.text.toString()
            medicineModel.ingredients = binding.etIngredients.text.toString()
            medicineModel.productDetail?.originalPrice = binding.etUnits.text.toString()
            medicineModel.productDetail?.expiryDate = binding.etExpiryDate.text.toString()
            medicineModel.howToUse = binding.etHowToUse.text.toString()
            medicineModel.precautions = binding.etPrecautions.text.toString()
            medicineModel.storageInfo = binding.etStorage.text.toString()
            medicineModel.sideEffects = binding.etSideEffect.text.toString()
            medicineModel.belongingCategory=selectedCategoryIdsList.map { it.first }.toMutableList()
            db.collection(medicines).add(medicineModel)
            findNavController().popBackStack()
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