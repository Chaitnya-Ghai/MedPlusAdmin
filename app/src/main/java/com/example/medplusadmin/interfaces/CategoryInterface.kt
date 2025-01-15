package com.example.medplusadmin.interfaces

import com.example.medplusadmin.dataClasses.CategoryModel

interface CategoryInterface {
    fun onClick(position: Int, model: CategoryModel , onClickType: ClickType?=ClickType.nothing)
}
enum class ClickType{
    update , delete , nothing
}