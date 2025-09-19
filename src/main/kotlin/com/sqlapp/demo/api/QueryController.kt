package com.sqlapp.demo.api

import com.sqlapp.demo.repo.QueryRepository
import com.sqlapp.demo.services.QueryExecutor
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class AddQueryResponse(val id: Long)
data class ListItem(val id: Long, val query: String)

@RestController
class QueryController(
    private val repo: QueryRepository,
    private val exec: QueryExecutor
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/queries", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun add(@RequestBody body: String): ResponseEntity<Any> {
        log.info("Adding new query: {}", body.trim())
        val id = repo.add(body.trim())
            ?: return ResponseEntity.internalServerError()
                .body(mapOf("error" to "Could not save query"))
        log.info("Query saved with id={}", id)
        return ResponseEntity.ok(AddQueryResponse(id))
    }

    @GetMapping("/queries")
    fun list(): List<ListItem> {
        log.info("Listing queries")
        return repo.list().map { ListItem(it.id, it.query) }
    }

    @GetMapping("/execute")
    fun execute(@RequestParam("query") id: Long): ResponseEntity<List<List<Any?>>> {
        log.info("Executing query with id={}", id)
        val sql = repo.findById(id)
            ?: return ResponseEntity
                .status(404)
                .body(listOf(listOf("error", "Query not found")))

        return try {
            val result = exec.execute(sql)
            log.info("Query executed successfully, rows={}", result.size)
            ResponseEntity.ok(result)
        } catch (e: IllegalArgumentException) {
            log.warn("Invalid query: {}", e.message)
            ResponseEntity
                .badRequest()
                .body(listOf(listOf("error", e.message ?: "Invalid query")))
        } catch (e: DataAccessException) {
            val msg = e.mostSpecificCause.message ?: e.message ?: "SQL error"
            log.error("SQL execution error: {}", msg, e)
            ResponseEntity.badRequest()
                .body(listOf(listOf("error", msg)))
        }
    }
}
