package backend

import java.sql.{Connection, DriverManager, ResultSet}


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
  var connection: Connection = null

  /**
    * Start a session by connecting to a database.
    *
    * @param params parameters to use in connection establishment.
    */
  def startSession(params: ConnectionData = null) {
    classOf[org.postgresql.Driver]
    val connParams = if (params == null) getConnectionData() else params
    val connString = "jdbc:postgresql://%s/%s".format(
      connParams.dbhost, connParams.dbname)
    try {
      connection = DriverManager.getConnection(
        connString, connParams.dbuser, connParams.dbpasswd)
    } catch {
      case _: Throwable => 
        println("Connection failed.")
    }
  }

  /**
    * End the session by closing the database connection.
    *
    * @return 1 if operation was successful, 0 otherwise.
    */
  def endSession(): Int = {
    if (connection == null)
      return 0
    
    try {
      connection.close()
      return 1
    } catch {
      case _: Throwable => return 0
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
    return ConnectionData(name, host, user, passwd);
  }

  /**
    * Perform a fetch operation using 
    * established database connection.
    *
    * @param query query to execute in fetching.
    * @return ResultSet containing fetched rows.
    */
  def fetch(query: String): ResultSet = {
    if (connection == null)
      return null;
    else 
      return connection
        .createStatement(
          ResultSet.TYPE_SCROLL_INSENSITIVE, 
          ResultSet.CONCUR_READ_ONLY)
        .executeQuery(query)
  }

  /**
    * Perform an execute operation using
    * established database connection.
    *
    * @param query query to execute.
    */
  def execute(query: String) {
    if (connection == null)
      println("No connection was established.")
    else 
      connection
        .createStatement(
          ResultSet.TYPE_FORWARD_ONLY, 
          ResultSet.CONCUR_UPDATABLE)
        .executeUpdate(query)
  }
}