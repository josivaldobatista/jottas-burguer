import com.jfb.jottasburguer.exception.DuplicateEmailException
import com.jfb.jottasburguer.exception.ResourceNotFoundException
import com.jfb.jottasburguer.model.dto.CustomerRequest
import com.jfb.jottasburguer.model.entity.Customer
import com.jfb.jottasburguer.repository.CustomerRepository
import com.jfb.jottasburguer.service.impl.CustomerServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.DataIntegrityViolationException
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class CustomerServiceImplTest {

    private lateinit var customerService: CustomerServiceImpl
    @Mock
    private lateinit var customerRepository: CustomerRepository

    @BeforeEach
    fun setup() {
        customerService = CustomerServiceImpl(customerRepository)
    }

    @Test
    fun `createCustomer should create customer and return success`() {
        // Arrange
        val request = CustomerRequest("John Doe", "johndoe@example.com", "(11) 98765-4321", "123 Main St")
        `when`(customerRepository.existsByEmail(request.email)).thenReturn(false)
        `when`(customerRepository.save(any(Customer::class.java))).thenReturn(Customer(1L, "John Doe", "johndoe@example.com", "(11) 98765-4321", "123 Main St"))

        // Act
        val response = customerService.createCustomer(request)

        // Assert
        assertEquals("John Doe", response.name)
        assertEquals("johndoe@example.com", response.email)
        assertEquals("(11) 98765-4321", response.phone)
        assertEquals("123 Main St", response.address)
    }

    @Test
    fun `createCustomer should throw DuplicateEmailException when email already exists`() {
        // Arrange
        val request = CustomerRequest("John Doe", "johndoe@example.com", "(11) 98765-4321", "123 Main St")
        `when`(customerRepository.existsByEmail(request.email)).thenReturn(true)

        // Act and Assert
        assertThrows(DuplicateEmailException::class.java) { customerService.createCustomer(request) }
    }

    @Test
    fun `findAllCustomers should return all customers`() {
        // Arrange
        val customers = listOf(
            Customer(1L, "John Doe", "johndoe@example.com", "(11) 98765-4321", "123 Main St"),
            Customer(2L, "Jane Doe", "janeDoe@example.com", "(11) 98765-4322", "456 Main St")
        )
        `when`(customerRepository.findAll()).thenReturn(customers)

        // Act
        val response = customerService.findAllCustomers()

        // Assert
        assertEquals(2, response.size)
        assertEquals("John Doe", response[0].name)
        assertEquals("johndoe@example.com", response[0].email)
        assertEquals("(11) 98765-4321", response[0].phone)
        assertEquals("123 Main St", response[0].address)
        assertEquals("Jane Doe", response[1].name)
        assertEquals("janeDoe@example.com", response[1].email)
        assertEquals("(11) 98765-4322", response[1].phone)
        assertEquals("456 Main St", response[1].address)
    }

    @Test
    fun `findCustomerById should return customer by ID`() {
        // Arrange
        val customerId = 1L
        val customer = Customer(customerId, "John Doe", "johndoe@example.com", "(11) 98765-4321", "123 Main St")
        `when`(customerRepository.findById(customerId)).thenReturn(Optional.of(customer))

        // Act
        val response = customerService.findCustomerById(customerId)

        // Assert
        assertEquals("John Doe", response.name)
        assertEquals("johndoe@example.com", response.email)
        assertEquals("(11) 98765-4321", response.phone)
        assertEquals("123 Main St", response.address)
    }

    @Test
    fun `findCustomerById should throw ResourceNotFoundException when customer not found`() {
        // Arrange
        val customerId = 1L
        `when`(customerRepository.findById(customerId)).thenReturn(Optional.empty())

        // Act and Assert
        assertThrows(ResourceNotFoundException::class.java) { customerService.findCustomerById(customerId) }
    }

    @Test
    fun `updateCustomer should update customer and return success`() {
        // Arrange
        val customerId = 1L
        val request = CustomerRequest("John Doe", "updatedEmail@example.com", "(11) 98765-4322", "456 Main St")
        `when`(customerRepository.findById(customerId)).thenReturn(Optional.of(Customer(customerId, "John Doe", "johndoe@example.com", "(11) 98765-4321", "123 Main St")))
        `when`(customerRepository.save(any(Customer::class.java))).thenReturn(Customer(customerId, "John Doe", "updatedEmail@example.com", "(11) 98765-4322", "456 Main St"))

        // Act
        val response = customerService.updateCustomer(customerId, request)

        // Assert
        assertEquals("John Doe", response.name)
        assertEquals("updatedEmail@example.com", response.email)
        assertEquals("(11) 98765-4322", response.phone)
        assertEquals("456 Main St", response.address)
    }

    @Test
    fun `updateCustomer should throw DuplicateEmailException when email already exists`() {
        // Arrange
        val customerId = 1L
        val request = CustomerRequest("John Doe", "updatedEmail@example.com", "(11) 98765-4322", "456 Main St")
        `when`(customerRepository.findById(customerId)).thenReturn(Optional.of(Customer(customerId, "John Doe", "johndoe@example.com", "(11) 98765-4321", "123 Main St")))
        `when`(customerRepository.save(any(Customer::class.java))).thenThrow(DataIntegrityViolationException::class.java)

        // Act and Assert
        assertThrows(DuplicateEmailException::class.java) { customerService.updateCustomer(customerId, request) }
    }

    @Test
    fun `updateCustomer should throw ResourceNotFoundException when customer not found`() {
        // Arrange
        val customerId = 1L
        val request = CustomerRequest("John Doe", "updatedEmail@example.com", "(11) 98765-4322", "456 Main St")
        `when`(customerRepository.findById(customerId)).thenReturn(Optional.empty())

        // Act and Assert
        assertThrows(ResourceNotFoundException::class.java) { customerService.updateCustomer(customerId, request) }
    }

    @Test
    fun `deleteCustomer should delete customer and return success`() {
        // Arrange
        val customerId = 1L
        `when`(customerRepository.existsById(customerId)).thenReturn(true)
        doNothing().`when`(customerRepository).deleteById(customerId)

        // Act
        customerService.deleteCustomer(customerId)

        // Assert
        Mockito.verify(customerRepository).deleteById(customerId)
    }

    @Test
    fun `deleteCustomer should throw ResourceNotFoundException when customer not found`() {
        // Arrange
        val customerId = 1L
        `when`(customerRepository.existsById(customerId)).thenReturn(false)

        // Act and Assert
        assertThrows(ResourceNotFoundException::class.java) { customerService.deleteCustomer(customerId) }
    }
}