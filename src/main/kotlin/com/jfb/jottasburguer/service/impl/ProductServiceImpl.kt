package com.jfb.jottasburguer.service.impl

import com.jfb.jottasburguer.exception.ProductNotFoundException
import com.jfb.jottasburguer.model.dto.ProductRequest
import com.jfb.jottasburguer.model.dto.ProductResponse
import com.jfb.jottasburguer.model.entity.Product
import com.jfb.jottasburguer.repository.ProductRepository
import com.jfb.jottasburguer.service.ProductService
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository
) : ProductService {

    private val logger = LoggerFactory.getLogger(ProductServiceImpl::class.java)

    override fun createProduct(request: ProductRequest): ProductResponse {
        logger.info("Creating product with name: ${request.name}")
        val product = Product(
            name = request.name,
            ingredients = request.ingredients, // Novo campo
            description = request.description,
            price = request.price,
            imageUrl = request.imageUrl, // Pode ser nulo
            foodTypeId = request.foodTypeId // Novo campo
        )
        val savedProduct = productRepository.save(product)
        logger.info("Product created successfully: ${savedProduct.name}")
        return mapToProductResponse(savedProduct)
    }

    override fun findAllProducts(): List<ProductResponse> {
        logger.info("Fetching all products")
        return productRepository.findAll().map { mapToProductResponse(it) }
    }

    override fun findProductById(id: Long): ProductResponse {
        logger.info("Fetching product by ID: $id")
        val product = productRepository.findById(id)
            .orElseThrow { ProductNotFoundException("Product not found with ID: $id") }
        return mapToProductResponse(product)
    }

    override fun updateProduct(id: Long, request: ProductRequest): ProductResponse {
        logger.info("Updating product with ID: $id")
        val product = productRepository.findById(id)
            .orElseThrow { ProductNotFoundException("Product not found with ID: $id") }

        product.name = request.name
        product.ingredients = request.ingredients // Atualizando ingredients
        product.description = request.description
        product.price = request.price
        product.imageUrl = request.imageUrl // Atualizando imageUrl
        product.foodTypeId = request.foodTypeId // Atualizando foodTypeId

        val updatedProduct = productRepository.save(product)
        logger.info("Product updated successfully: ${updatedProduct.name}")
        return mapToProductResponse(updatedProduct)
    }

    override fun deleteProduct(id: Long) {
        logger.info("Deleting product with ID: $id")
        if (!productRepository.existsById(id)) {
            throw ProductNotFoundException("Product not found with ID: $id")
        }
        productRepository.deleteById(id)
        logger.info("Product deleted successfully: $id")
    }

    private fun mapToProductResponse(product: Product): ProductResponse {
        return ProductResponse(
            id = product.id!!,
            name = product.name,
            ingredients = product.ingredients, // Mapeando ingredients
            description = product.description,
            price = product.price,
            imageUrl = product.imageUrl, // Mapeando imageUrl
            foodTypeId = product.foodTypeId // Mapeando foodTypeId
        )
    }
}