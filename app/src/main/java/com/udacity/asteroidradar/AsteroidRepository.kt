package com.udacity.asteroidradar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.Moshi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.network.NasaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: AsteroidDatabase) {
    val asteroidList: LiveData<List<Asteroid>>
        get() = database.asteroidDao().getAll()

    val pictureOfDay: LiveData<PictureOfDay>
        get() = database.pictureDao().get()


    suspend fun refreshAsteroidList() {
        withContext(Dispatchers.IO) {
            try {
                val asteroid = NasaApi.retrofitService.getAsteroids(
                    BuildConfig.API_KEY
                )
                val json = JSONObject(asteroid)
                val data = parseAsteroidsJsonResult(json)
                database.asteroidDao().insertAll(data)


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

}