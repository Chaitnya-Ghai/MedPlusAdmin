package com.example.medplusadmin.Presentation.interfaces

import com.example.medplusadmin.domain.models.Category

interface CategoryInterface {
    fun onClick(position: Int, model: Category, onClickType: ClickType?=ClickType.nothing)
}
enum class ClickType{
    update , delete , nothing , onClick
}