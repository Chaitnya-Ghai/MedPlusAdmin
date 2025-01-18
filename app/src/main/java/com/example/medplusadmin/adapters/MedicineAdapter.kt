package com.example.medplusadmin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medplusadmin.R
import com.example.medplusadmin.dataClasses.MedicineModel
import com.example.medplusadmin.interfaces.MedicineInterface

data class MedicineAdapter(var context: Context, var list: MutableList<MedicineModel>, var medicineInterface : MedicineInterface):RecyclerView.Adapter<MedicineAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.med_list,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder
    }
}
