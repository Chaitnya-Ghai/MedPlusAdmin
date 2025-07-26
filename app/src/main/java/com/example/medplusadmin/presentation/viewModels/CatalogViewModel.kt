package com.example.medplusadmin.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medplusadmin.domain.models.Category
import com.example.medplusadmin.domain.usecases.catalog.DeleteCategoryUseCase
import com.example.medplusadmin.domain.usecases.catalog.GetAllCategoriesUseCase
import com.example.medplusadmin.domain.usecases.catalog.UpsertCategoriesUseCase
import com.example.medplusadmin.utils.CatalogUIEvent
import com.example.medplusadmin.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val upsertCategoriesUseCase: UpsertCategoriesUseCase
) : ViewModel() {

    // ------------------- CATEGORY -------------------

    val categories = getAllCategoriesUseCase().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Resource.Loading())
    /* Searching*/
    private val _searchQueryCategory = MutableStateFlow("")//used flow for better debounce operator usage
    fun updateSearchQuery(query: String) { _searchQueryCategory.value = query }
    val filteredCategories = combine(_searchQueryCategory.debounce(300L), categories) { query, result ->
        // Initially set loading
        if (result is Resource.Loading) {
            Resource.Loading()
        } else if (result is Resource.Error) {
            Resource.Error(result.message.toString())
        } else if (result is Resource.Success) {
            val list = result.data ?: emptyList()
            val filtered = if (query.isBlank()) list else list.filter {
                it.categoryName.contains(query, ignoreCase = true) ||
                        it.id.contains(query, ignoreCase = true)
            }
            Resource.Success(filtered)
        } else {
            Resource.Success(emptyList()) // default fallback
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Resource.Loading())
    /* Upsert -> Add or Update*/
    private val _upsertCategoryState = MutableStateFlow<Resource<Boolean>>(Resource.Loading())
    val upsertCategoryState: StateFlow<Resource<Boolean>> = _upsertCategoryState

    fun upsertCategories(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            _upsertCategoryState.emit(Resource.Loading())
            val result = upsertCategoriesUseCase.invoke(category)
            _upsertCategoryState.emit(result)
        }
    }

    suspend fun deleteCategory(id: String): Resource<Boolean> {
        return deleteCategoryUseCase.invoke(id)
    }

    // ------------------- MEDICINE -------------------

    // Placeholder: Add medicine-related flows and functions when needed

    // ------------------- SUPABASE IMAGE UPLOAD -------------------

    // private val _uploadState = MutableStateFlow<Resource<String>>(Resource.Loading())
    // val uploadState: StateFlow<Resource<String>> = _uploadState

    // ------------------- UI EVENTS :- SHARED FLOW  --------------------

    private val _uiEvent = MutableSharedFlow<CatalogUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onCategoryImageClick() {
        viewModelScope.launch {
            _uiEvent.emit(CatalogUIEvent.OpenCategoryImagePicker)
        }
    }

    fun onMedicineImageClick() {
        viewModelScope.launch {
            _uiEvent.emit(CatalogUIEvent.OpenMedicineImagePicker)
        }
    }
}


