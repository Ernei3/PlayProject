package models

import play.api.libs.json.Json

case class Review(id: Int, title: String, content: String, product: Int)

object Review {
  implicit val reviewFormat = Json.format[Review]
}
