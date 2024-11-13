package repository

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.beercellar.model.Beer
import com.example.beercellar.repository.BeerStoreService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class BeersRepository {
    private val baseUrl = "https://anbo-restbeer.azurewebsites.net/api/"

    private val beerStoreService: BeerStoreService
    val beers = mutableStateOf<List<Beer>>(listOf())
    val isLoadingBeers = mutableStateOf(false)
    val errorMessage = mutableStateOf("")
    private var originalBeerList: List<Beer> = listOf()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        beerStoreService = retrofit.create(BeerStoreService::class.java)
        getBeers()
    }

    fun getBeers() {
        isLoadingBeers.value = true
        beerStoreService.getAllBeers().enqueue(object : Callback<List<Beer>> {
            override fun onResponse(call: Call<List<Beer>>, response: Response<List<Beer>>) {
                isLoadingBeers.value = false
                if (response.isSuccessful) {
                    beers.value = response.body() ?: emptyList()
                    errorMessage.value = ""
                } else {
                    errorMessage.value = "${response.code()} ${response.message()}"
                    Log.d("BEER_REPOSITORY", errorMessage.value)
                }
            }

            override fun onFailure(call: Call<List<Beer>>, t: Throwable) {
                isLoadingBeers.value = false
                errorMessage.value = t.message ?: "Unable to connect to the server"
                Log.d("BEER_REPOSITORY", errorMessage.value)
            }
        })
    }

    fun addBeer(beer: Beer) {
        beerStoreService.saveBeer(beer).enqueue(object : Callback<Beer> {
            override fun onResponse(call: Call<Beer>, response: Response<Beer>) {
                if (response.isSuccessful) {
                    getBeers() // Refresh list after adding
                    errorMessage.value = ""
                } else {
                    errorMessage.value = "${response.code()} ${response.message()}"
                    Log.d("BEER_REPOSITORY", errorMessage.value)
                }
            }

            override fun onFailure(call: Call<Beer>, t: Throwable) {
                errorMessage.value = t.message ?: "Unable to connect to the server"
                Log.d("BEER_REPOSITORY", errorMessage.value)
            }
        })
    }

    fun deleteBeer(id: Int) {
        beerStoreService.deleteBeer(id).enqueue(object : Callback<Beer> {
            override fun onResponse(call: Call<Beer>, response: Response<Beer>) {
                if (response.isSuccessful) {
                    getBeers() // Refresh list after deletion
                    errorMessage.value = ""
                } else {
                    errorMessage.value = "${response.code()} ${response.message()}"
                    Log.d("BEER_REPOSITORY", errorMessage.value)
                }
            }

            override fun onFailure(call: Call<Beer>, t: Throwable) {
                errorMessage.value = t.message ?: "Unable to connect to the server"
                Log.d("BEER_REPOSITORY", errorMessage.value)
            }
        })
    }

    fun updateBeer(beerId: Int, beer: Beer) {
        beerStoreService.updateBeer(beerId, beer).enqueue(object : Callback<Beer> {
            override fun onResponse(call: Call<Beer>, response: Response<Beer>) {
                if (response.isSuccessful) {
                    getBeers() // Refresh list after updating
                    errorMessage.value = ""
                } else {
                    errorMessage.value = "${response.code()} ${response.message()}"
                    Log.d("BEER_REPOSITORY", errorMessage.value)
                }
            }

            override fun onFailure(call: Call<Beer>, t: Throwable) {
                errorMessage.value = t.message ?: "Unable to connect to the server"
                Log.d("BEER_REPOSITORY", errorMessage.value)
            }
        })
    }

    fun sortBeersByName(ascending: Boolean) {
        beers.value = if (ascending) {
            beers.value.sortedBy { it.name }
        } else {
            beers.value.sortedByDescending { it.name }
        }
    }

    fun sortBeersByAbv(ascending: Boolean) {
        beers.value = if (ascending) {
            beers.value.sortedBy { it.abv }
        } else {
            beers.value.sortedByDescending { it.abv }
        }
    }

    fun filterBeersByName(nameFragment: String) {
        if (nameFragment.isEmpty()) {
            getBeers()
        } else {
            beers.value = beers.value.filter { it.name.contains(nameFragment, ignoreCase = true) }
        }
    }

    fun getBeersByUsername(username: String) {
        beerStoreService.getBeersByUsername(username).enqueue(object : Callback<List<Beer>> {
            override fun onResponse(call: Call<List<Beer>>, response: Response<List<Beer>>) {
                if (response.isSuccessful) {
                    beers.value = response.body() ?: emptyList()
                    errorMessage.value = ""
                } else {
                    errorMessage.value = "${response.code()} ${response.message()}"
                    Log.d("BEER_REPOSITORY", errorMessage.value)
                }
            }

            override fun onFailure(call: Call<List<Beer>>, t: Throwable) {
                errorMessage.value = t.message ?: "Unable to connect to the server"
                Log.d("BEER_REPOSITORY", errorMessage.value)
            }
        })
    }
}