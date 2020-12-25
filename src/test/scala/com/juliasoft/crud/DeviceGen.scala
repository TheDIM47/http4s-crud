package com.juliasoft.crud

import org.scalacheck.Gen
import java.sql.Timestamp

object DeviceGen {
  val deviceWithId = deviceGen(true)
  val newDevice = deviceGen(false)

  private def deviceGen(withId: Boolean): Gen[Device] =
    for {
      id          <- if (withId) Gen.some(Gen.uuid.map(_.toString())) 
                     else Gen.oneOf(Seq(Option.empty[String]))
      created     <- Gen.calendar.map(t => new Timestamp(t.getTimeInMillis()))
      version     <- Gen.choose[Long](0, Int.MaxValue)
      camId       <- Gen.numStr.filterNot(_.isEmpty).map(_.take(10))
      description <- Gen.some(Gen.alphaNumStr.map(_.take(50)))
      imei        <- Gen.numStr.filterNot(_.isEmpty).map(_.take(15))
      name        <- Gen.alphaNumStr.map(_.take(20))
      serial      <- Gen.some(Gen.numStr.map(_.take(15)))
      phone       <- Gen.some(Gen.numStr.map(_.take(12)))
      isHidden    <- Gen.prob(0.05)
    } yield Device(
      id,
      created,
      version,
      camId,
      description,
      imei,
      name,
      serial,
      phone,
      isHidden
    )
}
