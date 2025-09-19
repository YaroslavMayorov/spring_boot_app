package com.sqlapp.demo.services

import com.sqlapp.demo.util.QueryValidator
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class QueryExecutor(private val jdbcTemplate: JdbcTemplate) {

    fun execute(sql: String): List<Map<String, Any?>> {
        require(QueryValidator.isReadOnlyQuery(sql)) { "Only SELECT/WITH/EXPLAIN are allowed" }
        return jdbcTemplate.queryForList(sql)
    }
}
