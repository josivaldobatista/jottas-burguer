package com.jfb.jottasburguer.controller

import com.jfb.jottasburguer.model.dto.CustomerRequest
import com.jfb.jottasburguer.model.dto.CustomerResponse
import com.jfb.jottasburguer.service.CustomerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/customers")
class CustomerController(private val customerService: CustomerService) {

    @PostMapping
    fun createCustomer(@Valid @RequestBody request: CustomerRequest): ResponseEntity<CustomerResponse> {
        val createdCustomer = customerService.createCustomer(request)
        return ResponseEntity(createdCustomer, HttpStatus.CREATED)
    }

    @GetMapping
    fun findAllCustomers(): ResponseEntity<List<CustomerResponse>> {
        val customers = customerService.findAllCustomers()
        return ResponseEntity(customers, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun findCustomerById(@PathVariable id: Long): ResponseEntity<CustomerResponse> {
        val customer = customerService.findCustomerById(id)
        return ResponseEntity(customer, HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun updateCustomer(@PathVariable id: Long, @Valid @RequestBody request: CustomerRequest): ResponseEntity<CustomerResponse> {
        val updatedCustomer = customerService.updateCustomer(id, request)
        return ResponseEntity(updatedCustomer, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteCustomer(@PathVariable id: Long): ResponseEntity<Void> {
        customerService.deleteCustomer(id)
        return ResponseEntity.noContent().build()
    }
}