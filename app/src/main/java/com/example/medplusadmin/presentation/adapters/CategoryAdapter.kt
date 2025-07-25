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
import com.bumptech.glide.Glide
import com.example.medplusadmin.R
import com.example.medplusadmin.domain.models.Category
import com.example.medplusadmin.presentation.interfaces.CategoryInterface
import com.example.medplusadmin.presentation.interfaces.ClickType

class CategoryAdapter(var context: Context, var list: MutableList<Category>, var categoryInterface: CategoryInterface ):RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){
    class ViewHolder( view: View) : RecyclerView.ViewHolder(view){
        val name=view.findViewById<TextView>(R.id.tvName)
        val updateBtn=view.findViewById<ImageButton>(R.id.updateBtn)
        val deleteBtn=view.findViewById<ImageButton>(R.id.deleteBtn)
        val img=view.findViewById<ImageView>(R.id.imgV)
        val card = view.findViewById<CardView>(R.id.recycler_layout_xml)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.item_list,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text=list[position].categoryName
        Glide.with(context)
            .load(list[position].imageUrl)
            .centerCrop()
            .into(holder.img)
        holder.card.setOnClickListener { categoryInterface.onClick(position,list[position],ClickType.onClick) }
        holder.updateBtn.setOnClickListener { categoryInterface.onClick(position,list[position],ClickType.update) }
        holder.deleteBtn.setOnClickListener { categoryInterface.onClick(position,list[position],ClickType.delete) }
    }

}
