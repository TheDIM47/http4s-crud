package com.juliasoft.crud

import cats.effect.{ ExitCode, IO, IOApp }
import com.typesafe.config.ConfigFactory
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import io.circe.config.parser
import io.circe.generic.auto._

object Main extends IOApp {
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  def run(args: List[String]) = {
    val config = ConfigFactory.load()
    for {
      cfg       <- parser.decodeF[IO, Conf](config)
      transactor = Transactor.fromDriverManager[IO](cfg.db.driver, cfg.db.url, cfg.db.user, cfg.db.pass)
      rc        <- DeviceServer
                     .stream[IO](transactor, cfg.server)
                     .compile
                     .drain
                     .as(ExitCode.Success)
    } yield rc
  }
}
