package com.sqlapp.demo.repo


import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement


data class StoredQuery(val id: Long, val query: String)

@Repository
class QueryRepository(private val jdbcTemplate: JdbcTemplate) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun add(queryText: String): Long? = try {
        SimpleJdbcInsert(jdbcTemplate)
            .withTableName("queries")
            .usingGeneratedKeyColumns("id")
            .executeAndReturnKey(mapOf("query" to queryText))
            .toLong()
    } catch (ex: Exception) {
        log.warn("Insert failed: {}", ex.message)
        null
    }

    fun list(): List<StoredQuery> =
        jdbcTemplate.query("SELECT id, query FROM queries ORDER BY id") { rs, _ ->
            StoredQuery(rs.getLong("id"), rs.getString("query"))
        }

    fun findById(id: Long): String? =
        jdbcTemplate.query("SELECT query FROM queries WHERE id=?", { rs, _ -> rs.getString(1) }, id)
            .firstOrNull()
}
