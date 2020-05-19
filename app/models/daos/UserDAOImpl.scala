package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.Inject
import models.User
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

/**
 * Give access to the user object.
 */
class UserDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends UserDAO with DAOSlick {
  import profile.api._

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo): Future[Option[User]] = {
    val userQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.loginInfoId === dbLoginInfo.id)
      dbUser <- slickUsers.filter(_.id === dbUserLoginInfo.userID)
    } yield dbUser
    db.run(userQuery.result.headOption).map { dbUserOption =>
      dbUserOption.map { user =>
        User(user.id, user.firstName, user.lastName, user.email, user.password)
      }
    }
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: Int): Future[Option[User]] = {
    val query = slickUsers.filter(_.id === userID)

    db.run(query.result.headOption).map { resultOption =>
      resultOption.map(DBUser.toUser)
    }
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User] = {
    db.run(slickUsers.insertOrUpdate(DBUser(user.id, user.firstName, user.lastName, user.email, user.password))).map(_ => user)
  }


  /**
   * Finds a user by its email
   *
   * @param email email of the user to find
   * @return The found user or None if no user for the given login info could be found
   */
  def findByEmail(email: String): Future[Option[User]] = {
    db.run(slickUsers.filter(_.email === email).take(1).result.headOption).map(_ map DBUser.toUser)
  }
}