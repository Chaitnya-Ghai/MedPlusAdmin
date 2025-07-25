package com.example.medplusadmin.presentation.interfaces

import com.example.medplusadmin.domain.models.Medicine

interface AllMedicineInterface {
    fun onClick(position: Int, model: Medicine, clickType:onclickType ?= onclickType.default)
}
enum class onclickType{
    default , delete , update
}