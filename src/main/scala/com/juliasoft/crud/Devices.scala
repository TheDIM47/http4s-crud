package com.juliasoft.crud

trait Devices[F[_]] {
  def insert(dev: Device): F[String]
  def update(dev: Device): F[Int]
  def delete(id: String): F[Int]

  def findAll(): F[List[Device]]

  def findOne(id: String): F[Option[Device]]
  def findByCamId(camId: String): F[Option[Device]]
  def findByImei(imei: String): F[Option[Device]]
}

object Devices {
  implicit def apply[F[_]](implicit ev: Devices[F]): Devices[F] = ev

  final class DevicesImpl[F[_]](svc: DeviceService[F]) extends Devices[F] {
    def insert(dev: Device): F[String]                = svc.insert(dev)
    def update(dev: Device): F[Int]                   = svc.update(dev)
    def delete(id: String): F[Int]                    = svc.delete(id)
    def findAll(): F[List[Device]]                    = svc.findAll()
    def findOne(id: String): F[Option[Device]]        = svc.findOne(id)
    def findByCamId(camId: String): F[Option[Device]] = svc.findByCamId(camId)
    def findByImei(imei: String): F[Option[Device]]   = svc.findByImei(imei)
  }

  def impl[F[_]](svc: DeviceService[F]): Devices[F] = new DevicesImpl(svc)
}
