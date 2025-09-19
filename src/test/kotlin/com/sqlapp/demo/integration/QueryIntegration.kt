package com.sqlapp.demo.integration

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QueryIntegration @Autowired constructor(
    private val rest: TestRestTemplate
) {
    @LocalServerPort lateinit var port: Integer

    private fun url(path: String) = "http://localhost:${port}/$path"

    @Test
    fun `add, list, execute`() {
        // 1) Add a query
        val addHeaders = HttpHeaders().apply { contentType = MediaType.TEXT_PLAIN }
        val addResp = rest.postForEntity(url("queries"), HttpEntity("SELECT * FROM passengers", addHeaders), Map::class.java)
        assertEquals(HttpStatus.OK, addResp.statusCode)
        val id = (addResp.body!!["id"] as Number).toLong()
        assertTrue(id > 0)

        // 2) List queries
        val listResp = rest.getForEntity(url("queries"), List::class.java)
        assertEquals(HttpStatus.OK, listResp.statusCode)
        assertTrue((listResp.body as List<*>).isNotEmpty())

        // 3) Execute
        val execResp = rest.getForEntity(url("execute?query=$id"), List::class.java)
        assertEquals(HttpStatus.OK, execResp.statusCode)
        val rows = execResp.body as List<*>
        assertTrue(rows.isNotEmpty(), "Passengers should be loaded from CSV")
    }

    @Test
    fun `execute returns 404 when id not found`() {
        val resp = rest.getForEntity(url("execute?query=999999"), Map::class.java)
        assertEquals(HttpStatus.NOT_FOUND, resp.statusCode)
        assertEquals("Query not found", resp.body?.get("error"))
    }

}
