package com.sqlapp.demo.api

import com.sqlapp.demo.repo.StoredQuery
import com.sqlapp.demo.repo.QueryRepository
import com.sqlapp.demo.services.QueryExecutor
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(QueryController::class)
class QueryControllerTest @Autowired constructor(
    private val mvc: MockMvc
) {
    @MockitoBean lateinit var repo: QueryRepository
    @MockitoBean lateinit var exec: QueryExecutor

    @Test fun `POST queries accepts text_plain and returns id`() {
        given(repo.add("SELECT 1")).willReturn(7L)

        mvc.perform(
            post("/queries")
                .contentType(MediaType.TEXT_PLAIN)
                .content("SELECT 1")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", `is`(7)))

        verify(repo).add("SELECT 1")
    }

    @Test fun `GET queries returns id and query`() {
        given(repo.list()).willReturn(listOf(
            StoredQuery(1, "SELECT * FROM passengers")
        ))

        mvc.perform(get("/queries"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id", `is`(1)))
            .andExpect(jsonPath("$[0].query", `is`("SELECT * FROM passengers")))
    }

    @Test fun `GET execute returns 2D array`() {
        given(repo.findById(1L)).willReturn("SELECT 1")
        given(exec.execute("SELECT 1")).willReturn(listOf(listOf(1)))

        mvc.perform(get("/execute").param("query", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0][0]", `is`(1)))
    }

    @Test fun `GET execute 404 on missing id`() {
        given(repo.findById(99L)).willReturn(null)

        mvc.perform(get("/execute").param("query", "99"))
            .andExpect(status().isNotFound)
    }
}
