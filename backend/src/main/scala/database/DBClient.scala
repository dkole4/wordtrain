package backend

import com.twitter.util.{Future, FuturePool, Await}
import com.twitter.finagle.Postgres
import com.twitter.finagle.postgres._
import com.twitter.finagle.postgres.generic._


/** Database client class. */
class Client {

  /**
    * Class representing datad neede for 
    * establishing a database connection.
    *
    * @param dbname name of the database.
    * @param dbhost host of the database.
    * @param dbuser user of the database to log in as.
    * @param dbpasswd password of the user.
    */
  case class ConnectionData(
    dbname: String,
    dbhost: String,
    dbuser: String,
    dbpasswd: String
  )

  /** Variable for storing database connection. */
  val connection = connect()

  /**
    * Start a session by connecting to a database.
    */
  def connect(): Option[PostgresClientImpl] = {
    val params = getConnectionData()
    println(params)
    try {
      Option(
        Postgres.Client()
          .withCredentials(params.dbuser, Some(params.dbpasswd))
          .database(params.dbname)
          .withSessionPool.maxSize(3)
          .withBinaryResults(true)
          .withBinaryParams(true)
          .newRichClient(s"${params.dbhost}:5432")
      )
    } catch {
      case _: Throwable => 
        println("Connection failed.")
        Option.empty
    }
  }

  /**
    * End the session by closing the database connection.
    *
    * @return 1 if operation was successful, 0 otherwise.
    */
  def endSession(): Int = {
    if (!connection.isDefined) 0
    
    try {
      Await.result { connection.get.close() }
      1
    } catch {
      case _: Throwable => 0
    }
  }

  /**
    * Get the connection data from environment variables
    * and return it as a ConnectionData object.
    *
    * @return ConnectionData object containing necessary
    * data for connection establishment.
    */
  def getConnectionData(): ConnectionData = {
    val name = sys.env("DB_NAME");
    val host = sys.env("DB_HOST");
    val user = sys.env("DB_USER");
    val passwd = sys.env("DB_PASSWD");
    ConnectionData(name, host, user, passwd);
  }

  /**
    * Perform a fetch operation using 
    * established database connection.
    *
    * @param query query to execute in fetching.
    * @return ResultSet containing fetched rows.
    */
  def fetch[T](query: Query[T]): Option[Seq[T]] = {
    if (!connection.isDefined)
      Option.empty
    else {
      Option( Await.result { query.run(connection.get) } )
    }
  }

  /**
    * Perform an execute operation using
    * established database connection.
    *
    * @param query query to execute.
    */
  def execute[T](query: Query[T]): Boolean = {
    if (!connection.isDefined)
      false 
    else
      Await.result { query.exec(connection.get) } == 1
  }
}