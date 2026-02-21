package com.trevisol.photos.data.repositories

import android.content.Context
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.trevisol.photos.data.models.PhotoList
import com.trevisol.photos.domain.entities.Photo
import java.net.HttpURLConnection.HTTP_NOT_MODIFIED
import java.net.HttpURLConnection.HTTP_OK

class JsonPlaceholderAPI(context: Context) {
    companion object {
        const val PHOTOS_ENDPOINT = "https://jsonplaceholder.typicode.com/photos"

        @Volatile
        private var INSTANCE: JsonPlaceholderAPI? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: JsonPlaceholderAPI(context).also {
                INSTANCE = it
            }
        }
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addRequestToQueue(request: Request<T>) {
        requestQueue.add(request)
    }

    class PhotosRequest(
        private val responseListener: Response.Listener<List<Photo>>,
        errorListener: Response.ErrorListener,
    ): Request<List<Photo>>(Method.GET, PHOTOS_ENDPOINT, errorListener) {
        override fun parseNetworkResponse(p0: NetworkResponse?): Response<List<Photo>?> {
            if (p0?.statusCode == HTTP_OK || p0?.statusCode == HTTP_NOT_MODIFIED) {
                return String(p0.data).run {
                    Response.success(
                        Gson().fromJson(this, PhotoList::class.java).toDomainList(),
                        HttpHeaderParser.parseCacheHeaders(p0)
                    )
                }
            } else {
                return Response.error(VolleyError())
            }
        }

        override fun deliverResponse(p0: List<Photo>?) {
            responseListener.onResponse(p0)
        }
    }
}
