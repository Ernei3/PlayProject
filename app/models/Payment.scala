package models

import play.api.libs.json.Json

case class Payment(id: Int, number: String, name: String, date: String, code: Int, order: Int )

object Payment {
  implicit val paymentFormat = Json.format[Payment]
}