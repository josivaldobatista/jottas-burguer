package com.jfb.jottasburguer.service.impl

import com.jfb.jottasburguer.exception.ResourceNotFoundException
import com.jfb.jottasburguer.model.dto.OrderItemRequest
import com.jfb.jottasburguer.model.dto.OrderItemResponse
import com.jfb.jottasburguer.model.dto.OrderRequest
import com.jfb.jottasburguer.model.dto.OrderResponse
import com.jfb.jottasburguer.model.entity.Order
import com.jfb.jottasburguer.model.entity.OrderItem
import com.jfb.jottasburguer.model.entity.OrderStatus
import com.jfb.jottasburguer.repository.CustomerRepository
import com.jfb.jottasburguer.repository.OrderItemRepository
import com.jfb.jottasburguer.repository.OrderRepository
import com.jfb.jottasburguer.repository.ProductRepository
import com.jfb.jottasburguer.service.OrderService
import jakarta.transaction.Transactional
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

    private val logger = LoggerFactory.getLogger(OrderServiceImpl::class.java)

    override fun createOrder(request: OrderRequest): OrderResponse {
        logger.info("Creating order for customer ID: ${request.customerId}")

        val customer = customerRepository.findById(request.customerId)
            .orElseThrow { ResourceNotFoundException("Customer not found with ID: ${request.customerId}") }

        val (total, orderItems) = calculateTotalAndCreateOrderItems(request.items)

        val order = Order(
            customer = customer,
            total = total,
            status = OrderStatus.RECEIVED.name
        )

        val savedOrder = orderRepository.save(order)
        val savedOrderItems = saveOrderItems(orderItems, savedOrder)

        logger.info("Order created successfully with ID: ${savedOrder.id}")
        return mapToOrderResponse(savedOrder, savedOrderItems)
    }

    private fun calculateTotalAndCreateOrderItems(items: List<OrderItemRequest>): Pair<Double, List<OrderItem>> {
        var total = 0.0
        val orderItems = items.map { itemRequest ->
            val product = productRepository.findById(itemRequest.productId)
                .orElseThrow { ResourceNotFoundException("Product not found with ID: ${itemRequest.productId}") }

            total += product.price * itemRequest.quantity

            OrderItem(
                product = product,
                quantity = itemRequest.quantity,
                price = product.price
            )
        }
        return total to orderItems
    }

    private fun saveOrderItems(orderItems: List<OrderItem>, savedOrder: Order): List<OrderItem> {
        return orderItems.map { orderItem ->
            orderItem.order = savedOrder
            orderItemRepository.save(orderItem)
        }
    }

    override fun findOrderById(id: Long): OrderResponse {
        logger.info("Fetching order by ID: $id")

        val orderResponse = orderRepository.findOrderById(id)
            ?: throw ResourceNotFoundException("Order not found with ID: $id")

        val orderItems = orderRepository.findOrderItemsByOrderId(id)

        return orderResponse.copy(items = orderItems)
    }

    @Transactional
    override fun updateOrderStatus(id: Long, status: String): OrderResponse {
        logger.info("Updating order status with ID: $id to $status")
        val order = orderRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Order not found with ID: $id") }

        order.status = status
        order.updatedAt = LocalDateTime.now()

        val updatedOrder = orderRepository.save(order)
        val orderItems = orderItemRepository.findByOrderIdTest(id)
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

    companion object {
        private const val DEFAULT_ORDER_STATUS = "RECEIVED"
    }
}