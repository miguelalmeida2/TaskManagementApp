package pt.isel.ls

import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import kotlin.test.assertEquals

val url: String = System.getenv("LS_DATABASE_URL")

fun setUrl(url: String): PGSimpleDataSource {
    val dataSource = PGSimpleDataSource()
    dataSource.setURL(url)
    return dataSource
}

class DBtests {
    @Test
    fun `test db`() {
        var res: String
        val dataSource = setUrl(url)

        // insert rows at table teachers
        dataSource.connection.use {
            res = it.prepareStatement(
                "drop table if exists students;create table students (number int primary key,name varchar(80));"
            ).execute().toString()
        }
        // verify the expected results
        assertEquals("false", res)

        // insert rows at table teachers
        dataSource.connection.use {
            it.prepareStatement(
                "insert into students(number, name) values (1,'Pedro');"
            ).execute()

            // do a select query to check if the data is in the table
            val stm = it.prepareStatement("select name from students where name='Pedro';")
            val rs = stm.executeQuery()

            while (rs.next()) {
                res = rs.getString("name")
            }
        }
        // verify the expected results
        assertEquals("Pedro", res)

        dataSource.connection.use {
            // update a row at table teachers
            it.prepareStatement(
                "update students set name='Miguel' where number=1;"
            ).executeUpdate()

            // do a select query to check if the data was updated
            val stm = it.prepareStatement("select name from students where number=1;")
            val rs = stm.executeQuery()

            while (rs.next()) {
                res = rs.getString("name")
            }
        }
        // check the results
        assertEquals("Miguel", res)

        dataSource.connection.use {
            // update a row at table teachers
            it.prepareStatement("delete from students where name='Miguel'").execute()

            // do a select query to check if the data was updated
            val stm = it.prepareStatement("select name from students where name='Miguel';")
            val rs = stm.executeQuery()

            res = rs.next().toString()
        }
        // check the results
        assertEquals("false", res)

        // insert rows at table teachers
        dataSource.connection.use {
            res = it.prepareStatement(
                "drop table if exists students;"
            ).execute().toString()
        }
        // verify the expected results
        assertEquals("false", res)
    }
}
