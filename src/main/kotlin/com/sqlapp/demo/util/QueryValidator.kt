package com.sqlapp.demo.util

import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.Statements
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.ExplainStatement
import org.slf4j.LoggerFactory

object QueryValidator {

    private val log = LoggerFactory.getLogger(javaClass)

    fun isReadOnlyQuery(sql: String): Boolean {
        if (sql.isBlank()) {
            log.warn("Validation failed: empty SQL")
            return false
        }

        return try {
            log.debug("Validating SQL: {}", sql.trim())
            val stmts: Statements = CCJSqlParserUtil.parseStatements(sql.trim())
            val list = stmts.statements

            if (list.size != 1) {
                log.warn("Validation failed: multiple statements detected (count={})", list.size)
                return false
            }

            val stmt = list[0]
            val isAllowed = stmt is Select || stmt is ExplainStatement
            if (!isAllowed) {
                log.warn("Validation failed: unsupported statement type={}", stmt.javaClass.simpleName)
            } else {
                log.info("Validation passed: {}", stmt.javaClass.simpleName)
            }
            isAllowed
        } catch (e: Exception) {
            log.error("Validation failed: parsing error - {}", e.message)
            false
        }
    }
}
