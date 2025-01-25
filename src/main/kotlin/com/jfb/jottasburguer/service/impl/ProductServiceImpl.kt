package com.jfb.jottasburguer.service.impl

import com.jfb.jottasburguer.exception.FoodTypeNotFoundException
import com.jfb.jottasburguer.exception.ResourceNotFoundException
import com.jfb.jottasburguer.model.dto.ProductRequest
import com.jfb.jottasburguer.model.dto.ProductResponse
import com.jfb.jottasburguer.model.entity.Product
import com.jfb.jottasburguer.repository.FoodTypeRepository
import com.jfb.jottasburguer.repository.ProductRepository
import com.jfb.jottasburguer.service.ProductService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val foodTypeRepository: FoodTypeRepository
) : ProductService {

    private val logger = LoggerFactory.getLogger(ProductServiceImpl::class.java)

    override fun createProduct(request: ProductRequest): ProductResponse {
        logger.info("Creating product with name: ${request.name}")

        // Busca o FoodType pelo ID
        val foodType = foodTypeRepository.findById(request.foodTypeId)
            .orElseThrow { FoodTypeNotFoundException("Food type not found with ID: ${request.foodTypeId}") }

        val product = Product(
            name = request.name,
            ingredients = request.ingredients,
            description = request.description,
            price = request.price,
            imageUrl = request.imageUrl,
            foodType = foodType // Associa o FoodType ao Product
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
            .orElseThrow { ResourceNotFoundException("Product not found with ID: $id") }
        return mapToProductResponse(product)
    }

    override fun updateProduct(id: Long, request: ProductRequest): ProductResponse {
        logger.info("Updating product with ID: $id")

        // Busca o produto existente
        val product = productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found with ID: $id") }

        // Busca o novo FoodType pelo ID
        val foodType = foodTypeRepository.findById(request.foodTypeId)
            .orElseThrow { FoodTypeNotFoundException("Food type not found with ID: ${request.foodTypeId}") }

        // Atualiza os campos do produto
        product.name = request.name
        product.ingredients = request.ingredients
        product.description = request.description
        product.price = request.price
        product.imageUrl = request.imageUrl
        product.foodType = foodType // Atualiza o FoodType

        val updatedProduct = productRepository.save(product)
        logger.info("Product updated successfully: ${updatedProduct.name}")
        return mapToProductResponse(updatedProduct)
    }

    override fun deleteProduct(id: Long) {
        logger.info("Deleting product with ID: $id")

        if (!productRepository.existsById(id)) {
            throw ResourceNotFoundException("Product not found with ID: $id")
        }

        productRepository.deleteById(id)
        logger.info("Product deleted successfully: $id")
    }

    private fun mapToProductResponse(product: Product): ProductResponse {
        return ProductResponse(
            id = product.id!!,
            name = product.name,
            ingredients = product.ingredients,
            description = product.description,
            price = product.price,
            imageUrl = product.imageUrl,
            foodTypeId = product.foodType.id!! // Retorna o ID do FoodType
        )
    }
}