package models

import play.api.libs.json._

case class Order(id: Int, user: Int, status: String)

object Order {
  implicit val orderFormat = Json.format[Order]
}