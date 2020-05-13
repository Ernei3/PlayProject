package models

import play.api.libs.json.Json

case class Payment(id: Int, number: String, name: String, date: String, code: String, order: Int )

object Payment {
  implicit val paymentFormat = Json.format[Payment]
}