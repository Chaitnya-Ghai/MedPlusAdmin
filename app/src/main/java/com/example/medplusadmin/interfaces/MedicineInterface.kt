package com.example.medplusadmin.interfaces

import com.example.medplusadmin.dataClasses.MedicineModel

interface MedicineInterface {
    fun onMedClick(position: Int,model: MedicineModel , onMedicineClickType:medicineCLick?=medicineCLick.default)
}
enum class medicineCLick{
    update , default , delete , onclick
}
