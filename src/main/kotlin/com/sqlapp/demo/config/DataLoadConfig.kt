package com.sqlapp.demo.config


import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component


@Component
class DataLoadConfig(
    private val jdbcTemplate: JdbcTemplate
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun init() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS passengers")
        jdbcTemplate.execute(
            """
        CREATE TABLE IF NOT EXISTS passengers AS 
        SELECT * FROM CSVREAD('classpath:data/titanic.csv');
    """.trimIndent()
        )
        jdbcTemplate.execute(
            """
            CREATE TABLE IF NOT EXISTS queries (
                id IDENTITY PRIMARY KEY,
                query VARCHAR NOT NULL
            )
            """.trimIndent()
        )
        log.info("Loaded Titanic into passengers; queries table ready.")
    }
}
