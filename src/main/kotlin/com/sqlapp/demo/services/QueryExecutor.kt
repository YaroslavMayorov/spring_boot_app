package com.sqlapp.demo.services

import com.sqlapp.demo.util.QueryValidator
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class QueryExecutor(private val jdbcTemplate: JdbcTemplate) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun execute(sql: String): List<List<Any?>> {
        log.debug("Received SQL: {}", sql)

        require(QueryValidator.isReadOnlyQuery(sql)) {
            "Only SELECT/WITH/EXPLAIN are allowed to execute"
        }

        log.info("Executing query...")
        val result = jdbcTemplate.query(sql) { rs, _ ->
            val cols = rs.metaData.columnCount
            (1..cols).map { rs.getObject(it) }
        }

        log.info("Query executed successfully, rows returned={}", result.size)
        return result
    }
}
