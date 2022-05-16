package com.enpik.inventorymanagement.consts

class Api {

    companion object {
        val BASE_URL : String = "http://192.168.43.32:8080"
        val GET_MANUFACTURERS : String = BASE_URL+"/manufacturers"
        val ADD_MANUFACTURER : String = BASE_URL+"/manufacturers/new"
        val GET_UNITS : String = BASE_URL+"/units"
        val ADD_ITEM : String = BASE_URL+"/items/new"
    }
}