package com.example.yukgym.volley.api

class ProfileApi {
    companion object{
        val BASE_URL = "http://192.168.19.253/yukGym_Laravel/yukgym/public/api/"

        val LOGIN = BASE_URL + "login"
        val GET_ALL_URL = BASE_URL + "login"
        val GET_BY_ID_URL = BASE_URL + "user/"
        val ADD_URL = BASE_URL + "register"
        val UPDATE_URL = BASE_URL + "user/"
//        val DELETE_URL = BASE_URL + "user/"
    }
}