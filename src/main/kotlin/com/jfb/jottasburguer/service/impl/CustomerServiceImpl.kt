package com.jfb.jottasburguer.service.impl

import com.jfb.jottasburguer.exception.DuplicateEmailException
import com.jfb.jottasburguer.exception.CustomerNotFoundException
import com.jfb.jottasburguer.model.dto.CustomerRequest
import com.jfb.jottasburguer.model.dto.CustomerResponse
import com.jfb.jottasburguer.model.entity.Customer
import com.jfb.jottasburguer.repository.CustomerRepository
import com.jfb.jottasburguer.service.CustomerService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository
) : CustomerService {

    private val logger = LoggerFactory.getLogger(CustomerServiceImpl::class.java)

    override fun createCustomer(request: CustomerRequest): CustomerResponse {
        logger.info("Creating customer with email: ${request.email}")

        // Verifica se o email já está em uso
        if (customerRepository.existsByEmail(request.email)) {
            logger.error("Tentativa de cadastro com email duplicado: ${request.email}")
            throw DuplicateEmailException("O email fornecido já está em uso.")
        }

        val customer = Customer(
            name = request.name,
            email = request.email,
            phone = request.phone,
            address = request.address
        )

        try {
            val savedCustomer = customerRepository.save(customer)
            logger.info("Customer created successfully: ${savedCustomer.email}")
            return mapToCustomerResponse(savedCustomer)
        } catch (ex: DataIntegrityViolationException) {
            logger.error("Erro ao salvar cliente: ${ex.message}", ex)
            throw DuplicateEmailException("O email fornecido já está em uso.")
        }
    }

    override fun findAllCustomers(): List<CustomerResponse> {
        logger.info("Fetching all customers")
        return customerRepository.findAll().map { mapToCustomerResponse(it) }
    }

    override fun findCustomerById(id: Long): CustomerResponse {
        logger.info("Fetching customer by ID: $id")
        val customer = customerRepository.findById(id)
            .orElseThrow { CustomerNotFoundException("Customer not found with ID: $id") }
        return mapToCustomerResponse(customer)
    }

    override fun updateCustomer(id: Long, request: CustomerRequest): CustomerResponse {
        logger.info("Updating customer with ID: $id")

        val customer = customerRepository.findById(id)
            .orElseThrow { CustomerNotFoundException("Customer not found with ID: $id") }

        customer.name = request.name
        customer.email = request.email
        customer.phone = request.phone
        customer.address = request.address

        try {
            val updatedCustomer = customerRepository.save(customer)
            logger.info("Customer updated successfully: ${updatedCustomer.email}")
            return mapToCustomerResponse(updatedCustomer)
        } catch (ex: DataIntegrityViolationException) {
            logger.error("Duplicate email: ${request.email}")
            throw DuplicateEmailException("Email already in use: ${request.email}")
        }
    }

    override fun deleteCustomer(id: Long) {
        logger.info("Deleting customer with ID: $id")

        if (!customerRepository.existsById(id)) {
            throw CustomerNotFoundException("Customer not found with ID: $id")
        }

        customerRepository.deleteById(id)
        logger.info("Customer deleted successfully: $id")
    }

    private fun mapToCustomerResponse(customer: Customer): CustomerResponse {
        return CustomerResponse(
            id = customer.id!!,
            name = customer.name,
            email = customer.email,
            phone = customer.phone,
            address = customer.address
        )
    }
}