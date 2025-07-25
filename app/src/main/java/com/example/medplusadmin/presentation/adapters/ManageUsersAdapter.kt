package com.example.medplusadmin.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medplusadmin.databinding.RequestUserLayoutBinding

class ManageUsersAdapter : RecyclerView.Adapter<ManageUsersAdapter.ViewHolder>() {
    class ViewHolder (val binding: RequestUserLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = RequestUserLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding
    }

}
