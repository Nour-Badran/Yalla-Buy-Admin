package com.example.yallabuyadmin.products.model

data class Product(
    val id: Long? = null,
    val title: String,
    val body_html: String,
    val vendor: String,
    val product_type: String,
    val tags: String,
    val images: List<Image> = listOf(),
    val variants: List<Variant> = listOf()
)

data class Variant(
    val id: Long? = null,
    val title: String,
    val price: String,
    val sku: String,
    val inventory_quantity: Long = 20L,
    val option1: String = "5",
    val option2: String? = "N/A"
)

data class VariantRequest(
    val variant: Variant
)

data class Image(
    val id: Long? = null,
    val src: String // URL of the product image
)
data class ProductResponse(
    val product: Product
)

data class ProductsResponse(
    val products: List<Product>
)
