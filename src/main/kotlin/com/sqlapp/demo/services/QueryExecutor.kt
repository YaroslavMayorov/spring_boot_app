package com.sqlapp.demo.services

import com.sqlapp.demo.util.QueryValidator
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class QueryExecutor(private val jdbcTemplate: JdbcTemplate) {
}
