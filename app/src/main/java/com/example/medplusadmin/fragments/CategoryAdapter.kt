package com.example.medplusadmin.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medplusadmin.R
import com.example.medplusadmin.dataClasses.CategoryModel
import com.example.medplusadmin.interfaces.CategoryInterface
import com.example.medplusadmin.interfaces.ClickType

class CategoryAdapter(var context: Context, var list: MutableList<CategoryModel>, var categoryInterface: CategoryInterface ):RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){
    class ViewHolder( view: View) : RecyclerView.ViewHolder(view){
        val name=view.findViewById<TextView>(R.id.tvName)
        val updateBtn=view.findViewById<ImageButton>(R.id.updateBtn)
        val deleteBtn=view.findViewById<ImageButton>(R.id.deleteBtn)
        val img=view.findViewById<ImageView>(R.id.imgV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.item_category,parent,false)
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
        holder.updateBtn.setOnClickListener { categoryInterface.onClick(position,list[position],ClickType.update) }
        holder.deleteBtn.setOnClickListener { categoryInterface.onClick(position,list[position],ClickType.delete) }
    }

}
