package models

import play.api.libs.json._

case class Order(id: Int, user: String, status: String, address: Int)

object Order {
  implicit val orderFormat = Json.format[Order]
}