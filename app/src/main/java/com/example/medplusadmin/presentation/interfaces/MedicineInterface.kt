package com.example.medplusadmin.presentation.interfaces

import com.example.medplusadmin.domain.models.Medicine

interface MedicineInterface {
    fun onMedClick(position: Int, model: Medicine, onMedicineClickType:medicineCLick?=medicineCLick.default)
}
enum class medicineCLick{
    update , default , delete , onclick
}
