package com.example.beercellar.model


data class Beer(
    val id: Int,
    val user: String,
    val brewery: String,
    val name: String,
    val style: String,
    val abv: Double,
    val volume: Double,
    val pictureUrl: String,
    val howMany: Int = 1
) {
    constructor(
        id: Int,
        user: String,
        brewery: String,
        name: String,
        style: String,
        abv: Double,
        volume: Double,
        pictureUrl: String
    ) : this(id, user, brewery, name, style, abv, volume, pictureUrl, howMany = 1)

    override fun toString(): String {
        return "Beer(id=$id, user='$user', brewery='$brewery', name='$name', " +
                "style='$style', abv=$abv, volume=${volume}cl, pictureUrl='$pictureUrl', howMany=$howMany)"
    }
}