package com.juliasoft.crud

import cats.effect.{ ConcurrentEffect, Timer }
import doobie._
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

object DeviceServer {

  def stream[F[_]: ConcurrentEffect](transactor: Transactor[F], server: ServerConf)(implicit
      T: Timer[F]
  ): Stream[F, Nothing] = {
    for {
      _ <- BlazeClientBuilder[F](global).stream // client <-

      svc          = new DeviceService[F](transactor)
      devicesAlg   = Devices.impl[F](svc)
      httpApp      = (DeviceRoutes.deviceRoutes[F](devicesAlg)).orNotFound
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F](global)
                    .bindHttp(server.port, server.ip)
                    .withHttpApp(finalHttpApp)
                    .serve
    } yield exitCode
  }.drain
}
