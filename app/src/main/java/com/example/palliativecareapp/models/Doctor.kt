package com.example.palliativecareapp.models

class Doctor {
    var id: String = ""
    var name: String = ""
    var image: String = ""

    constructor()

    constructor(id: String, name: String, image: String) {
        this.id = id
        this.name = name
        this.image = image
    }
}