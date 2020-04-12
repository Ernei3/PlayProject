package models

import play.api.libs.json.Json

case class OrderProd(id: Int, name: String, price: Int, quantity: Int, order: Int )

object OrderProd {
  implicit val basketFormat = Json.format[Basket]
}
