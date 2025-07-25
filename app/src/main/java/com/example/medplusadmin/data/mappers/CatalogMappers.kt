package com.example.medplusadmin.data.mappers

import com.example.medplusadmin.data.remote.dto.CategoryDto
import com.example.medplusadmin.data.remote.dto.MedicineDto
import com.example.medplusadmin.domain.models.Category
import com.example.medplusadmin.domain.models.Medicine

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
    return Medicine()
}