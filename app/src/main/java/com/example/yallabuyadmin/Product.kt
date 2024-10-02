package com.example.yallabuyadmin

data class Product(
    val id: Long? = null,
    val title: String,
    val body_html: String,
    val vendor: String,
    val product_type: String,
    val tags: String
)

data class ProductResponse(
    val product: Product
)

data class ProductsResponse(
    val products: List<Product>
)
