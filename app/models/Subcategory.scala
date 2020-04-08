package models

import play.api.libs.json._

case class Subcategory(id: Int, name: String, category: Int)

object Subcategory {
  implicit val subcategoryFormat = Json.format[Subcategory]
}
