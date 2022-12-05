package com.example.yukgym.volley.api

class HistoryApi {
    companion object{
        val BASE_URL = "http://10.53.12.180/yukGym_Laravel/yukgym/public/api/"
//        val BASE_URL = "https://henryyg.com/yukgym/public/api/"

        val GET_ALL_URL = BASE_URL + "history/"
        val GET_BY_ID_URL = BASE_URL + "historyDetail/"
        val ADD_URL = BASE_URL + "history"
        val UPDATE_URL = BASE_URL + "history/"
        val DELETE_URL = BASE_URL + "history/"
    }
}