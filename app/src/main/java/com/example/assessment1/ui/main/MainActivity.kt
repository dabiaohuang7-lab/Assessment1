package com.example.assessment1.ui.main

import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assessment1.R
import com.example.assessment1.adapter.CafeAdapter
import com.example.assessment1.data.CafeRepository
import com.example.assessment1.viewmodel.CafesViewModel

/**
 * Main activity displaying a list of cafes with search and filter functionality.
 * Implements adaptive UI for different screen sizes and orientations.
 */
class MainActivity : AppCompatActivity() {

    // Initialize ViewModel using property delegation
    private val vm: CafesViewModel by viewModels()
    private lateinit var adapter: CafeAdapter

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     * being shut down then this Bundle contains the data it most recently supplied
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        setupRecyclerView()
        setupSearchView()
        setupCategoryFilter()
        setupObservers()
    }

    /**
     * Sets up the RecyclerView with a GridLayoutManager and adapter.
     */
    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = CafeAdapter(this, vm)

        // Use GridLayoutManager with 2 columns
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        // Optimize performance when item views are of fixed size
        recyclerView.setHasFixedSize(true)
    }

    /**
     * Configures the SearchView with appropriate styling and functionality.
     */
    private fun setupSearchView() {
        val searchView: SearchView = findViewById(R.id.searchView)

        // Customize search text appearance
        val searchEditText = searchView.findViewById<EditText>(
            androidx.appcompat.R.id.search_src_text
        )
        searchEditText.setTextColor(Color.BLACK)
        searchEditText.setHintTextColor(Color.GRAY)

        // Set up search query listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                vm.searchCafes(newText)
                return true
            }
        })
    }

    /**
     * Sets up the category filter spinner with available categories.
     */
    private fun setupCategoryFilter() {
        val categoryFilter = findViewById<android.widget.Spinner>(R.id.categoryFilter)

        // Get all unique categories from the repository
        val categories = listOf("All") + CafeRepository.getCafes()
            .map { it.category }
            .distinct()

        // Create adapter for spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoryFilter.adapter = adapter

        // Set up category selection listener
        categoryFilter.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                vm.filterByCategory(selectedCategory)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                // No action needed when nothing is selected
            }
        }
    }

    /**
     * Sets up LiveData observers to update UI when data changes.
     */
    private fun setupObservers() {
        // Observe filtered cafe list and update adapter when it changes
        vm.filteredCafes.observe(this) { cafes ->
            adapter.updateList(cafes)
        }
    }
}