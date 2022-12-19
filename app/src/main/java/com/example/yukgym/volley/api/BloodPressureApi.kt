package com.example.yukgym.volley.api

class BloodPressureApi {
    companion object{
        val BASE_URL = Api.BASE_URL

        val GET_ALL_URL = BASE_URL + "bloodPressure"
        val GET_BY_USER_URL = BASE_URL + "bloodPressurebyuser"
        val GET_BY_ID_URL = BASE_URL + "bloodPressureDetail/"
        val GET_LAST_DATA = BASE_URL + "bloodPressurelastdata"
        val ADD_URL = BASE_URL + "bloodPressure"
        val UPDATE_URL = BASE_URL + "bloodPressure/"
        val DELETE_URL = BASE_URL + "bloodPressure/"
    }
}