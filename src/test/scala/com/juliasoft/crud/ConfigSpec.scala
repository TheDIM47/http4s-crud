package com.juliasoft.crud

import cats.effect.IO
import io.circe.generic.auto._
import io.circe.config.parser
import org.scalatest._
import com.typesafe.config.ConfigFactory

class ConfigSpec extends funsuite.AnyFunSuite with matchers.must.Matchers {

  test("Parse valid config") {
    val str = """db.driver="org.h2.Driver"
                |db.url="jdbc:h2:mem:test"
                |db.user="sa"
                |db.pass="pwd"
                |server.ip="0.0.0.0"
                |server.port=8888""".stripMargin

    val config = ConfigFactory.parseString(str)
    val cfg: IO[Conf] = parser.decodeF[IO, Conf](config)

    cfg.unsafeRunSync() mustBe Conf(
      db = DbConf(
        driver="org.h2.Driver", 
        url="jdbc:h2:mem:test",
        user="sa",
        pass="pwd"
      ),
      server = ServerConf(
        ip="0.0.0.0", 
        port=8888
      )
    )
  }
}
