package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf

trait DBTableDefinitions {

  protected val profile: JdbcProfile
  import profile.api._

  case class DBUserRole(id: Int, name: String)

  case class DBUser(id: Int,
                    email: String,
                    firstName: String,
                    lastName: String,
                    password: String)

  object DBUser {
    def toUser(u: DBUser): User = User(u.id, u.firstName, u.lastName, u.email, u.password)
    def fromUser(u: User): DBUser = DBUser(u.id, u.firstName, u.lastName, u.email, u.password)
  }

  class Users(tag: Tag) extends Table[DBUser](tag, Some("auth"), "user") {
    //import MyPostgresProfile.api._

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def email = column[String]("email")
    def firstName = column[String]("firstName")
    def lastName = column[String]("lastName")
    def password = column[String]("password")
    def * = (id, firstName, lastName, email, password) <> ((DBUser.apply _).tupled, DBUser.unapply)
  }

  case class DBLoginInfo(id: Option[Int], providerID: String, providerKey: String)
  object DBLoginInfo {
    def fromLoginInfo(loginInfo: LoginInfo): DBLoginInfo = DBLoginInfo(None, loginInfo.providerID, loginInfo.providerKey)
    def toLoginInfo(dbLoginInfo: DBLoginInfo): LoginInfo = LoginInfo(dbLoginInfo.providerID, dbLoginInfo.providerKey)
  }

  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, Some("auth"), "login_info") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def providerID = column[String]("provider_id")
    def providerKey = column[String]("provider_key")
    def * = (id.?, providerID, providerKey) <> ((DBLoginInfo.apply _).tupled, DBLoginInfo.unapply)
  }

  case class DBUserLoginInfo(
                              userID: Int,
                              loginInfoId: Int
                            )

  class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, Some("auth"), "user_login_info") {
    def userID = column[Int]("user_id")
    def loginInfoId = column[Int]("login_info_id")
    def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  case class DBOAuth2Info(
                           id: Option[Int],
                           accessToken: String,
                           tokenType: Option[String],
                           expiresIn: Option[Int],
                           refreshToken: Option[String],
                           loginInfoId: Int
                         )

  class OAuth2Infos(tag: Tag) extends Table[DBOAuth2Info](tag, Some("auth"), "oauth2_info") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def accessToken = column[String]("access_token")
    def tokenType = column[Option[String]]("token_type")
    def expiresIn = column[Option[Int]]("expires_in")
    def refreshToken = column[Option[String]]("refresh_token")
    def loginInfoId = column[Int]("login_info_id")
    def * = (id.?, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> (DBOAuth2Info.tupled, DBOAuth2Info.unapply)
  }


  // table query definitions
  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickUserLoginInfos = TableQuery[UserLoginInfos]
  val slickOAuth2Infos = TableQuery[OAuth2Infos]

  // queries used in multiple places
  def loginInfoQuery(loginInfo: LoginInfo): Query[LoginInfos, DBLoginInfo, Seq] =
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)

}