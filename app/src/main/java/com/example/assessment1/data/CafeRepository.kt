package com.example.assessment1.data

import com.example.assessment1.R
import com.example.assessment1.model.Cafe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

/**
 * Repository pattern implementation for managing cafe data.
 * Uses Kotlin Flow for reactive data streaming and state management.
 * Acts as a single source of truth for cafe information and favorite status.
 */
object CafeRepository {
    // Predefined list of cafes with sample data
    private val cafes = listOf(
        Cafe("1", "Guilty Pleasure", "A Korean dessert cafe that offers a wide range of beverages, cube toasts, croffles and cakes", "Northbridge", R.drawable.guiltypleasure),
        Cafe("2", "Camellia Cafe", "A dessert cafe with a variety of different drinks and specialise in honey bread, croffles and bingsu", "Northbridge", R.drawable.camellia_cafe),
        Cafe("3", "Tiara Cafe", "A cute cafe hidden in Northbridge that specialises in honey bread, croffles, cakes and bingsu", "Northbridge", R.drawable.tiara_cafe),
        Cafe("4", "20Twenty", "A cafe that specialises in all things cake and coffee!", "Northbridge", R.drawable.twentytwenty),
        Cafe("5", "Brown Spoon", "Perth's FIRST Korean dessert cafe has everything from pancakes and bagels to bingsu and honey toast bread.", "Victoria Park", R.drawable.brown_spoon),
        Cafe("6", "Matcha Garden", "A perfect spot for matcha lovers!", "Victoria Park", R.drawable.matcha_garden),
        Cafe("7", "Cafe Eighty2", "A Korean fusion cafe that serves lunch options alongside breakfast and dessert ", "Victoria Park", R.drawable.cafe_eighty2),
        Cafe("8", "Hinata Cafe", "An authentic Japanese cafe that offers traditional home-style cooking, with seasonal menus ", "Fremantle", R.drawable.hinata_cafe),
        Cafe("9", "Moore & Moore", "A charming cafe in an old merchant's warehouse that not only is a cafe but a popular event venue!", "Fremantle", R.drawable.moore_and_moore),
        Cafe("10", "Cheol's Cafe", "A cozy, friendly cafe that reimagines the breakfast scene with Asian fusions", "Mount Lawley", R.drawable.cheols_cafe),
    )

    // Mutable StateFlow to track favorite cafe IDs
    private val _favourites = MutableStateFlow<Set<String>>(emptySet())

    // Public StateFlow for observing favorite changes
    val favourites: StateFlow<Set<String>> = _favourites

    /**
     * Retrieves all cafes from the repository with current favorite status.
     * This is a synchronous method for use cases where immediate data is needed.
     *
     * @return List of Cafe objects with current favorite status
     */
    fun getCafes(): List<Cafe> {
        val currentFavourites = _favourites.value
        return cafes.map { cafe ->
            cafe.copy(isFavourited = currentFavourites.contains(cafe.id))
        }
    }

    /**
     * Retrieves all cafes from the repository with current favorite status as a Flow.
     *
     * @return Flow emitting List of Cafe objects with updated favorite status
     */
    fun getCafesFlow(): kotlinx.coroutines.flow.Flow<List<Cafe>> {
        return _favourites.map { favourites ->
            cafes.map { cafe ->
                cafe.copy(isFavourited = favourites.contains(cafe.id))
            }
        }
    }

    /**
     * Finds a cafe by its unique identifier with current favorite status.
     *
     * @param id The ID of the cafe to find
     * @return Cafe object if found, null otherwise
     */
    fun getCafeById(id: String): Cafe? {
        val currentFavourites = _favourites.value
        return cafes.find { it.id == id }?.copy(
            isFavourited = currentFavourites.contains(id)
        )
    }

    /**
     * Gets a Flow for a specific cafe that updates when its favorite status changes.
     *
     * @param id The ID of the cafe to observe
     * @return Flow emitting the Cafe object with updated favorite status
     */
    fun getCafeFlow(id: String): kotlinx.coroutines.flow.Flow<Cafe?> {
        return _favourites.map { favourites ->
            cafes.find { it.id == id }?.copy(
                isFavourited = favourites.contains(id)
            )
        }
    }

    /**
     * Filters cafes by category with current favorite status.
     *
     * @param category The category to filter by
     * @return List of cafes matching the category
     */
    fun getCafesByCategory(category: String): List<Cafe> {
        val currentFavourites = _favourites.value
        return cafes.filter { it.category.equals(category, ignoreCase = true) }
            .map { cafe ->
                cafe.copy(isFavourited = currentFavourites.contains(cafe.id))
            }
    }

    /**
     * Searches cafes by title or description with current favorite status.
     *
     * @param query The search query
     * @return List of cafes matching the search query
     */
    fun searchCafes(query: String): List<Cafe> {
        val currentFavourites = _favourites.value
        return cafes.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }.map { cafe ->
            cafe.copy(isFavourited = currentFavourites.contains(cafe.id))
        }
    }

    /**
     * Toggles the favorite status of a cafe.
     *
     * @param cafeId The ID of the cafe to toggle favorite status for
     * @return The new favorite status (true if now favorited, false otherwise)
     */
    fun toggleFavourite(cafeId: String): Boolean {
        val newFavourites = _favourites.value.toMutableSet()
        val isNowFavourited = if (newFavourites.contains(cafeId)) {
            newFavourites.remove(cafeId)
            false
        } else {
            newFavourites.add(cafeId)
            true
        }

        // Update the StateFlow with the new favorites set
        _favourites.value = newFavourites

        return isNowFavourited
    }
}