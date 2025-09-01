package com.example.medplusadmin.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medplusadmin.R
import com.example.medplusadmin.presentation.interfaces.ShowCategoryInterface
import com.google.android.material.chip.Chip

class ShowCategoryAdapter(
    private var list: List<Pair<String, String>>,
    private val showCategory: ShowCategoryInterface
) : RecyclerView.Adapter<ShowCategoryAdapter.ViewHolder>() {

    // Keep track of selected IDs
    private val selectedItems = mutableSetOf<String>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val category: Chip = view.findViewById(R.id.categorySelected)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.show_category, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (id, name) = list[position]
        holder.category.text = name

        // ✅ Remove old listener
        holder.category.setOnCheckedChangeListener(null)

        // ✅ Make sure chip is checked if its ID is in selectedItems
        holder.category.isChecked = selectedItems.contains(id)

        // ✅ Add new listener
        holder.category.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(id)
                showCategory.tick(position)
            } else {
                selectedItems.remove(id)
                showCategory.unTick(position)
            }
        }
    }

    fun updateData(newList: List<Pair<String, String>>) {
        list = newList
        notifyDataSetChanged()
    }

    // ✅ Set preselected items and refresh UI
    fun setSelectedItems(ids: List<String>) {
        selectedItems.clear()
        selectedItems.addAll(ids)
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<String> = selectedItems.toList()
}


