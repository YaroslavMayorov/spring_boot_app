package com.sqlapp.demo.util

import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.ExplainStatement

object QueryValidator {

    fun isReadOnlyQuery(sql: String): Boolean {
        if (sql.isBlank()) return false

        val stmt: Statement = try {
            CCJSqlParserUtil.parse(sql)
        } catch (_: Exception) {
            return false
        }

        return stmt is Select || stmt is ExplainStatement
    }
}
