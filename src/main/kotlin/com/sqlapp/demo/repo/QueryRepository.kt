package com.sqlapp.demo.repo

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository

data class StoredQuery(val id: Long, val query: String)

@Repository
class QueryRepository(private val jdbcTemplate: JdbcTemplate) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun add(queryText: String): Long? = try {
        log.debug("Inserting query: {}", queryText)
        val id = SimpleJdbcInsert(jdbcTemplate)
            .withTableName("queries")
            .usingGeneratedKeyColumns("id")
            .executeAndReturnKey(mapOf("query" to queryText))
            .toLong()
        log.info("Query inserted successfully with id={}", id)
        id
    } catch (ex: Exception) {
        log.error("Insert failed: {}", ex.message, ex)
        null
    }

    fun list(): List<StoredQuery> {
        log.debug("Fetching all queries")
        val result = jdbcTemplate.query(
            "SELECT id, query FROM queries ORDER BY id"
        ) { rs, _ -> StoredQuery(rs.getLong("id"), rs.getString("query")) }
        log.info("Fetched {} queries", result.size)
        return result
    }

    fun findById(id: Long): String? {
        log.debug("Looking up query with id={}", id)
        val query = jdbcTemplate.query(
            "SELECT query FROM queries WHERE id=?",
            { rs, _ -> rs.getString(1) },
            id
        ).firstOrNull()

        if (query == null) {
            log.warn("Query not found for id={}", id)
        } else {
            log.info("Query found for id={}", id)
        }
        return query
    }
}
