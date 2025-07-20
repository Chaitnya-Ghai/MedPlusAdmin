package com.example.medplusadmin.Presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medplusadmin.R
import com.example.medplusadmin.domain.models.Medicine
import com.example.medplusadmin.Presentation.interfaces.MedicineInterface
import com.example.medplusadmin.Presentation.interfaces.medicineCLick

data class MedicineAdapter(var context: Context, var medicineList: MutableList<Medicine>, var medicineInterface : MedicineInterface):RecyclerView.Adapter<MedicineAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val price = view.findViewById<TextView>(R.id.price)
        val name = view.findViewById<TextView>(R.id.name)
        val deleteBtn = view.findViewById<ImageView>(R.id.delete)
        val editBtn = view.findViewById<ImageView>(R.id.edit)
        val img = view.findViewById<ImageView>(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.med_list,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return medicineList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(medicineList[position].medicineImg)
            .into(holder.img)
        holder.name.text = medicineList[position].medicineName
        holder.price.text = medicineList[position].productDetail?.originalPrice
        holder.editBtn.setOnClickListener { medicineInterface.onMedClick(position,medicineList[position],medicineCLick.update) }
        holder.deleteBtn.setOnClickListener { medicineInterface.onMedClick(position,medicineList[position],medicineCLick.delete) }
        holder.img.setOnClickListener { medicineInterface.onMedClick(position,medicineList[position],medicineCLick.onclick) }
    }
}
