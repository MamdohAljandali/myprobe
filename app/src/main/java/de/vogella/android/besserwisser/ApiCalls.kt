package de.vogella.android.besserwisser;

import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.*;
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException;
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit;
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson

class ApiCalls{


    /**
     * OkHttpClient instance to make the calls
     */
    private val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS)
            .build()

    /**
     * to get all Networks
     * make a @get request on the BASE_URL_ALL and call it with the OkHttpClient instance
     * onFailure call onError, that belong to OnRequestCompleteListener
     * onResponse call onSuccess, that belong to OnRequestCompleteListener
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun postSensorValuesAverageToServer(id:String, type : String, average: Double) {
        val current = LocalDateTime.now()
        val rootObject= JSONObject()
        rootObject.put("Id",id)
        rootObject.put("Type",type)
        rootObject.put("Value",average.toString())
        rootObject.put("Datum",current)
        System.out.println(rootObject.toString())
        val mediaType = "application/json".toMediaType()
        val requestBody = rootObject.toString().toRequestBody(mediaType)
        val request = Request.Builder()
                .post(requestBody)
                .url("http://192.168.178.65:8091/add")
                .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                System.out.println(response.message)
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")


                }
            }

        })
    }

}
