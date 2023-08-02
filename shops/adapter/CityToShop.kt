package com.buisness.bonuscards.shops.adapter

data class CityToShop(
    val cityName: String = "",
    val shops: HashMap<String, Shop> = hashMapOf()
)
