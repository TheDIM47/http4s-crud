package com.juliasoft.crud

import java.sql.Timestamp
import java.util.{Date, UUID}

import cats.effect.IO
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import org.http4s._
import org.http4s.implicits._
import org.scalatest._

class DeviceSpec extends flatspec.AnyFlatSpec with matchers.must.Matchers {
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  implicit val transactor = Transactor.fromDriverManager[IO](
    "org.h2.Driver",
    "jdbc:h2:mem:test_devices;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL;AUTO_RECONNECT=FALSE;DB_CLOSE_DELAY=-1;" +
      "INIT=RUNSCRIPT FROM 'classpath:device.sql'",
    "sa",
    ""
  )

  val svc = new DeviceService[IO](transactor)
  val dev = Devices.impl[IO](svc)

  "Device Routes" should "return empty list on empty DB" in {
    val req = Request[IO](Method.GET, uri"/devices")
    val res = DeviceRoutes.deviceRoutes(dev).orNotFound(req).unsafeRunSync()
    res.status mustBe Status.Ok
    res.as[String].unsafeRunSync() mustBe "[]"
  }

  it should "return 404 if not found by id" in {
    val req = Request[IO](Method.GET, uri"/device/-1")
    val res = DeviceRoutes.deviceRoutes(dev).orNotFound(req).unsafeRunSync()
    res.status mustBe Status.NotFound
    res.as[String].unsafeRunSync() mustBe empty
  }

  it should "return 404 if not found by imei" in {
    val req = Request[IO](Method.GET, Uri.unsafeFromString("/device/imei/" + UUID.randomUUID().toString()))
    val res = DeviceRoutes.deviceRoutes(dev).orNotFound(req).unsafeRunSync()
    res.status mustBe Status.NotFound
    res.as[String].unsafeRunSync() mustBe empty
  }

  it should "return 404 if not found by camId" in {
    val req = Request[IO](Method.GET, Uri.unsafeFromString("/device/camid/" + UUID.randomUUID().toString()))
    val res = DeviceRoutes.deviceRoutes(dev).orNotFound(req).unsafeRunSync()
    res.status mustBe Status.NotFound
    res.as[String].unsafeRunSync() mustBe empty
  }

  it should "return zero on delete non-existing" in {
    val req = Request[IO](Method.DELETE, uri"/device/-1")
    val res = DeviceRoutes.deviceRoutes(dev).orNotFound(req).unsafeRunSync()
    res.status mustBe Status.Ok
    res.as[String].unsafeRunSync() mustBe "0"
  }

  it should "process with valid workflow" in {
    val v = Device(
      id = None,
      created = new Timestamp(new Date().getTime()),
      version = 1L,
      camId = UUID.randomUUID.toString,
      description = Some(UUID.randomUUID.toString),
      imei = UUID.randomUUID.toString,
      name = UUID.randomUUID.toString,
      serial = Some(UUID.randomUUID.toString),
      phone = Some(UUID.randomUUID.toString),
      isHidden = false
    )

    import com.juliasoft.crud.DeviceEncoders._

    // insert
    val req1 = Request[IO](Method.POST, uri"/device").withEntity(v)
    val res1 = DeviceRoutes.deviceRoutes(dev).orNotFound(req1).unsafeRunSync()
    res1.status mustBe Status.Ok
    // check result is UUID (Note: Remove quotes !)
    val idx: String = res1.as[String].unsafeRunSync().replaceAll("\"", "")
    println(s"IDX: $idx")
    idx must not be empty
    val uuid = UUID.fromString(idx)
    println(s"UUID: $uuid")

    // findById
    val req2 = Request[IO](Method.GET, Uri.unsafeFromString(s"/device/$idx"))
    val res2 = DeviceRoutes.deviceRoutes(dev).orNotFound(req2).unsafeRunSync()
    res2.status mustBe Status.Ok
    res2.as[Device].unsafeRunSync() mustBe v.copy(id = Some(idx))

    // findByImei
    val req3 = Request[IO](Method.GET, Uri.unsafeFromString(s"/device/imei/${v.imei}"))
    val res3 = DeviceRoutes.deviceRoutes(dev).orNotFound(req3).unsafeRunSync()
    res3.status mustBe Status.Ok
    res3.as[Device].unsafeRunSync() mustBe v.copy(id = Some(idx))

    // findByCamId
    val req4 = Request[IO](Method.GET, Uri.unsafeFromString(s"/device/camid/${v.camId}"))
    val res4 = DeviceRoutes.deviceRoutes(dev).orNotFound(req4).unsafeRunSync()
    res4.status mustBe Status.Ok
    res4.as[Device].unsafeRunSync() mustBe v.copy(id = Some(idx))

    // findAll
    val req5 = Request[IO](Method.GET, uri"/devices")
    val res5 = DeviceRoutes.deviceRoutes(dev).orNotFound(req5).unsafeRunSync()
    res5.status mustBe Status.Ok
    res5.as[List[Device]].unsafeRunSync() mustBe List(v.copy(id = Some(idx)))

    val v2 = v.copy(
      id = Some(idx),
      created = new Timestamp(new Date().getTime() - 42 * 1000),
      version = 42L,
      camId = UUID.randomUUID.toString,
      description = Some(UUID.randomUUID.toString),
      imei = UUID.randomUUID.toString,
      name = UUID.randomUUID.toString,
      serial = Some(UUID.randomUUID.toString),
      phone = Some(UUID.randomUUID.toString),
      isHidden = !v.isHidden
    )

    // update
    val req6 = Request[IO](Method.PUT, uri"/device").withEntity(v2)
    val res6 = DeviceRoutes.deviceRoutes(dev).orNotFound(req6).unsafeRunSync()
    res6.status mustBe Status.Ok
    res6.as[String].unsafeRunSync() mustBe "1"

    // findById (check updated)
    val req7 = Request[IO](Method.GET, Uri.unsafeFromString(s"/device/$idx"))
    val res7 = DeviceRoutes.deviceRoutes(dev).orNotFound(req7).unsafeRunSync()
    res7.status mustBe Status.Ok
    res7.as[Device].unsafeRunSync() mustBe v2

    // delete
    val req8 = Request[IO](Method.DELETE, Uri.unsafeFromString(s"/device/$idx"))
    val res8 = DeviceRoutes.deviceRoutes(dev).orNotFound(req8).unsafeRunSync()
    res8.status mustBe Status.Ok
    res8.as[String].unsafeRunSync() mustBe "1"

    // check deleted
    val req9 = Request[IO](Method.GET, uri"/devices")
    val res9 = DeviceRoutes.deviceRoutes(dev).orNotFound(req9).unsafeRunSync()
    res9.status mustBe Status.Ok
    res9.as[List[Device]].unsafeRunSync() mustBe empty
  }

}
