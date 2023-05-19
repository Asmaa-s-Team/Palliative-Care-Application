package com.example.palliativecareapp.models

class Topic {
    var id: String = ""
    var logo: String = ""
    var name: String = ""
    var description: String = ""
    var information: String = ""
    lateinit var images: ArrayList<String>
    lateinit var video: ArrayList<String>
    lateinit var pdf: ArrayList<String>
    lateinit var infographic: ArrayList<String>
    lateinit var comments: ArrayList<HashMap<String, String>>
    var autherId: String = ""

    constructor(id: String, logo: String, name: String, description: String) {
        this.id = id
        this.logo = logo
        this.name = name
        this.description = description
    }
}