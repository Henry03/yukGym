package com.example.yukgym.volley.api

class ProfileApi {
    companion object{
        val BASE_URL = "https://henryyg.com/yukgym/public/api/"
//        val BASE_URL = "http://192.168.100.20/yukGym_Laravel/yukgym/public/api/"
        val LOGIN = BASE_URL + "login"
        var LOGOUT = BASE_URL + "logout"
        val GET_ALL_URL = BASE_URL + "login"
        val GET_BY_ID_URL = BASE_URL + "user/"
        val ADD_URL = BASE_URL + "register"
        val UPDATE_URL = BASE_URL + "user/"
        val AUTH = BASE_URL + "auth"
        val LATEST_HISTORY = BASE_URL + "historyLast/"
        val FINGERPRINT = BASE_URL + "userFingerprint/"
            val FINGERPRINT_STATUS = BASE_URL + "getUserFingerprint/"
//        val DELETE_URL = BASE_URL + "user/"
    }
}