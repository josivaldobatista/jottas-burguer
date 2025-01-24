package com.jfb.jottasburguer.service

import com.jfb.jottasburguer.model.dto.CustomerRequest
import com.jfb.jottasburguer.model.dto.CustomerResponse

interface CustomerService {
    fun createCustomer(request: CustomerRequest): CustomerResponse
    fun findAllCustomers(): List<CustomerResponse>
    fun findCustomerById(id: Long): CustomerResponse
    fun updateCustomer(id: Long, request: CustomerRequest): CustomerResponse
    fun deleteCustomer(id: Long)
}