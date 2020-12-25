package com.juliasoft.crud

import cats.effect.Sync
import cats.implicits._

import org.http4s._
import org.http4s.dsl.Http4sDsl

object DeviceRoutes {
  import DeviceEncoders._

  import org.http4s.circe.CirceEntityCodec._

  def deviceRoutes[F[_]: Sync](D: Devices[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "devices" =>
        for {
          ds <- D.findAll()
          rs <- Ok(ds)
        } yield rs

      case GET -> Root / "device" / id =>
        for {
          md <- D.findOne(id)
          rs <- if (md.isDefined) Ok(md) else NotFound()
        } yield rs

      case GET -> Root / "device" / "camid" / camId =>
        for {
          md <- D.findByCamId(camId)
          rs <- if (md.isDefined) Ok(md) else NotFound()
        } yield rs

      case GET -> Root / "device" / "imei" / imei =>
        for {
          md <- D.findByImei(imei)
          rs <- if (md.isDefined) Ok(md) else NotFound()
        } yield rs

      case req @ POST -> Root / "device" =>
        for {
          dv <- req.as[Device]
          id <- D.insert(dv)
          rs <- Ok(id)
        } yield rs

      case req @ PUT -> Root / "device" =>
        for {
          dv <- req.as[Device]
          rx <- D.update(dv)
          rs <- Ok(rx)
        } yield rs

      case DELETE -> Root / "device" / id =>
        for {
          rx <- D.delete(id)
          rs <- Ok(rx)
        } yield rs
    }
  }
}
