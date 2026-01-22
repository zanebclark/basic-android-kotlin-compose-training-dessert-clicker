package com.example.dessertclicker.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dessertclicker.data.Datasource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UiState(
    val totalRevenue: Int = 0, val quantitySold: Int = 0, val currentDessert: Dessert
)

class DessertViewModel(
    private val desserts: List<Dessert>
) : ViewModel() {
    private lateinit var _uiState: MutableStateFlow<UiState>
    val uiState: StateFlow<UiState>
        get() = _uiState.asStateFlow()

    /**
     * Determine which dessert to show.
     */
    fun getNewDessert(quantitySold: Int): Dessert {
        var newDessertIndex = 0
        desserts.forEachIndexed { index, dessert ->
            if (quantitySold >= dessert.startProductionAmount) {
                newDessertIndex = index
            }
        }
        return desserts[newDessertIndex]
    }

    fun onDessertClicked() {
        _uiState.update { currentState ->
            val totalRevenue = currentState.totalRevenue + currentState.currentDessert.price
            val quantitySold = currentState.quantitySold + 1
            val dessert = getNewDessert(quantitySold)
            currentState.copy(
                totalRevenue = totalRevenue,
                quantitySold = quantitySold,
                currentDessert = dessert
            )
        }
    }

    init {
        require(desserts.isNotEmpty()) {
            "Dessert list cannot be empty"
        }
        _uiState = MutableStateFlow(
            UiState(currentDessert = desserts.first())
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DessertViewModel(desserts = Datasource.dessertList)
            }
        }
    }
}
