import java.sql.Connection

object Db1 {
  def connect: Connection = {
    Class.forName("org.h2.Driver")
    java.sql.DriverManager.getConnection("jdbc:h2:~/test_dev")
  }
}
