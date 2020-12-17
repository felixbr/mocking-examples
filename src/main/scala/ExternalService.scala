import cats.effect._

/**
  * This is our client for an external service. In our tests we don't want to use the real service, so we mock it.
  */
trait ExternalService {
  def sendMessage(msg: String): IO[Unit]

  def fetchUserName(userId: Int): IO[String]
}
