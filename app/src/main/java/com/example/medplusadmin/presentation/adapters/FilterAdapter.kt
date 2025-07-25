package com.example.medplusadmin.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.medplusadmin.R
import com.example.medplusadmin.domain.models.Medicine
import com.example.medplusadmin.presentation.interfaces.AllMedicineInterface

class FilterAdapter(context: Context, var list : MutableList<Medicine>, var medInterface: AllMedicineInterface) :RecyclerView.Adapter<FilterAdapter.ViewHolder>() {
    class ViewHolder( view: View) : RecyclerView.ViewHolder(view){
        val name=view.findViewById<TextView>(R.id.tvName)
        val updateBtn=view.findViewById<ImageButton>(R.id.updateBtn)
        val deleteBtn=view.findViewById<ImageButton>(R.id.deleteBtn)
        val img=view.findViewById<ImageView>(R.id.imgV)
        val card = view.findViewById<CardView>(R.id.recycler_layout_xml)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 0
    }

}