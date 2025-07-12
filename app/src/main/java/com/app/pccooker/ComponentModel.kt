package com.app.pccooker

data class ComponentModel(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val ratingCount: Int = 0,
    val imageUrl: String = "",
    val amazonUrl: String = "",
    val description: String = "",
    val specifications: Map<String, String> = emptyMap(),
    val inStock: Boolean = true,
    val brand: String = "",
    val model: String = ""
)
