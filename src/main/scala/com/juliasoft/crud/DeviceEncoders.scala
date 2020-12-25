package com.juliasoft.crud

import java.sql.Timestamp

import cats.effect.IO
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder, _}
import org.http4s.circe._
import org.http4s.{EntityDecoder, EntityEncoder}
import java.util.UUID

object DeviceEncoders {
  implicit val TimestampFormat: Encoder[Timestamp] with Decoder[Timestamp] =
    new Encoder[Timestamp] with Decoder[Timestamp] {
      override def apply(a: Timestamp): Json                    = Encoder.encodeLong.apply(a.getTime)
      override def apply(c: HCursor): Decoder.Result[Timestamp] =
        Decoder.decodeLong.map(s => new Timestamp(s)).apply(c)
    }

  implicit val UUIDFormat: Encoder[UUID] with Decoder[UUID] =
    new Encoder[UUID] with Decoder[UUID] {
      override def apply(a: UUID): Json                    = Encoder.encodeString.apply(a.toString)
      override def apply(c: HCursor): Decoder.Result[UUID] =
        Decoder.decodeString.map(s => UUID.fromString(s)).apply(c)
    }

  implicit val deviceEncoder: Encoder[Device] = deriveEncoder[Device]
  implicit val deviceDecoder: Decoder[Device] = deriveDecoder[Device]

  implicit def deviceEntityDecoder: EntityDecoder[IO, Device] = jsonOf[IO, Device]
  implicit def deviceEntityListDecoder: EntityDecoder[IO, List[Device]] = jsonOf[IO, List[Device]]
  
  implicit def deviceEntityEncoder: EntityEncoder[IO, Device] = jsonEncoderOf[IO, Device]
}
