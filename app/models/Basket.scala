package models

import play.api.libs.json.Json

case class Basket(id: Int, user: Int, quantity: Int, product: Int)

object Basket {
  implicit val basketFormat = Json.format[Basket]
}