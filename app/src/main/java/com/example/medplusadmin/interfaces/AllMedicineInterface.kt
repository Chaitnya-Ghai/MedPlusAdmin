package com.example.medplusadmin.interfaces

import com.example.medplusadmin.dataClasses.MedicineModel

interface AllMedicineInterface {
    fun onClick(position: Int , model: MedicineModel , clickType:onclickType ?= onclickType.default)
}
enum class onclickType{
    default , delete , update
}