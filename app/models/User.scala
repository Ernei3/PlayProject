package models

import com.mohiva.play.silhouette.api.Identity
import play.api.libs.json._


case class User(id: Int,
                email: String,
                firstName: String,
                lastName: String,
                password: String) extends Identity


object User {
  implicit val userFormat: OFormat[User] = Json.format[User]
}