package com.jfb.jottasburguer.controller

import com.jfb.jottasburguer.model.dto.ProductRequest
import com.jfb.jottasburguer.model.dto.ProductResponse
import com.jfb.jottasburguer.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    @PostMapping
    fun createProduct(@Valid @RequestBody request: ProductRequest): ResponseEntity<ProductResponse> {
        val createdProduct = productService.createProduct(request)
        return ResponseEntity(createdProduct, HttpStatus.CREATED)
    }

    @GetMapping
    fun findAllProducts(): ResponseEntity<List<ProductResponse>> {
        val products = productService.findAllProducts()
        return ResponseEntity(products, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun findProductById(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = productService.findProductById(id)
        return ResponseEntity(product, HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: Long, @Valid @RequestBody request: ProductRequest): ResponseEntity<ProductResponse> {
        val updatedProduct = productService.updateProduct(id, request)
        return ResponseEntity(updatedProduct, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }
}