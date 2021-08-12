package backend

import java.sql.ResultSet
import java.sql.Types


/** Handler of database related operations. */
object DatabaseHandler {
  /**
    * Execute a query using the connected database.
    *
    * @param query query to execute.
    * @param mode mode of the query, either fetch or execute.
    * @return a ResultSet containing the returned rows after 
    * the execution of a query.
    */
  def executeQuery(query: String, mode: String = "fetch"): ResultSet = {
    val client = new Client()
    client.startSession()

    println(query)

    try {
      if (mode == "fetch") {
        val result = client.fetch(query)
        client.endSession()
        return result
      } else {
        println(mode)
        client.execute(query)
        client.endSession()
        return null
      }
    } catch {
      case e: Throwable => 
        println(f"An error occurred while executing query: '$query%s'")
        println(e)
        client.endSession()
    }
    return null
  }

  /**
    * Fetch data from the connected database as a ResultSet.
    *
    * @param tables tables to fetch from.
    * @param columns columns to use in fetch.
    * @param condition WHERE conditions to use in fetch.
    * @param joins JOIN statements to use in fetch.
    * @return a ResultSet containing fetched rows.
    */
  def fetchData(
    tables: String, 
    columns: String = "*", 
    condition: String = null,
    joins: String = null
  ): ResultSet = {
    val query = if (joins == null)
      "SELECT %s FROM %s".format(columns, tables)
    else
      "SELECT %s FROM %s LEFT JOIN %s".format(columns, tables, joins)
    
    if (condition != null)
      return executeQuery(query + " WHERE %s".format(condition))
    
    return executeQuery(query)
  }

  /**
    * Insert data into the connected database.
    *
    * @param table table to insert into.
    * @param columns columns to use in insertion.
    * @param rows rows to insert.
    * @param returning columns of the inserted rows to return after insertion.
    * @return an array of maps containing inserted rows.
    */
  def insertData(
    table: String,
    columns: String,
    rows: Array[String],
    returning: String = "*"
  ): Array[Map[String, Any]] = {
    val query = "INSERT INTO %s(%s) VALUES (%s) RETURNING %s;"
    return rows.map(row => {
        try {
          resultSetToMapArray(
            executeQuery(query.format(table, columns, row, returning))
          ).head
        } catch {
          case e: NoSuchElementException =>
            null
        }
      }
    )
  }

  /**
    * Update data of the connected database.
    *
    * @param table table to update.
    * @param newValue assignment expression containing the column and the new value.
    * @param condition WHERE condition to use in row matching.
    * @param returning columns of table to return after update.
    * @return updated row as a map.
    */
  def updateData(
    table: String,
    newValue: String,
    condition: String,
    returning: String = "*"
  ): Map[String, Any] = {
    val query = f"UPDATE $table%s SET $newValue%s WHERE $condition%s RETURNING $returning%s;"
    try {
      return resultSetToMapArray(
        executeQuery(query)
      ).head
    } catch {
      case e: NoSuchElementException =>
        return null
    }
  }

  /**
    * Delete data from connected database.
    *
    * @param table table to delete from.
    * @param condition condition to use in row matching.
    * @return 1 if operation was successful, 0 otherwise.
    */
  def deleteData(
    table: String,
    condition: String
  ): Int = {
    val query = f"DELETE FROM $table%s WHERE $condition%s;"

    try {
      executeQuery(query, "execute")
      return 1
    } catch {
      case e: NoSuchElementException =>
        return 0
    }
  }

  /**
    * Fetch rows from database as an array of arrays.
    *
    * @param tables tables to fetch from.
    * @param columns columns to fetch.
    * @param condition WHERE statements to use in fetch.
    * @param joins JOIN statements to use in fetch.
    * @return an array containing the fetched rows as arrays, 
    * empty array if no matched rows were found.
    */
  def fetchArray(
    tables: String,
    columns: String,
    condition: String = null,
    joins: String = null
  ): Array[Array[Any]] = {
    return resultSetToArray(
      fetchData(tables, columns, condition, joins)
    )
  }

  /**
    * Fetch a row from database as an array.
    *
    * @param tables tables to fetch from.
    * @param columns columns to fetch.
    * @param condition WHERE statements to use in fetch.
    * @param joins JOIN statements to use in fetch.
    * @return an array containing the fetched row, null if no matched row was found.
    */
  def fetchArrayRow(
    tables: String, 
    columns: String,
    condition: String = null,
    joins: String = null
  ): Array[Any] = {
    try {
      return resultSetToArray(
        fetchData(tables, columns, condition, joins)
      ).head
    } catch {
      case e: NoSuchElementException =>
        return null
    }
  }

  /**
    * Fetch rows from database as an array of maps.
    *
    * @param tables tables to fetch from.
    * @param columns columns to fetch.
    * @param condition WHERE statements to use in fetch.
    * @param joins JOIN statements to use in fetch.
    * @return an array containing the fetched rows as maps, 
    * empty array if no matched rows were found.
    */
  def fetchMapArray(
    tables: String,
    columns: String,
    condition: String = null, 
    joins: String = null
  ): Array[Map[String, Any]] = {
    return resultSetToMapArray(
      fetchData(tables, columns, condition, joins)
    )
  }

  /**
    * Fetch a row from database as a map.
    *
    * @param tables tables to fetch from.
    * @param columns columns to fetch.
    * @param condition WHERE statements to use in fetch.
    * @param joins JOIN statements to use in fetch.
    * @return a map containing the fetched row, null if no matched row was found.
    */
  def fetchMapRow(
    tables: String,
    columns: String,
    condition: String = null, 
    joins: String = null
  ): Map[String, Any] = {
    try {
      return resultSetToMapArray(
        fetchData(tables, columns, condition, joins)
      ).head
    } catch {
      case e: NoSuchElementException =>
        return null
    }
  }

  /**
    * Convert ResultSet to array of arrays
    *
    * @param result ResultSet containing fetched rows.
    * @return array of rows in array form containing the same data.
    */
  def resultSetToArray(result: ResultSet): Array[Array[Any]] = {
    if (result == null) 
      return Array()

    val metadata = result.getMetaData()
    val colLength = metadata.getColumnCount()

    return new Iterator[Array[Any]] {
      def hasNext = result.next()
      def next = {
        for (i <- 1 to colLength)
          yield convertValue(result, metadata.getColumnType(i), i)
      }.toArray
    }.toArray
  }

  /**
    * Convert ResultSet to array of maps.
    *
    * @param result ResultSet containing fetched rows.
    * @return array of maps contraining the same data.
    */
  def resultSetToMapArray(result: ResultSet): Array[Map[String, Any]] = {
    if (result == null)
      return Array()

    val metadata = result.getMetaData()
    val colLength = metadata.getColumnCount()

    return new Iterator[Map[String, Any]] {
      def hasNext = result.next()
      def next = {
        for (i <- 1 to colLength)
          yield metadata.getColumnName(i) -> 
            convertValue(result, metadata.getColumnType(i), i)
      }.toMap
    }.toArray
  }

  /**
    * Convert value of a ResultSet row into Any type.
    *
    * @param result ResultSet containing fetched rows.
    * @param colType the type of the value column.
    * @param colIndex the index of the value column.
    * @return the value as Any type.
    */
  def convertValue(result: ResultSet, colType: Int, colIndex: Int): Any = {
    if (colType == Types.INTEGER || colType == Types.BIGINT)
      result.getInt(colIndex)
    else if (colType == Types.TIMESTAMP)
      result.getTimestamp(colIndex)
    else
      result.getString(colIndex)
  }
}