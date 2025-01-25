package com.jfb.jottasburguer.service.impl

import com.jfb.jottasburguer.exception.OrderNotFoundException
import com.jfb.jottasburguer.exception.ResourceNotFoundException
import com.jfb.jottasburguer.model.dto.OrderItemResponse
import com.jfb.jottasburguer.model.dto.OrderRequest
import com.jfb.jottasburguer.model.dto.OrderResponse
import com.jfb.jottasburguer.model.entity.Order
import com.jfb.jottasburguer.model.entity.OrderItem
import com.jfb.jottasburguer.repository.CustomerRepository
import com.jfb.jottasburguer.repository.OrderItemRepository
import com.jfb.jottasburguer.repository.OrderRepository
import com.jfb.jottasburguer.repository.ProductRepository
import com.jfb.jottasburguer.service.OrderService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductRepository
) : OrderService {

    private val logger = LoggerFactory.getLogger(CustomerServiceImpl::class.java)

    override fun createOrder(request: OrderRequest): OrderResponse {
        logger.info("Creating order for customer ID: ${request.customerId}")

        // Busca o cliente
        val customer = customerRepository.findById(request.customerId)
            .orElseThrow { ResourceNotFoundException("Customer not found with ID: ${request.customerId}") }

        // Calcula o total e cria os itens do pedido
        var total = 0.0
        val orderItems = request.items.map { itemRequest ->
            val product = productRepository.findById(itemRequest.productId)
                .orElseThrow { ResourceNotFoundException("Product not found with ID: ${itemRequest.productId}") }

            total += product.price * itemRequest.quantity

            OrderItem(
                product = product,
                quantity = itemRequest.quantity,
                price = product.price
            )
        }

        // Cria o pedido
        val order = Order(
            customer = customer,
            total = total,
            status = "RECEIVED"
        )

        // Salva o pedido no banco de dados
        val savedOrder = orderRepository.save(order)

        // Associa o pedido salvo aos itens e salva os itens
        val savedOrderItems = orderItems.map { orderItem ->
            orderItem.order = savedOrder // Associa o pedido ao item
            orderItemRepository.save(orderItem) // Salva o item no banco de dados
        }

        logger.info("Order created successfully with ID: ${savedOrder.id}")
        return mapToOrderResponse(savedOrder, savedOrderItems)
    }

    override fun findOrderById(id: Long): OrderResponse {
        logger.info("Fetching order by ID: $id")
        val order = orderRepository.findById(id)
            .orElseThrow { OrderNotFoundException("Order not found with ID: $id") }

        val orderItems = orderItemRepository.findByOrderId(id)
        return mapToOrderResponse(order, orderItems)
    }

    override fun updateOrderStatus(id: Long, status: String): OrderResponse {
        logger.info("Updating order status with ID: $id to $status")
        val order = orderRepository.findById(id)
            .orElseThrow { OrderNotFoundException("Order not found with ID: $id") }

        order.status = status
        order.updatedAt = LocalDateTime.now()

        val updatedOrder = orderRepository.save(order)
        val orderItems = orderItemRepository.findByOrderId(id)
        return mapToOrderResponse(updatedOrder, orderItems)
    }

    private fun mapToOrderResponse(order: Order, orderItems: List<OrderItem>): OrderResponse {
        return OrderResponse(
            id = order.id!!,
            customerId = order.customer?.id!!,
            total = order.total,
            status = order.status,
            createdAt = order.createdAt,
            updatedAt = order.updatedAt,
            items = orderItems.map { item ->
                OrderItemResponse(
                    productId = item.product.id!!,
                    productName = item.product.name,
                    quantity = item.quantity,
                    price = item.price
                )
            }
        )
    }
}