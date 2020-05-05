package models

import play.api.libs.json.Json

case class Wishlist(id: Int, user: Int, quantity: Int, product: Int)

object Wishlist {
  implicit val wishlistFormat = Json.format[Wishlist]
}
