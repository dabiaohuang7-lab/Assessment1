package com.example.assessment1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.assessment1.data.CafeRepository
import com.example.assessment1.model.Cafe
import kotlinx.coroutines.launch

/**
 * ViewModel for managing cafe data and UI state.
 * Uses Kotlin Flow for reactive data streaming and automatic UI updates.
 * Follows Android Architecture Components guidelines to survive configuration changes.
 */
class CafesViewModel : ViewModel() {
    // LiveData for the filtered list of cafes (based on search/filter)
    private val _filteredCafes = MutableLiveData<List<Cafe>>()
    val filteredCafes: LiveData<List<Cafe>> get() = _filteredCafes

    // Flow for the complete list of cafes, converted to LiveData for UI observation
    val cafes: LiveData<List<Cafe>> = CafeRepository.getCafesFlow().asLiveData()

    /**
     * Initializes the ViewModel by loading initial cafe data.
     */
    init {
        // Initialize with all cafes
        viewModelScope.launch {
            CafeRepository.getCafesFlow().collect { cafeList ->
                _filteredCafes.value = cafeList
            }
        }
    }

    /**
     * Toggles the favorite status of a cafe.
     *
     * @param cafeId The ID of the cafe to toggle favorite status for
     */
    fun toggleFavourite(cafeId: String) {
        viewModelScope.launch {
            CafeRepository.toggleFavourite(cafeId)
            // UI will automatically update through Flow observation
        }
    }

    /**
     * Filters cafes by category.
     *
     * @param category The category to filter by, or "All" to show all cafes
     */
    fun filterByCategory(category: String) {
        viewModelScope.launch {
            if (category == "All") {
                // Get all cafes from Flow and update filtered list
                CafeRepository.getCafesFlow().collect { cafeList ->
                    _filteredCafes.value = cafeList
                }
            } else {
                // Use repository's category filter
                _filteredCafes.value = CafeRepository.getCafesByCategory(category)
            }
        }
    }

    /**
     * Searches cafes by query string.
     *
     * @param query The search query to filter cafes by title or description
     */
    fun searchCafes(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                // Get all cafes from Flow when query is empty
                CafeRepository.getCafesFlow().collect { cafeList ->
                    _filteredCafes.value = cafeList
                }
            } else {
                // Use repository's search function
                _filteredCafes.value = CafeRepository.searchCafes(query)
            }
        }
    }
}