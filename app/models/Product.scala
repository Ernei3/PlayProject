package models

import play.api.libs.json.Json

case class Product(id: Int, name: String, description: String, subcategory: Int)

object Product {
  implicit val productFormat = Json.format[Product]
}