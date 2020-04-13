package models

import play.api.libs.json.Json

case class OrderAd(id: Int, country: String, city: String, street: String, number: String, order: Int )

object OrderAd {
  implicit val orderAdFormat = Json.format[OrderAd]
}