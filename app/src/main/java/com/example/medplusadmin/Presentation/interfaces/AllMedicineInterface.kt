package com.example.medplusadmin.Presentation.interfaces

import com.example.medplusadmin.domain.models.Medicine

interface AllMedicineInterface {
    fun onClick(position: Int, model: Medicine, clickType:onclickType ?= onclickType.default)
}
enum class onclickType{
    default , delete , update
}