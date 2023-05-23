package com.example.palliativecareapp.models

class Topic {
    var id: String = ""
    var logo: String = ""
    var name: String = ""
    var description: String = ""
    var information: String = ""
    var autherId: String = ""
    var hidden: Boolean = false
    var subscription: Boolean = false


    constructor(id: String, logo: String, name: String, description: String) {
        this.id = id
        this.logo = logo
        this.name = name
        this.description = description
        this.hidden = false
        this.subscription = false
    }

    constructor()

}