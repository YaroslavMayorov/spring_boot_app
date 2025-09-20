package com.sqlapp.demo.config


import com.opencsv.CSVReader
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.io.InputStreamReader


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
            CREATE TABLE IF NOT EXISTS passengers (
              PassengerId INT,
              Survived INT,
              Pclass INT,
              Name VARCHAR(255),
              Sex VARCHAR(50),
              Age DOUBLE,
              SibSp INT,
              Parch INT,
              Ticket VARCHAR(50),
              Fare DOUBLE,
              Cabin VARCHAR(50),
              Embarked VARCHAR(5)
            );
    """.trimIndent()
        )
        val count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM passengers", Int::class.java) ?: 0
        if (count == 0) {
            val stream = this::class.java.getResourceAsStream("/data/titanic.csv")
                ?: error("CSV not found at /data/titanic.csv")
            CSVReader(InputStreamReader(stream)).use { reader ->
                val header = reader.readNext() ?: return@use
                require(header.size >= 12) { "Unexpected CSV header size" }

                val sql = """
                  INSERT INTO passengers(
                    PassengerId, Survived, Pclass, Name, Sex, Age, SibSp, Parch, Ticket, Fare, Cabin, Embarked
                  ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
                """.trimIndent()

                val batch = mutableListOf<Array<Any?>>()
                var row = reader.readNext()
                while (row != null) {
                    batch += arrayOf(
                        row[0].toIntOrNull(),
                        row[1].toIntOrNull(),
                        row[2].toIntOrNull(),
                        row[3],
                        row[4],
                        row[5].toDoubleOrNull(),
                        row[6].toIntOrNull(),
                        row[7].toIntOrNull(),
                        row[8],
                        row[9].toDoubleOrNull(),
                        row[10],
                        row[11]
                    )
                    row = reader.readNext()
                }
                jdbcTemplate.batchUpdate(sql, batch)
            }
        }
        jdbcTemplate.execute(
            """
            CREATE TABLE IF NOT EXISTS queries (
              id BIGSERIAL PRIMARY KEY,
              query VARCHAR(10000) NOT NULL
            )
            """.trimIndent()
        )
        log.info("Loaded Titanic into passengers; queries table ready.")
    }
}
