package com.sqlapp.demo.util

import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.Statements
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.ExplainStatement

object QueryValidator {

    fun isReadOnlyQuery(sql: String): Boolean {
        if (sql.isBlank()) return false

        return try {
            val stmts: Statements = CCJSqlParserUtil.parseStatements(sql.trim())

            val list = stmts.statements
            if (list.size != 1) {
                return false
            }

            val stmt = list[0]
            stmt is Select || stmt is ExplainStatement
        } catch (_: Exception) {
            false
        }
    }
}
