package com.tofiq.myimdb.util

import org.junit.Assert.*
import org.junit.Test

class ResourceTest {

    @Test
    fun `Resource Success should contain data`() {
        // Given
        val testData = "test data"

        // When
        val resource = Resource.Success(testData)

        // Then
        assertTrue(resource is Resource.Success)
        assertEquals(testData, resource.data)
        assertNull(resource.message)
    }

    @Test
    fun `Resource Error should contain message and optional data`() {
        // Given
        val errorMessage = "Error occurred"
        val errorData = "error data"

        // When
        val resource = Resource.Error(errorMessage, errorData)

        // Then
        assertTrue(resource is Resource.Error)
        assertEquals(errorMessage, resource.message)
        assertEquals(errorData, resource.data)
    }

    @Test
    fun `Resource Error should contain message without data`() {
        // Given
        val errorMessage = "Error occurred"

        // When
        val resource = Resource.Error<String>(errorMessage)

        // Then
        assertTrue(resource is Resource.Error)
        assertEquals(errorMessage, resource.message)
        assertNull(resource.data)
    }

    @Test
    fun `Resource Loading should not contain data or message`() {
        // When
        val resource = Resource.Loading<String>()

        // Then
        assertTrue(resource is Resource.Loading)
        assertNull(resource.data)
        assertNull(resource.message)
    }

    @Test
    fun `Resource Success with null data should work`() {
        // When
        val resource = Resource.Success<String?>(null)

        // Then
        assertTrue(resource is Resource.Success)
        assertNull(resource.data)
        assertNull(resource.message)
    }

    @Test
    fun `Resource Error with null data should work`() {
        // Given
        val errorMessage = "Error occurred"

        // When
        val resource = Resource.Error<String>(errorMessage, null)

        // Then
        assertTrue(resource is Resource.Error)
        assertEquals(errorMessage, resource.message)
        assertNull(resource.data)
    }

    @Test
    fun `Resource Success with complex data should work`() {
        // Given
        val complexData = mapOf(
            "key1" to "value1",
            "key2" to 123,
            "key3" to listOf("item1", "item2")
        )

        // When
        val resource = Resource.Success(complexData)

        // Then
        assertTrue(resource is Resource.Success)
        assertEquals(complexData, resource.data)
        assertNull(resource.message)
    }

    @Test
    fun `Resource Error with complex data should work`() {
        // Given
        val errorMessage = "Complex error"
        val complexData = mapOf(
            "errorCode" to 500,
            "details" to "Server error",
            "timestamp" to "2023-01-01T00:00:00Z"
        )

        // When
        val resource = Resource.Error(errorMessage, complexData)

        // Then
        assertTrue(resource is Resource.Error)
        assertEquals(errorMessage, resource.message)
        assertEquals(complexData, resource.data)
    }

    @Test
    fun `Resource Loading with different types should work`() {
        // When
        val stringLoading = Resource.Loading<String>()
        val intLoading = Resource.Loading<Int>()
        val listLoading = Resource.Loading<List<String>>()

        // Then
        assertTrue(stringLoading is Resource.Loading)
        assertTrue(intLoading is Resource.Loading)
        assertTrue(listLoading is Resource.Loading)
        assertNull(stringLoading.data)
        assertNull(intLoading.message)
    }

    @Test
    fun `Resource Success with empty string should work`() {
        // Given
        val emptyString = ""

        // When
        val resource = Resource.Success(emptyString)

        // Then
        assertTrue(resource is Resource.Success)
        assertEquals(emptyString, resource.data)
        assertNull(resource.message)
    }

    @Test
    fun `Resource Error with empty message should work`() {
        // Given
        val emptyMessage = ""

        // When
        val resource = Resource.Error<String>(emptyMessage)

        // Then
        assertTrue(resource is Resource.Error)
        assertEquals(emptyMessage, resource.message)
        assertNull(resource.data)
    }

    @Test
    fun `Resource Success with zero value should work`() {
        // Given
        val zeroValue = 0

        // When
        val resource = Resource.Success(zeroValue)

        // Then
        assertTrue(resource is Resource.Success)
        assertEquals(zeroValue, resource.data)
        assertNull(resource.message)
    }

    @Test
    fun `Resource Error with zero value data should work`() {
        // Given
        val errorMessage = "Error"
        val zeroValue = 0

        // When
        val resource = Resource.Error(errorMessage, zeroValue)

        // Then
        assertTrue(resource is Resource.Error)
        assertEquals(errorMessage, resource.message)
        assertEquals(zeroValue, resource.data)
    }

    @Test
    fun `Resource Success with boolean values should work`() {
        // Given
        val trueValue = true
        val falseValue = false

        // When
        val trueResource = Resource.Success(trueValue)
        val falseResource = Resource.Success(falseValue)

        // Then
        assertTrue(trueResource is Resource.Success)
        assertTrue(falseResource is Resource.Success)
        assertEquals(trueValue, trueResource.data)
        assertEquals(falseValue, falseResource.data)
    }

    @Test
    fun `Resource Error with boolean data should work`() {
        // Given
        val errorMessage = "Boolean error"
        val booleanData = false

        // When
        val resource = Resource.Error(errorMessage, booleanData)

        // Then
        assertTrue(resource is Resource.Error)
        assertEquals(errorMessage, resource.message)
        assertEquals(booleanData, resource.data)
    }

