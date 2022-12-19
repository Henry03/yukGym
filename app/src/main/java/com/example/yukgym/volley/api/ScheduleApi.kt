package com.example.yukgym.volley.api

class ScheduleApi {
    companion object{
        val BASE_URL = Api.BASE_URL

        val GET_ALL_URL = BASE_URL + "schedule"
        val GET_BY_USER_URL = BASE_URL + "schedulebyuser"
        val GET_BY_ID_URL = BASE_URL + "scheduleDetail/"
        val ADD_URL = BASE_URL + "schedule"
        val UPDATE_URL = BASE_URL + "schedule/"
        val DELETE_URL = BASE_URL + "schedule/"
    }
}