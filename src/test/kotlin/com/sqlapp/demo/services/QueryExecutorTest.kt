package com.sqlapp.demo.services

import com.sqlapp.demo.util.QueryValidator // used internally by executor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles

@JdbcTest
@ActiveProfiles("test")
class QueryExecutorTest @Autowired constructor(
    private val jdbc: JdbcTemplate
) {
    private val exec = QueryExecutor(jdbc)

    init {
        jdbc.execute("DROP TABLE IF EXISTS passengers")
        jdbc.execute("CREATE TABLE passengers(id INT, name VARCHAR(100))")
        jdbc.update("INSERT INTO passengers(id, name) VALUES (1, 'Ann'), (2, 'Bob')")
    }

    @Test fun `exec SELECT returns rows`() {
        val rows = exec.execute("SELECT id, name FROM passengers ORDER BY id")
        assertEquals(listOf(1, "Ann"), rows[0])
        assertEquals(listOf(2, "Bob"), rows[1])
    }

    @Test fun `rejects INSERT`() {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            exec.execute("INSERT INTO passengers(id) VALUES (3)")
        }
        assertTrue(ex.message!!.contains("SELECT/WITH/EXPLAIN"))
    }
}
