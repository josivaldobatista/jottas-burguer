package com.jfb.jottasburguer.service.impl

import com.jfb.jottasburguer.exception.ResourceNotFoundException
import com.jfb.jottasburguer.model.dto.OrderItemRequest
import com.jfb.jottasburguer.model.dto.OrderItemResponse
import com.jfb.jottasburguer.model.dto.OrderRequest
import com.jfb.jottasburguer.model.dto.OrderResponse
import com.jfb.jottasburguer.model.entity.*
import com.jfb.jottasburguer.repository.CustomerRepository
import com.jfb.jottasburguer.repository.OrderItemRepository
import com.jfb.jottasburguer.repository.OrderRepository
import com.jfb.jottasburguer.repository.ProductRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class OrderServiceImplTest {

    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var orderItemRepository: OrderItemRepository

    @Mock
    private lateinit var customerRepository: CustomerRepository

    @Mock
    private lateinit var productRepository: ProductRepository

    @InjectMocks
    private lateinit var orderService: OrderServiceImpl

    private lateinit var customer: Customer
    private lateinit var product: Product
    private lateinit var order: Order
    private lateinit var orderItem: OrderItem

    @BeforeEach
    fun setUp() {
        // Configuração do Customer
        customer = Customer(
            id = 1L,
            name = "John Doe",
            email = "john@example.com",
            phone = "123456789",
            address = "123 Main St"
        )

        // Configuração do Product
        val foodType = FoodType(id = 1L, name = "Burger") // Simulação de FoodType
        product = Product(
            id = 1L,
            name = "Cheeseburger",
            ingredients = "Beef, Cheese, Lettuce",
            description = "Delicious cheeseburger",
            price = 10.0,
            imageUrl = "http://example.com/cheeseburger.jpg",
            foodType = foodType
        )

        // Configuração do Order
        order = Order(
            id = 1L,
            customer = customer,
            total = 20.0,
            status = OrderStatus.RECEIVED.name
        )

        // Configuração do OrderItem
        orderItem = OrderItem(
            id = 1L,
            order = order,
            product = product,
            quantity = 2,
            price = 10.0
        )
    }

    @Test
    fun `createOrder should return OrderResponse when order is created successfully`() {
        // Arrange
        val orderRequest = OrderRequest(customerId = 1L, items = listOf(OrderItemRequest(productId = 1L, quantity = 2)))
        val orderItems = listOf(orderItem)

        `when`(customerRepository.findById(1L)).thenReturn(Optional.of(customer))
        `when`(productRepository.findById(1L)).thenReturn(Optional.of(product))
        `when`(orderRepository.save(any(Order::class.java))).thenReturn(order)
        `when`(orderItemRepository.save(any(OrderItem::class.java))).thenReturn(orderItem)

        // Act
        val result = orderService.createOrder(orderRequest)

        // Assert
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals(1L, result.customerId)
        assertEquals(20.0, result.total)
        assertEquals("RECEIVED", result.status)
        assertEquals(1, result.items.size)
    }

    @Test
    fun `createOrder should throw ResourceNotFoundException when customer is not found`() {
        // Arrange
        val orderRequest = OrderRequest(customerId = 1L, items = listOf(OrderItemRequest(productId = 1L, quantity = 2)))

        `when`(customerRepository.findById(1L)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(ResourceNotFoundException::class.java) {
            orderService.createOrder(orderRequest)
        }
    }

    @Test
    fun `createOrder should throw ResourceNotFoundException when product is not found`() {
        // Arrange
        val orderRequest = OrderRequest(customerId = 1L, items = listOf(OrderItemRequest(productId = 1L, quantity = 2)))

        `when`(customerRepository.findById(1L)).thenReturn(Optional.of(customer))
        `when`(productRepository.findById(1L)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(ResourceNotFoundException::class.java) {
            orderService.createOrder(orderRequest)
        }
    }

    @Test
    fun `findOrderById should return OrderResponse when order is found`() {
        // Arrange
        val orderResponse = OrderResponse(
            id = 1L,
            customerId = 1L,
            total = 20.0,
            status = "RECEIVED",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val orderItemResponse = OrderItemResponse(
            productId = 1L,
            productName = "Cheeseburger",
            quantity = 2,
            price = 10.0
        )

        `when`(orderRepository.findOrderById(1L)).thenReturn(orderResponse)
        `when`(orderRepository.findOrderItemsByOrderId(1L)).thenReturn(listOf(orderItemResponse))

        // Act
        val result = orderService.findOrderById(1L)

        // Assert
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals(1L, result.customerId)
        assertEquals(20.0, result.total)
        assertEquals("RECEIVED", result.status)
        assertEquals(1, result.items.size)
    }

    @Test
    fun `findOrderById should throw ResourceNotFoundException when order is not found`() {
        // Arrange
        `when`(orderRepository.findOrderById(1L)).thenReturn(null)

        // Act & Assert
        assertThrows(ResourceNotFoundException::class.java) {
            orderService.findOrderById(1L)
        }
    }

    @Test
    fun `updateOrderStatus should return updated OrderResponse when status is updated`() {
        // Arrange
        val updatedOrder = order.copy(status = "PROCESSING", updatedAt = LocalDateTime.now())

        `when`(orderRepository.findById(1L)).thenReturn(Optional.of(order))
        `when`(orderRepository.save(any(Order::class.java))).thenReturn(updatedOrder)
        `when`(orderItemRepository.findByOrderIdTest(1L)).thenReturn(listOf(orderItem))

        // Act
        val result = orderService.updateOrderStatus(1L, "PROCESSING")

        // Assert
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals(1L, result.customerId)
        assertEquals(20.0, result.total)
        assertEquals("PROCESSING", result.status)
        assertEquals(1, result.items.size)
    }

    @Test
    fun `updateOrderStatus should throw ResourceNotFoundException when order is not found`() {
        // Arrange
        `when`(orderRepository.findById(1L)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(ResourceNotFoundException::class.java) {
            orderService.updateOrderStatus(1L, "PROCESSING")
        }
    }
}