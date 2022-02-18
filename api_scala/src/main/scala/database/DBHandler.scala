package backend

import com.twitter.util.{Future, FuturePool, Await}
import com.twitter.finagle.postgres._
import com.twitter.finagle.postgres.generic._


/** Handler of database related operations. */
object DBHandler {
  /**
    * Execute a query using the connected database.
    *
    * @param query query to execute.
    */
  def fetch[T](query: Query[T]): Future[Seq[T]] = {
    val client = new Client()
    try {
      val res = client.fetch(query)
      client.endSession()
      res
    } catch {
      case e: Throwable => 
        println(e)
        client.endSession()
        Future(Seq[T]())
    }
  }

  def fetchRow[T](query: Query[T]): Future[Option[T]] = {
    fetch[T](query) flatMap { result => 
      if (result.length > 0)
        Future(Option(result.head))
      else
        Future(Option.empty)
    }
  }
  

  def execute[T](query: Query[T]): Future[Boolean] = {
    val client = new Client()
    try {
      val res = client.execute(query)
      client.endSession()
      res
    } catch {
      case e: Throwable => 
        println(e)
        client.endSession()
        Future(false)
    }
  }
}