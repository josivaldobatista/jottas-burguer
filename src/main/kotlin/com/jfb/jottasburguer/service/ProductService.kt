package com.jfb.jottasburguer.service

import com.jfb.jottasburguer.model.dto.ProductRequest
import com.jfb.jottasburguer.model.dto.ProductResponse

interface ProductService {
    fun createProduct(request: ProductRequest): ProductResponse
    fun findAllProducts(): List<ProductResponse>
    fun findProductById(id: Long): ProductResponse
    fun updateProduct(id: Long, request: ProductRequest): ProductResponse
    fun deleteProduct(id: Long)
}