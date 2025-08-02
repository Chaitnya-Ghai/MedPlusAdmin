package com.example.medplusadmin.data.mappers

import com.example.medplusadmin.data.remote.dto.CategoryDto
import com.example.medplusadmin.data.remote.dto.MedicineDto
import com.example.medplusadmin.domain.models.Category
import com.example.medplusadmin.domain.models.Medicine
import com.example.medplusadmin.domain.models.ProductDetail

fun CategoryDto.toCategory(): Category {
    return Category(
        id = id ?:"",
        categoryName = categoryName?:"",
        imageUrl = imageUrl?:""
    )
}

fun Category.toDto(): CategoryDto = CategoryDto(
    id = this.id,
    categoryName = this.categoryName,
    imageUrl = this.imageUrl
)

fun MedicineDto.toMedicine(): Medicine {
    return Medicine(
        medId = this.id.orEmpty(),
        medicineName = this.medicineName.orEmpty(),
        description = this.description.orEmpty(),
        medicineImg = this.medicineImg.orEmpty(),
        belongingCategory = this.belongingCategory ?: mutableListOf(),
        dosageForm = this.dosageForm.orEmpty(),
        unit = this.unit.orEmpty(),
        ingredients = this.ingredients.orEmpty(),
        howToUse = this.howToUse.orEmpty(),
        precautions = this.precautions.orEmpty(),
        storageInfo = this.storageInfo.orEmpty(),
        sideEffects = this.sideEffects.orEmpty(),
        productDetail = this.productDetail ?: ProductDetail()
    )
}

fun Medicine.toDto(): MedicineDto {
    return MedicineDto(
        id = this.medId,
        medicineName = this.medicineName,
        description = this.description,
        medicineImg = this.medicineImg,
        belongingCategory = this.belongingCategory,
        dosageForm = this.dosageForm,
        unit = this.unit,
        ingredients = this.ingredients,
        howToUse = this.howToUse,
        precautions = this.precautions,
        storageInfo = this.storageInfo,
        sideEffects = this.sideEffects,
        productDetail = this.productDetail
    )
}



