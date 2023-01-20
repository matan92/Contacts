package com.project.contacts.data

data class Contact(
    val id: Int,
    var firstName: String,
    var lastName: String,
    var displayName: String,
    var emailList : HashMap<String, String>,
    var phoneList : HashMap<String, String>,
    val photo: String
)

