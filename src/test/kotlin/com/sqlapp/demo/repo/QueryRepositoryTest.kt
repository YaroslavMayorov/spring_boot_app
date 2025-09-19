package com.sqlapp.demo.repo

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.JdbcTemplate

@JdbcTest
class QueryRepositoryTest @Autowired constructor(
    private val jdbc: JdbcTemplate
) {
    private val repo = QueryRepository(jdbc)

    init {
        jdbc.execute(
            """
        CREATE TABLE IF NOT EXISTS queries(
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        query VARCHAR(10000) NOT NULL
        )
    """.trimIndent()
        )
    }

    @Test fun `add and list`() {
        val id1 = repo.add("SELECT 1")
        val id2 = repo.add("SELECT 2")
        assertNotNull(id1)
        assertNotNull(id2)
        assertTrue(id1!! > 0 && id2!! > id1)

        val all = repo.list()
        assertEquals(2, all.size)
        assertEquals("SELECT 1", all[0].query)
        assertEquals("SELECT 2", all[1].query)
    }

    @Test fun `findById`() {
        val id = repo.add("SELECT 42")
        assertNotNull(id)
        assertEquals("SELECT 42", repo.findById(id!!))
        assertNull(repo.findById(9999L))
    }
}
