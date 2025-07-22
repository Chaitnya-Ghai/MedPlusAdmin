package com.example.medplusadmin.presentation.interfaces

import com.example.medplusadmin.domain.models.Category

interface CategoryInterface {
    fun onClick(position: Int, model: Category, onClickType: ClickType?=ClickType.nothing)
}
enum class ClickType{
    update , delete , nothing , onClick
}