package com.udacity.asteroidradar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.squareup.moshi.Moshi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.network.NasaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: AsteroidDatabase) {
/*
    val todayAsteroidList: LiveData<List<Asteroid>>
        get() = database.asteroidDao().getTodayAsteroid(Constants.getCurrentDate())
*/


    /*var asteroidList: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao().getAll()) {
            it
        }*/

    val asteroidList: LiveData<List<Asteroid>>
        get() = database.asteroidDao().getAll()

    val todayAsteroidList: LiveData<List<Asteroid>>
        get() = database.asteroidDao().getTodayAsteroid(Constants.getCurrentDate())

    val pictureOfDay: LiveData<PictureOfDay>
        get() = database.pictureDao().get()


    suspend fun refreshAsteroidList() {
        withContext(Dispatchers.IO) {
            try {
                val asteroid = NasaApi.retrofitService.getAsteroids()
                val json = JSONObject(asteroid)
                val data = parseAsteroidsJsonResult(json)
                database.asteroidDao().updateData(data)
                Log.e("data", "")

            } catch (e: Exception) {
                Log.e("data", "")
            }
        }
    }


    suspend fun getPictureOfTheDate() {
        withContext(Dispatchers.IO) {
            try {
                val response = NasaApi.retrofitService.getPictureOfDay(
                    BuildConfig.API_KEY
                )
                val pic = Moshi.Builder()
                    .build()
                    .adapter(PictureOfDay::class.java)
                    .fromJson(response)
                    ?:
                    // Return an empty picture
                    PictureOfDay(-1, "image", "", "")
                database.pictureDao().insert(pic)
                Log.e("data", "")

            } catch (e: Exception) {
                Log.e("data", "")

            }

        }
    }


//    fun filterAsteroid() {
//        asteroidList = Transformations.map(
//            database.asteroidDao().getAll()
//        ) {
//            it
//        }
//
//        /*     asteroidList = database.asteroidDao().getTodayAsteroid(Constants.getCurrentDate())
//             Log.e("data", "")*/
//
//    }

}