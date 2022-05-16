package com.enpik.inventorymanagement.controller

import com.enpik.inventorymanagement.consts.Api
import com.enpik.inventorymanagement.model.ErrorResponseModel
import com.enpik.inventorymanagement.model.GetManufacturerResponseModel
import com.enpik.inventorymanagement.model.GetUnitsResponseModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.io.StringReader
import java.util.*


class ApiController {

    fun getManufacturers() : MutableList<GetManufacturerResponseModel> {
        val url = Api.GET_MANUFACTURERS
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        var manufacturers = Collections.emptyList<GetManufacturerResponseModel>()
        runBlocking {
            withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val gson = Gson()
                    val stringReader = StringReader(response.body?.string())
                    manufacturers =
                        gson.fromJson(stringReader, Array<GetManufacturerResponseModel>::class.java)
                            .toList()
                }
            }
        }
        return manufacturers
    }

    fun addItem (code : String, name : String, manufacturerId : Int, qty:Int ) : String{
        val url = Api.ADD_ITEM
        val client = OkHttpClient()
        val gson = Gson()
        val json = "{\"code\":\""+code+"\",\"name\":\""+name+"\",\"manufacturerId\":"+manufacturerId+",\"qty\":"+qty+"}"
        val body: RequestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), json
        )
        var funResponse = ""
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        runBlocking {
            withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    if (response.code == 200) {
                        val body = response.body?.string()
                    } else{
                        val body = response.body?.string()
                        val entity = gson.fromJson(
                            body,
                            ErrorResponseModel::class.java
                        )
                        funResponse = entity.error
                    }
                }
            }
        }
        return funResponse
    }

    fun addManufacturer (name : String) : GetManufacturerResponseModel {
        val url = Api.ADD_MANUFACTURER
        val client = OkHttpClient()
        val gson = Gson()
        val json = "{\"name\":\"" + name + "\"}"
        val body: RequestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), json
        )
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        var entity: GetManufacturerResponseModel = GetManufacturerResponseModel("",-1)
        runBlocking {
            withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    if (response.code == 200) {
                        val body = response.body?.string()
                        entity = gson.fromJson(
                            body,
                            GetManufacturerResponseModel::class.java
                        )
                    }
                }
            }
        }
        return entity
    }

    fun getUnits () {
        val url = Api.GET_UNITS
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        runBlocking {
            withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    val gson = Gson()
                    val stringReader = StringReader(response.body?.string())
                    val list: List<GetUnitsResponseModel> = gson.fromJson(stringReader , Array<GetUnitsResponseModel>::class.java).toList()
                }
            }
        }
    }

}