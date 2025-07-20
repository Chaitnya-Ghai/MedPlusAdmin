package com.example.medplusadmin.Presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medplusadmin.R
import com.example.medplusadmin.Presentation.interfaces.ShowCategoryInterface
import com.google.android.material.checkbox.MaterialCheckBox

class ShowCategoryAdapter(
    private var list: List<Pair<String, String>>,
    private val showCategory: ShowCategoryInterface
):RecyclerView.Adapter<ShowCategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val category = view.findViewById<MaterialCheckBox>(R.id.categorySelected)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.show_category, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         if (list.isNotEmpty()){
             holder.category.text = list[position].second
             holder.category.setOnCheckedChangeListener { _, isChecked ->
                 when (isChecked) {
                     true -> {
                         showCategory.tick(position)
                     }
                     false -> {
                         showCategory.unTick(position)
                    /*call function who removes its data from list*/
                     }
                 }
             }
        }
    }
    fun updateData(newList: List<Pair<String, String>>) {
        list = newList
        notifyDataSetChanged()
    }
}