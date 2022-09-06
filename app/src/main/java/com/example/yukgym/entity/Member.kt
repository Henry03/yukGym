package com.example.yukgym.entity

class Member(var name: String, var email: String, var birthDate: String, var noTelp: String, var pass: String) {
    companion object{
        @JvmField
        var listOfDosen = arrayOf(
            Member("admin", "admin", "1-Jan-2000", "082122888877", "yukgym")
        )
    }
}