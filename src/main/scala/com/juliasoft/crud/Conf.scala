package com.juliasoft.crud

case class DbConf(driver: String, url: String, user: String, pass: String)
case class ServerConf(ip: String, port: Int)
case class Conf(db: DbConf, server: ServerConf)

