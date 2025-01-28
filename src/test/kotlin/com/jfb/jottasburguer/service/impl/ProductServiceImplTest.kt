package com.jfb.jottasburguer.service.impl

import com.jfb.jottasburguer.exception.ResourceNotFoundException
import com.jfb.jottasburguer.model.dto.ProductRequest
import com.jfb.jottasburguer.model.entity.FoodType
import com.jfb.jottasburguer.model.entity.Product
import com.jfb.jottasburguer.repository.FoodTypeRepository
import com.jfb.jottasburguer.repository.ProductRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class ProductServiceImplTest {

    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var foodTypeRepository: FoodTypeRepository

    @InjectMocks
    private lateinit var productService: ProductServiceImpl

    private lateinit var foodType: FoodType
    private lateinit var product: Product
    private lateinit var productRequest: ProductRequest

    @BeforeEach
    fun setUp() {
        foodType = FoodType(id = 1L, name = "Burger")
        product = Product(
            id = 1L,
            name = "Cheeseburger",
            ingredients = "Beef, Cheese, Lettuce",
            description = "Delicious cheeseburger",
            price = 10.0,
            imageUrl = "http://example.com/cheeseburger.jpg",
            foodType = foodType
        )
        productRequest = ProductRequest(
            name = "Cheeseburger",
            ingredients = "Beef, Cheese, Lettuce",
            description = "Delicious cheeseburger",
            price = 10.0,
            imageUrl = "http://example.com/cheeseburger.jpg",
            foodTypeId = 1L
        )
    }

    @Test
    fun `createProduct should return ProductResponse when product is created successfully`() {
        // Arrange
        `when`(foodTypeRepository.findById(1L)).thenReturn(Optional.of(foodType))
        `when`(productRepository.save(any(Product::class.java))).thenReturn(product)

        // Act
        val result = productService.createProduct(productRequest)

        // Assert
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("Cheeseburger", result.name)
        assertEquals("Beef, Cheese, Lettuce", result.ingredients)
        assertEquals("Delicious cheeseburger", result.description)
        assertEquals(10.0, result.price)
        assertEquals("http://example.com/cheeseburger.jpg", result.imageUrl)
        assertEquals(1L, result.foodTypeId)
    }

    @Test
    fun `createProduct should throw ResourceNotFoundException when food type is not found`() {
        // Arrange
        `when`(foodTypeRepository.findById(1L)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(ResourceNotFoundException::class.java) {
            productService.createProduct(productRequest)
        }
    }

    @Test
    fun `findAllProducts should return list of ProductResponse`() {
        // Arrange
        `when`(productRepository.findAll()).thenReturn(listOf(product))

        // Act
        val result = productService.findAllProducts()

        // Assert
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("Cheeseburger", result[0].name)
        assertEquals("Beef, Cheese, Lettuce", result[0].ingredients)
        assertEquals("Delicious cheeseburger", result[0].description)
        assertEquals(10.0, result[0].price)
        assertEquals("http://example.com/cheeseburger.jpg", result[0].imageUrl)
        assertEquals(1L, result[0].foodTypeId)
    }

    @Test
    fun `findProductById should return ProductResponse when product is found`() {
        // Arrange
        `when`(productRepository.findById(1L)).thenReturn(Optional.of(product))

        // Act
        val result = productService.findProductById(1L)

        // Assert
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("Cheeseburger", result.name)
        assertEquals("Beef, Cheese, Lettuce", result.ingredients)
        assertEquals("Delicious cheeseburger", result.description)
        assertEquals(10.0, result.price)
        assertEquals("http://example.com/cheeseburger.jpg", result.imageUrl)
        assertEquals(1L, result.foodTypeId)
    }

    @Test
    fun `findProductById should throw ResourceNotFoundException when product is not found`() {
        // Arrange
        `when`(productRepository.findById(1L)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(ResourceNotFoundException::class.java) {
            productService.findProductById(1L)
        }
    }

    @Test
    fun `updateProduct should return updated ProductResponse when product is updated successfully`() {
        // Arrange
        `when`(productRepository.findById(1L)).thenReturn(Optional.of(product))
        `when`(foodTypeRepository.findById(1L)).thenReturn(Optional.of(foodType))
        `when`(productRepository.save(any(Product::class.java))).thenReturn(product)

        // Act
        val result = productService.updateProduct(1L, productRequest)

        // Assert
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("Cheeseburger", result.name)
        assertEquals("Beef, Cheese, Lettuce", result.ingredients)
        assertEquals("Delicious cheeseburger", result.description)
        assertEquals(10.0, result.price)
        assertEquals("http://example.com/cheeseburger.jpg", result.imageUrl)
        assertEquals(1L, result.foodTypeId)
    }

    @Test
    fun `updateProduct should throw ResourceNotFoundException when product is not found`() {
        // Arrange
        `when`(productRepository.findById(1L)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(ResourceNotFoundException::class.java) {
            productService.updateProduct(1L, productRequest)
        }
    }

    @Test
    fun `updateProduct should throw ResourceNotFoundException when food type is not found`() {
        // Arrange
        `when`(productRepository.findById(1L)).thenReturn(Optional.of(product))
        `when`(foodTypeRepository.findById(1L)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(ResourceNotFoundException::class.java) {
            productService.updateProduct(1L, productRequest)
        }
    }

    @Test
    fun `deleteProduct should delete product when product exists`() {
        // Arrange
        `when`(productRepository.existsById(1L)).thenReturn(true)

        // Act
        productService.deleteProduct(1L)

        // Assert
        verify(productRepository, times(1)).deleteById(1L)
    }

    @Test
    fun `deleteProduct should throw ResourceNotFoundException when product does not exist`() {
        // Arrange
        `when`(productRepository.existsById(1L)).thenReturn(false)

        // Act & Assert
        assertThrows(ResourceNotFoundException::class.java) {
            productService.deleteProduct(1L)
        }
    }
}