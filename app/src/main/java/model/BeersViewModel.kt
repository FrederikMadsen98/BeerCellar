package com.example.beercellar.model

import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import repository.BeersRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BeersViewModel : ViewModel() {
    private val repository = BeersRepository()
    val beers: State<List<Beer>> = repository.beers
    val errorMessage: State<String> = repository.errorMessage
    val isLoadingBeers: State<Boolean> = repository.isLoadingBeers

    fun getBeers() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getBeers()
        }
    }

    fun add(beer: Beer) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addBeer(beer)
        }
    }

    fun update(beerId: Int, beer: Beer) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateBeer(beerId, beer)
        }
    }

    fun remove(beer: Beer) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteBeer(beer.id)
        }
    }

    fun sortBeersByName(ascending: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sortBeersByName(ascending)
        }
    }

    fun sortBeersByAbv(ascending: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sortBeersByAbv(ascending)
        }
    }

    fun filterByName(nameFragment: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.filterBeersByName(nameFragment)
        }
    }

    fun getBeersByUsername(username: String) {
        repository.getBeersByUsername(username)
    }

    fun clearBeers() {
        repository.beers.value = emptyList()
    }
}

