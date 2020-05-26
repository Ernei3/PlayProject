package controllers

import javax.inject._
import models.{Product, ProductRepository, Wishlist, WishlistRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import models.auth.UserRoles
import utils.auth.{DefaultEnv, HasRole}


@Singleton
class WishlistController @Inject()(wishRepo: WishlistRepository, productRepo: ProductRepository,
                                   silhouette: Silhouette[DefaultEnv], cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val wishForm: Form[CreateWishForm] = Form {
    mapping(
      "user" -> nonEmptyText,
      "quantity" -> number,
      "product" -> number
    )(CreateWishForm.apply)(CreateWishForm.unapply)
  }

  val updateWishForm: Form[UpdateWishForm] = Form {
    mapping(
      "id" -> number,
      "user" -> nonEmptyText,
      "quantity" -> number,
      "product" -> number
    )(UpdateWishForm.apply)(UpdateWishForm.unapply)
  }


  def wishlist(userId: String) = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val produkty = productRepo.list()
    val prod = Await.result(produkty, Duration.Inf)

    val listy = wishRepo.getByUser(userId)
    listy.map( wishlists => Ok(views.html.wishlist(wishlists, prod, userId, updateWishForm)))
  }

  def allWishes = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val produkty = productRepo.list()
    val prod = Await.result(produkty, Duration.Inf)

    val listy = wishRepo.list()
    listy.map( wishlists => Ok(views.html.allWishes(wishlists, prod, updateWishForm)))
  }

  def addToWishlistMenu(productId: Int) = Action {implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.addToWishlistMenu(wishForm, productId))
  }


  def addToWishlist(productId: Int) = Action.async { implicit request =>

    wishForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.addToWishlistMenu(errorForm, productId))
        )
      },
      wishlist => {
        wishRepo.create(wishlist.user, wishlist.quantity, wishlist.product).map { _ =>
          Redirect(routes.WishlistController.addToWishlistMenu(productId)).flashing("success" -> "product added to wishlist")
        }
      }
    )
  }

  def updateWishlist = Action.async { implicit request =>
    val produkty = productRepo.list()
    val listy = wishRepo.list()

    val prod = Await.result(produkty, Duration.Inf)
    val wish = Await.result(listy, Duration.Inf)

    updateWishForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.allWishes(wish, prod, errorForm))
        )
      },
      wishlist => {
        wishRepo.update(wishlist.id, Wishlist(wishlist.id, wishlist.user, wishlist.quantity, wishlist.product)).map { _ =>
          Redirect(routes.WishlistController.wishlist(wishlist.user))
        }
      }
    )
  }

  def removeFromWishlist(id: Int) = Action {
    wishRepo.delete(id)
    Redirect("/allWishes")
  }




  def wishlistJson(userId: String) = silhouette.SecuredAction(HasRole(UserRoles.User)).async { securedRequest =>
    if(userId == securedRequest.identity.userID){
      val listy = wishRepo.getByUser(userId)
      listy.map( wishlists => Ok(Json.toJson(wishlists)))
    }else{
      Future.successful(Unauthorized)
    }

  }

  def allWishesJson = Action.async { implicit request =>
    val produkty = productRepo.list()
    val prod = Await.result(produkty, Duration.Inf)

    val listy = wishRepo.list()
    listy.map( wishlists => Ok(Json.toJson(wishlists, prod)))
  }

  def addToWishlistJson: Action[AnyContent] = silhouette.SecuredAction(HasRole(UserRoles.User)) { securedRequest =>
    val wishlist:Wishlist = securedRequest.body.asJson.get.as[Wishlist]

    if(wishlist.user == securedRequest.identity.userID){
      wishRepo.create(wishlist.user, wishlist.quantity, wishlist.product)
      Ok(Json.toJson("Success."))
    }else{
      Unauthorized
    }
  }

  def updateWishlistJson: Action[AnyContent] = silhouette.SecuredAction(HasRole(UserRoles.User)) { securedRequest =>
    val wishlist:Wishlist = securedRequest.body.asJson.get.as[Wishlist]

    if(securedRequest.identity.userID == wishlist.user){
      wishRepo.update(wishlist.id, Wishlist(wishlist.id, wishlist.user, wishlist.quantity, wishlist.product))
      Ok(Json.toJson("Success."))
    }else{
      Unauthorized
    }


  }

  def removeFromWishlistJson: Action[AnyContent] = silhouette.SecuredAction(HasRole(UserRoles.User)) { securedRequest =>
    val wishlist:Wishlist = securedRequest.body.asJson.get.as[Wishlist]

    if(securedRequest.identity.userID == wishlist.user) {
      wishRepo.delete(wishlist.id)
      Ok(Json.toJson("Success."))
    }else{
      Unauthorized
    }
  }





}


case class CreateWishForm(user: String, quantity: Int, product: Int)
case class UpdateWishForm(id: Int, user: String, quantity: Int, product: Int)