package models

import com.mohiva.play.silhouette.api.Identity
import play.api.libs.json._


case class User(id: Int,
                firstName: String,
                email: String,
                lastName: String,
                password: String,
                role: UserRoles.UserRole) extends Identity