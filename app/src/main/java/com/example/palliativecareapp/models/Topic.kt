package com.example.palliativecareapp.models

class Topic {
    var id: String = ""
    var logo: String = ""
    var name: String = ""
    var description: String = ""
    var information: String = ""
    var autherId: String = ""
    var hidden: Boolean = false


    constructor(id: String, logo: String, name: String, description: String, hidden: Boolean) {
        this.id = id
        this.logo = logo
        this.name = name
        this.description = description
        this.hidden = hidden
    }

    constructor()

}