package com.example.assessment1.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing a Cafe entity.
 * Implements Parcelable for efficient serialization between components.
 *
 * @property id Unique identifier for the cafe
 * @property title Name of the cafe
 * @property description Detailed description of the cafe
 * @property category Location category of the cafe
 * @property imageID Resource ID of the cafe's image
 * @property isFavourited Flag indicating if the cafe is favorited by the user
 */
@Parcelize
data class Cafe(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val imageID: Int,
    var isFavourited: Boolean = false
) : Parcelable