package com.sqlapp.demo.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class QueryValidatorTest {

    @Test
    fun `valid simple SELECT`() {
        val sql = "SELECT * FROM passengers"
        assertTrue(QueryValidator.isReadOnlyQuery(sql))
    }

    @Test
    fun `valid SELECT with semicolon`() {
        val sql = "SELECT * FROM passengers;"
        assertTrue(QueryValidator.isReadOnlyQuery(sql))
    }

    @Test
    fun `valid WITH SELECT`() {
        val sql = """
            WITH adults AS (SELECT * FROM passengers WHERE age > 18)
            SELECT * FROM adults
        """.trimIndent()
        assertTrue(QueryValidator.isReadOnlyQuery(sql))
    }

    @Test
    fun `valid EXPLAIN SELECT`() {
        val sql = "EXPLAIN SELECT * FROM passengers"
        assertTrue(QueryValidator.isReadOnlyQuery(sql))
    }

    @Test
    fun `invalid INSERT`() {
        val sql = "INSERT INTO passengers(id) VALUES (1)"
        assertFalse(QueryValidator.isReadOnlyQuery(sql))
    }

    @Test
    fun `invalid multiple statements`() {
        val sql = "SELECT * FROM passengers; DELETE FROM passengers;"
        assertFalse(QueryValidator.isReadOnlyQuery(sql))
    }

    @Test
    fun `blank string`() {
        val sql = "   "
        assertFalse(QueryValidator.isReadOnlyQuery(sql))
    }

    @Test
    fun `random text`() {
        val sql = "catcatacat"
        assertFalse(QueryValidator.isReadOnlyQuery(sql))
    }
}
