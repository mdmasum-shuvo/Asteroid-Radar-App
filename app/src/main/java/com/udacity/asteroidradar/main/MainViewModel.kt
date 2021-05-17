package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.AsteroidRepository
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.network.NasaApi
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.await

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AsteroidDatabase.getInstance(application)
    private val repository = AsteroidRepository(database)

    private val weekAsteroidList = repository.asteroidList
    private val todayAsteroidList = repository.todayAsteroidList

    val pictureOfDay = repository.pictureOfDay
    /* val picture: LiveData<PictureOfDay>
         get() = repository.picture*/

    private val _navigateToSelectedProperty = MutableLiveData<Asteroid>()
    val asteroidList: MediatorLiveData<List<Asteroid>> = MediatorLiveData()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedProperty: LiveData<Asteroid>
        get() = _navigateToSelectedProperty

    init {
        getAsteroidData()
    }


    private fun getAsteroidData() {
        viewModelScope.launch {
            repository.refreshAsteroidList()
            repository.getPictureOfTheDate()
            asteroidList.addSource(weekAsteroidList) {
                asteroidList.value = it
            }

        }
    }

    fun displayPropertyDetails(asteroid: Asteroid) {
        _navigateToSelectedProperty.value = asteroid
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }


    fun onTodayAsteroidsClicked() {
        removeSource()
        asteroidList.addSource(todayAsteroidList) {
            asteroidList.value = it
        }
        // asteroidList.removeSource(todayAsteroidList)

//        repository.filterAsteroid()
    }

    fun onViewWeekAsteroidsClicked() {
        removeSource()
        asteroidList.addSource(weekAsteroidList) {
            asteroidList.value = it
        }

    }

    fun onSavedAsteroidsClicked() {
        removeSource()
        asteroidList.addSource(weekAsteroidList) {
            asteroidList.value = it
        }

    }

    private fun removeSource() {
        asteroidList.removeSource(todayAsteroidList)
        asteroidList.removeSource(weekAsteroidList)
    }

}