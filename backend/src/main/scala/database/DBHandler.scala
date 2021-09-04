package backend

import com.twitter.finagle.postgres._
import com.twitter.finagle.postgres.generic._


/** Handler of database related operations. */
object DBHandler {
  /**
    * Execute a query using the connected database.
    *
    * @param query query to execute.
    */
  def fetch[T](query: Query[T]): Option[Seq[T]] = {
    val client = new Client()
    try {
      val res = client.fetch(query)
      client.endSession()
      res
    } catch {
      case e: Throwable => 
        println(e)
        client.endSession()
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
    val client = new Client()
    try {
      val res = client.execute(query)
      client.endSession()
      res
    } catch {
      case e: Throwable => 
        println(e)
        client.endSession()
        false
    }
  }
}