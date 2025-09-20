# SQL runner app

Simple Spring Boot service that stores SQL queries and executes them against an in-memory H2 database loaded with the Titanic dataset.

---

## Table of Contents
1. [Overview](#overview)  
2. [Project Structure](#project-structure)  
3. [Setup and Installation](#setup-and-installation)  
4. [Usage](#usage)  
5. [Limitations](#limitations)  
6. [Contacts](#contacts)

## Overview

- **Database:** H2 in-memory. On startup, the Titanic dataset (`titanic.csv`) is loaded into a table `passengers` using H2’s `CSVREAD`.  
- **Features:**
  - Store SQL queries (`POST /queries`)  
  - List stored queries (`GET /queries`)  
  - Execute query by ID (`GET /execute?query={id}`)  
- **Safety:**
  - Only `SELECT`, `WITH`, or `EXPLAIN` statements are allowed  
  - Exactly one statement per request  
  - Execution runs with a read-only connection

## Project Structure

```bash
  com.sqlapp.demo
├─ api/
│ └─ QueryController.kt # REST API endpoints
├─ service/
│ └─ QueryExecutor.kt # Executes validated SQL, returns 2D arrays
├─ repo/
│ └─ QueryRepository.kt # Provides convenient interaction with the 'queries' table (save, list, find by ID)
├─ config/
│ └─ DataLoadConfig.kt # Loads Titanic CSV into passengers table and creates queries table
└─ util/
└─ QueryValidator.kt # Validates SQL (SELECT/WITH/EXPLAIN only)

src/main/resources/data/titanic.csv # Dataset
src/test/ # Unit and integration tests

```

## Setup and Installation

**Requirements:** Java 17+, Gradle 

1. Clone repository:
   ```bash
   git clone https://github.com/YaroslavMayorov/spring_boot_app.git
   cd spring_boot_app
   ```

2. Build and run:
   ```bash
   ./gradlew bootRun
   ```

   or run DemoApplicationKt in IDE


## Usage

1. Add a query

```bash
 curl -X POST "http://localhost:8080/queries" \
  -H "Content-Type: text/plain" \
  -d "SELECT PassengerId, Name, Age FROM passengers ORDER BY PassengerId LIMIT 10"
# -> {"id":1}
 ```

2. List queries
```bash
curl http://localhost:8080/queries
# -> [{"id":1,"query":"SELECT PassengerId, Name, Age FROM passengers ORDER BY PassengerId LIMIT 10"}]
 ```

3. Execute a query
```bash
curl "http://localhost:8080/execute?query=1"
# -> [[1,"Braund, Mr. Owen Harris",22.0],[2,"Cumings, Mrs. John Bradley (Florence Briggs Thayer)",38.0],[3,"Heikkinen, Miss. Laina",26.0],[4,"Futrelle, Mrs. Jacques Heath (Lily May Peel)",35.0],[5,"Allen, Mr. William Henry",35.0],[6,"Moran, Mr. James",null],[7,"McCarthy, Mr. Timothy J",54.0],[8,"Palsson, Master. Gosta Leonard",2.0],[9,"Johnson, Mrs. Oscar W (Elisabeth Vilhelmina Berg)",27.0],[10,"Nasser, Mrs. Nicholas (Adele Achem)",14.0]
```

## Limitations

- In-memory dataset (H2)
- No pagination or limits on result size (risk of large payloads).
- Only EXPLAIN SELECT statements are allowed.
- No caching or optimization for repeated queries
- No async job handling for long-running queries.
- Exceptions are handled too generically, without detailed error classification and recovery strategies.


## Contacts 

If you have any questions, feel free to contact me:  

- **Email:** sasha.val2006@gmail.com
- **Telegram:** @thedreamertype
