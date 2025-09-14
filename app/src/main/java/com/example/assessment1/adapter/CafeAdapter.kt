package com.example.assessment1.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.assessment1.R
import com.example.assessment1.model.Cafe
import com.example.assessment1.ui.details.CafeDetailsActivity
import com.example.assessment1.viewmodel.CafesViewModel

/**
 * Adapter for displaying a list of cafes in a RecyclerView.
 * Implements efficient view recycling and uses Glide for image loading.
 *
 * @param context The context in which the adapter is being used
 * @param vm The ViewModel containing cafe data and state
 */
class CafeAdapter(
    private val context: Context,
    private val vm: CafesViewModel
) : RecyclerView.Adapter<CafeAdapter.CafeViewHolder>() {

    // Current list of cafes to display
    private var cafes: List<Cafe> = emptyList()

    /**
     * ViewHolder class for caching view references.
     *
     * @param view The item view for a cafe
     */
    class CafeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.cafeTitle)
        val category: TextView = view.findViewById(R.id.cafeCategory)
        val image: ImageView = view.findViewById(R.id.cafeImage)
        val favouriteBtn: ImageButton = view.findViewById(R.id.favouriteBtn)
    }

    /**
     * Called when RecyclerView needs a new ViewHolder.
     *
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new ViewHolder that holds a View of the given view type
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cafe, parent, false)
        return CafeViewHolder(view)
    }

    /**
     * Called by RecyclerView to display data at a specified position.
     *
     * @param holder The ViewHolder which should be updated
     * @param position The position of the item within the adapter's data set
     */
    override fun onBindViewHolder(holder: CafeViewHolder, position: Int) {
        val cafe = cafes[position]

        // Set text content
        holder.title.text = cafe.title
        holder.category.text = cafe.category

        // Load image using Glide with placeholder and error handling
        Glide.with(context)
            .load(cafe.imageID)
            .placeholder(android.R.drawable.ic_menu_report_image)
            .error(android.R.drawable.ic_delete)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(holder.image)

        // Update favorite button state
        updateFavouriteButton(holder.favouriteBtn, cafe.isFavourited)

        // Set click listener for favorite button
        holder.favouriteBtn.setOnClickListener {
            vm.toggleFavourite(cafe.id)
        }

        // Set click listener for entire item to navigate to details
        holder.itemView.setOnClickListener {
            val intent = Intent(context, CafeDetailsActivity::class.java)
            intent.putExtra("cafe_id", cafe.id)
            context.startActivity(intent)
        }
    }

    /**
     * Returns the total number of items in the data set.
     *
     * @return The total number of items
     */
    override fun getItemCount(): Int = cafes.size

    /**
     * Updates the adapter with a new list of cafes.
     *
     * @param newCafes The new list of cafes to display
     */
    fun updateList(newCafes: List<Cafe>) {
        cafes = newCafes
        notifyDataSetChanged()
    }

    /**
     * Updates the visual state of the favorite button.
     *
     * @param button The ImageButton to update
     * @param isFavourited Whether the cafe is currently favorited
     */
    private fun updateFavouriteButton(button: ImageButton, isFavourited: Boolean) {
        button.setImageResource(if (isFavourited) R.drawable.filled_heart else R.drawable.empty_heart)
    }
}