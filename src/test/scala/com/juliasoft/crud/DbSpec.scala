package com.juliasoft.crud

import doobie._
import cats.effect._

import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import java.util.UUID

class DbSpec
    extends funsuite.AnyFunSuite
    with matchers.must.Matchers
    with doobie.scalatest.IOChecker
    with ScalaCheckPropertyChecks {

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val transactor = Transactor.fromDriverManager[IO](
    "org.h2.Driver",
    "jdbc:h2:mem:test_db;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL;AUTO_RECONNECT=FALSE;DB_CLOSE_DELAY=-1;" +
      "INIT=RUNSCRIPT FROM 'classpath:device.sql'",
    "sa",
    ""
  )

  test("Service select nothing from empty DB") {
    val svc = new DeviceService(transactor)
    forAll { (i: UUID) =>
      svc.findByCamId(i.toString).unsafeRunSync() mustBe empty
      svc.findByImei(i.toString).unsafeRunSync() mustBe empty
      svc.findOne(i.toString).unsafeRunSync() mustBe empty
    }
  }

  test("Service findAll return empty list from empty DB") {
    val svc = new DeviceService(transactor)
    svc.findAll().unsafeRunSync() mustBe Nil
  }

  test("Service delete nothing from empty DB") {
    val svc = new DeviceService(transactor)
    forAll { (s: String) =>
      svc.delete(s).unsafeRunSync() mustBe 0
    }
  }

  test("Service insert, modify and delete Devices") {
    val log = java.util.logging.Logger.getLogger(getClass.getName)
    val svc = new DeviceService(transactor)
    forAll(DeviceGen.newDevice, DeviceGen.newDevice) { (dev: Device, upd: Device) =>
      log.info(s"Dev: $dev")
      // check insert
      val id = svc.insert(dev).unsafeRunSync()
      id must not be empty
      log.info(s"ID: $id")
      // check inserted
      svc.findAll().unsafeRunSync().size must be > 0
      //
      svc.findOne(id).unsafeRunSync() must not be empty
      svc.findByCamId(dev.camId).unsafeRunSync() must not be empty
      svc.findByImei(dev.imei).unsafeRunSync() must not be empty

      // check update
      val idx     = id
      val updated = upd.copy(id = Some(idx))
      log.info(s"UPD: $updated")
      svc.update(updated).unsafeRunSync() mustBe 1
      // check updated
      svc.findByCamId(updated.camId).unsafeRunSync() must not be empty
      svc.findByImei(updated.imei).unsafeRunSync() must not be empty
      svc.findOne(id).unsafeRunSync() must not be empty

      svc.findByCamId(dev.camId).unsafeRunSync() mustBe empty
      svc.findByImei(dev.imei).unsafeRunSync() mustBe empty

      // check delete
      svc.delete(id).unsafeRunSync() mustBe 1
      // check deleted
      svc.findByCamId(updated.camId).unsafeRunSync() mustBe empty
      svc.findByImei(updated.imei).unsafeRunSync() mustBe empty
      svc.findOne(id).unsafeRunSync() mustBe empty
    }
  }
}
