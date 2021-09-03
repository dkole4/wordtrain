package backend

import java.sql.Types
import com.twitter.finagle.postgres._
import com.twitter.finagle.postgres.generic._


/** Handler of database related operations. */
class DBHandler {
  val client = new Client()

  def endSession() = client.endSession()

  /**
    * Execute a query using the connected database.
    *
    * @param query query to execute.
    */
  def fetch[T](query: Query[T]): Option[Seq[T]] = {
    try {
      client.fetch(query)
    } catch {
      case e: Throwable => 
        println(e)
        endSession()
        Option( Seq[T]() )
    }
  }

  def fetchRow[T](query: Query[T]): Option[T] = {
    val res = fetch[T](query)
    if (res.isDefined && res.get.length > 0)
      Option(res.get.head)
    else
      Option.empty
  }
  

  def execute[T](query: Query[T]): Boolean = {
    try {
      client.execute(query)
    } catch {
      case e: Throwable => 
        println(e)
        client.endSession()
        false
    }
  }
}