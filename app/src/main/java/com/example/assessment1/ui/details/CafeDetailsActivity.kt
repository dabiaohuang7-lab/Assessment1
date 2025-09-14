package com.example.assessment1.ui.details

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.assessment1.R
import com.example.assessment1.data.CafeRepository
import com.example.assessment1.model.Cafe
import com.example.assessment1.viewmodel.CafesViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Activity for displaying detailed information about a specific cafe.
 * Uses Kotlin Flow to observe and react to favorite status changes.
 * Allows users to view cafe details and toggle favorite status.
 */
class CafeDetailsActivity : AppCompatActivity() {
    private val vm: CafesViewModel by viewModels()
    private lateinit var detailFavouriteBtn: ImageButton
    private var currentCafeId: String? = null

    /**
     * Called when the activity is starting.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cafe_details)

        // Retrieve cafe ID from intent extras
        val cafeId = intent.getStringExtra("cafe_id")
        if (cafeId == null) {
            finish() // Close activity if no cafe ID provided
            return
        }

        currentCafeId = cafeId
        val cafe = CafeRepository.getCafeById(cafeId)

        if (cafe == null) {
            finish() // Close activity if cafe not found
            return
        }

        // Initialize UI with cafe data
        setupViews(cafe)

        // Observe favorite status changes for this cafe
        observeFavouriteStatus(cafeId)
    }

    /**
     * Initializes and populates UI views with cafe data.
     *
     * @param cafe The cafe object to display
     */
    private fun setupViews(cafe: Cafe) {
        val detailImage: ImageView = findViewById(R.id.detailImage)
        val detailTitle: TextView = findViewById(R.id.detailTitle)
        val detailCategory: TextView = findViewById(R.id.detailCategory)
        val detailDescription: TextView = findViewById(R.id.detailDescription)
        detailFavouriteBtn = findViewById(R.id.detailFavouriteBtn)

        // Load cafe image using Glide
        Glide.with(this)
            .load(cafe.imageID)
            .placeholder(android.R.drawable.ic_menu_report_image)
            .error(android.R.drawable.ic_delete)
            .into(detailImage)

        // Set text content
        detailTitle.text = cafe.title
        detailCategory.text = cafe.category
        detailDescription.text = cafe.description

        // Set initial favorite button state
        updateFavouriteButton(cafe.isFavourited)

        // Set click listener for favorite button
        detailFavouriteBtn.setOnClickListener {
            currentCafeId?.let { id ->
                vm.toggleFavourite(id)
            }
        }
    }

    /**
     * Observes favorite status changes for a specific cafe.
     *
     * @param cafeId The ID of the cafe to observe
     */
    private fun observeFavouriteStatus(cafeId: String) {
        lifecycleScope.launch {
            CafeRepository.getCafeFlow(cafeId).collect { cafe ->
                cafe?.let {
                    updateFavouriteButton(it.isFavourited)

                    // Show toast message for the change
                    val message = if (it.isFavourited) {
                        "${it.title} added to favourites"
                    } else {
                        "${it.title} removed from favourites"
                    }
                    Toast.makeText(this@CafeDetailsActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Updates the visual state of the favorite button.
     *
     * @param isFavourited Whether the cafe is currently favorited
     */
    private fun updateFavouriteButton(isFavourited: Boolean) {
        detailFavouriteBtn.setImageResource(
            if (isFavourited) R.drawable.filled_heart else R.drawable.empty_heart
        )
    }
}