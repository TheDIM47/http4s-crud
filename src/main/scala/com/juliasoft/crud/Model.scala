package com.juliasoft.crud

import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.util.query.Query0

import cats.effect._

import java.sql.Timestamp
import java.util.UUID

case class Device(
    id: Option[String],
    created: Timestamp,
    version: Long,
    camId: String,
    description: Option[String],
    imei: String,
    name: String,
    serial: Option[String],
    phone: Option[String],
    isHidden: Boolean
)

final class DeviceService[F[_]: Sync](trx: Transactor[F]) {
  def insert(device: Device): F[String] = {
    val newId = UUID.randomUUID().toString()
    Queries
      .insert(device.copy(id = Some(newId)))
      .run
      .map(_ => newId)
      .transact(trx)
  }

  def update(device: Device): F[Int] =
    Queries
      .update(device)
      .run
      .transact(trx)

  def delete(id: String): F[Int] =
    Queries
      .delete(id)
      .run
      .transact(trx)

  def findAll(): F[List[Device]] =
    Queries
      .findAll()
      .to[List]
      .transact(trx)

  def findOne(id: String): F[Option[Device]] =
    Queries
      .findOne(id)
      .option
      .transact(trx)

  def findByCamId(camId: String): F[Option[Device]] =
    Queries
      .findByCamId(camId)
      .option
      .transact(trx)

  def findByImei(imei: String): F[Option[Device]] =
    Queries
      .findByImei(imei)
      .option
      .transact(trx)

  private object Queries {
    implicit val logHandler = LogHandler.jdkLogHandler

    def insert(dev: Device): Update0 =
      sql"""|insert into device(id, created_date, version, cam_id, description, imei, name, serial_version, telephone, is_hidden)
            |values(${dev.id}, ${dev.created}, ${dev.version}, ${dev.camId}, ${dev.description}, 
            |  ${dev.imei}, ${dev.name}, ${dev.serial}, ${dev.phone}, ${dev.isHidden})
            |""".stripMargin.update

    def update(dev: Device): Update0 =
      sql"""|update device set
            |  created_date = ${dev.created},
            |  version = ${dev.version}, 
            |  cam_id = ${dev.camId}, 
            |  description = ${dev.description}, 
            |  imei = ${dev.imei}, 
            |  name = ${dev.name}, 
            |  serial_version = ${dev.serial}, 
            |  telephone = ${dev.phone}, 
            |  is_hidden = ${dev.isHidden} 
            |where id = ${dev.id.getOrElse("")}""".stripMargin.update

    def delete(id: String): Update0 =
      sql"delete from device where id=$id".update

    def findAll(): Query0[Device] =
      sql"""|select id, created_date, version, cam_id, description, imei, name, serial_version, telephone, is_hidden
            |from device""".stripMargin.query[Device]

    def findOne(id: String): Query0[Device] =
      sql"""|select id, created_date, version, cam_id, description, imei, name, serial_version, telephone, is_hidden
            |from device where id = ${id.toString}""".stripMargin.query[Device]

    def findByCamId(camId: String): Query0[Device] =
      sql"""|select id, created_date, version, cam_id, description, imei, name, serial_version, telephone, is_hidden
            |from device where cam_id = $camId""".stripMargin.query[Device]

    def findByImei(imei: String): Query0[Device] =
      sql"""|select id, created_date, version, cam_id, description, imei, name, serial_version, telephone, is_hidden
            |from device where imei = $imei""".stripMargin.query[Device]
  }

}