    @Test
    fun `Resource Success with list data should work`() {
        // Given
        val listData = listOf("item1", "item2", "item3")

        // When
        val resource = Resource.Success(listData)

        // Then
        assertTrue(resource is Resource.Success)
        assertEquals(listData, resource.data)
        assertNull(resource.message)
    }

    @Test
    fun `Resource Error with list data should work`() {
        // Given
        val errorMessage = "List error"
        val listData = listOf(1, 2, 3, 4, 5)

        // When
        val resource = Resource.Error(errorMessage, listData)

        // Then
        assertTrue(resource is Resource.Error)
        assertEquals(errorMessage, resource.message)
        assertEquals(listData, resource.data)
    }

    @Test
    fun `Resource Success with empty list should work`() {
        // Given
        val emptyList = emptyList<String>()

        // When
        val resource = Resource.Success(emptyList)

        // Then
        assertTrue(resource is Resource.Success)
        assertEquals(emptyList, resource.data)
        assertNull(resource.message)
    }

    @Test
    fun `Resource Error with empty list data should work`() {
        // Given
        val errorMessage = "Empty list error"
        val emptyList = emptyList<Int>()

        // When
        val resource = Resource.Error(errorMessage, emptyList)

        // Then
        assertTrue(resource is Resource.Error)
        assertEquals(errorMessage, resource.message)
        assertEquals(emptyList, resource.data)
    }

    @Test
    fun `Resource Success with custom object should work`() {
        // Given
        data class CustomObject(val id: Int, val name: String)
        val customObject = CustomObject(1, "test")

        // When
        val resource = Resource.Success(customObject)

        // Then
        assertTrue(resource is Resource.Success)
        assertEquals(customObject, resource.data)
        assertNull(resource.message)
    }

    @Test
    fun `Resource Error with custom object data should work`() {
        // Given
        data class CustomObject(val id: Int, val name: String)
        val errorMessage = "Custom object error"
        val customObject = CustomObject(1, "test")

        // When
        val resource = Resource.Error(errorMessage, customObject)

        // Then
        assertTrue(resource is Resource.Error)
        assertEquals(errorMessage, resource.message)
        assertEquals(customObject, resource.data)
    }

    @Test
    fun `Resource Success with nullable type should work`() {
        // Given
        val nullableString: String? = "test"

        // When
        val resource = Resource.Success<String?>(nullableString)

        // Then
        assertTrue(resource is Resource.Success)
        assertEquals(nullableString, resource.data)
        assertNull(resource.message)
    }

    @Test
    fun `Resource Error with nullable type data should work`() {
        // Given
        val errorMessage = "Nullable error"
        val nullableInt: Int? = 42

        // When
        val resource = Resource.Error(errorMessage, nullableInt)

        // Then
        assertTrue(resource is Resource.Error)
        assertEquals(errorMessage, resource.message)
        assertEquals(nullableInt, resource.data)
    }

    @Test
    fun `Resource Success with null nullable type should work`() {
        // Given
        val nullableString: String? = null

        // When
        val resource = Resource.Success<String?>(nullableString)

        // Then
        assertTrue(resource is Resource.Success)
        assertNull(resource.data)
        assertNull(resource.message)
    }

    @Test
    fun `Resource Error with null nullable type data should work`() {
        // Given
        val errorMessage = "Null nullable error"
        val nullableInt: Int? = null

        // When
        val resource = Resource.Error(errorMessage, nullableInt)

        // Then
        assertTrue(resource is Resource.Error)
        assertEquals(errorMessage, resource.message)
        assertNull(resource.data)
    }

    @Test
    fun `Resource Success with unit type should work`() {
        // Given
        val unitValue = Unit

        // When
        val resource = Resource.Success(unitValue)

        // Then
        assertTrue(resource is Resource.Success)
        assertEquals(unitValue, resource.data)
        assertNull(resource.message)
    }

    @Test
    fun `Resource Error with unit type data should work`() {
        // Given
        val errorMessage = "Unit error"
        val unitValue = Unit

        // When
        val resource = Resource.Error(errorMessage, unitValue)

        // Then
        assertTrue(resource is Resource.Error)
        assertEquals(errorMessage, resource.message)
        assertEquals(unitValue, resource.data)
    }

    @Test
    fun `Resource Success with double value should work`() {
        // Given
        val doubleValue = 3.14159

        // When
        val resource = Resource.Success(doubleValue)

        // Then
        assertTrue(resource is Resource.Success)
        assertEquals(doubleValue, resource.data)
        assertNull(resource.message)
    }

    @Test
    fun `Resource Error with double data should work`() {
        // Given
        val errorMessage = "Double error"
        val doubleValue = 2.71828

        // When
        val resource = Resource.Error(errorMessage, doubleValue)

        // Then
        assertTrue(resource is Resource.Error)
        assertEquals(errorMessage, resource.message)
        assertEquals(doubleValue, resource.data)
    }

    @Test
    fun `Resource Success with long value should work`() {
        // Given
        val longValue = 9223372036854775807L

        // When
        val resource = Resource.Success<Long>(longValue)

        // Then
        assertTrue(resource is Resource.Success)
        assertEquals(longValue, resource.data)
        assertNull(resource.message)
    }

    @Test
    fun `Resource Error with long data should work`() {
        // Given
        val errorMessage = "Long error"
        val longValue = -1000000000L

        // When
        val resource = Resource.Error<Long>(errorMessage, longValue)

        // Then
        assertTrue(resource is Resource.Error)
        assertEquals(errorMessage, resource.message)
        assertEquals(longValue, resource.data)
    }
} 