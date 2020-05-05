package controllers

import javax.inject._
import models.{Product, ProductRepository, Wishlist, WishlistRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}




@Singleton
class WishlistController @Inject()(wishRepo: WishlistRepository, productRepo: ProductRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val wishForm: Form[CreateWishForm] = Form {
    mapping(
      "user" -> number,
      "quantity" -> number,
      "product" -> number
    )(CreateWishForm.apply)(CreateWishForm.unapply)
  }

  val updateWishForm: Form[UpdateWishForm] = Form {
    mapping(
      "id" -> number,
      "user" -> number,
      "quantity" -> number,
      "product" -> number
    )(UpdateWishForm.apply)(UpdateWishForm.unapply)
  }


  def wishlist(userId: Int) = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var prod:Seq[Product] = Seq[Product]()
    val produkty = productRepo.list().onComplete{
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    val listy = wishRepo.getByUser(userId)
    listy.map( wishlists => Ok(views.html.wishlist(wishlists, prod, userId, updateWishForm)))
  }

  def allWishes = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var prod:Seq[Product] = Seq[Product]()
    val produkty = productRepo.list().onComplete{
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

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
    var prod:Seq[Product] = Seq[Product]()
    val produkty = productRepo.list().onComplete{
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    var wish:Seq[Wishlist] = Seq[Wishlist]()
    val listy = wishRepo.list().onComplete{
      case Success(w) => wish = w
      case Failure(_) => print("fail")
    }

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




  def wishlistJson(userId: Int) = Action.async { implicit request =>
    var prod:Seq[Product] = Seq[Product]()
    val produkty = productRepo.list().onComplete{
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    val listy = wishRepo.getByUser(userId)
    listy.map( wishlists => Ok(Json.toJson(wishlists, prod, userId)))
  }

  def allWishesJson = Action.async { implicit request =>
    var prod:Seq[Product] = Seq[Product]()
    val produkty = productRepo.list().onComplete{
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    val listy = wishRepo.list()
    listy.map( wishlists => Ok(Json.toJson(wishlists, prod)))
  }

  def addToWishlistJson: Action[AnyContent] = Action { implicit request =>
    val wishlist:Wishlist = request.body.asJson.get.as[Wishlist]
    wishRepo.create(wishlist.user, wishlist.quantity, wishlist.product)
    Redirect("/wishlistJson/"+wishlist.user)
  }

  def updateWishlistJson: Action[AnyContent] = Action { implicit request =>
    val wishlist:Wishlist = request.body.asJson.get.as[Wishlist]
    wishRepo.update(wishlist.id, Wishlist(wishlist.id, wishlist.user, wishlist.quantity, wishlist.product))
    Redirect("/wishlistJson/"+wishlist.user)
  }

  def removeFromWishlistJson: Action[AnyContent] = Action { implicit request =>
    val wishlist:Wishlist = request.body.asJson.get.as[Wishlist]
    wishRepo.delete(wishlist.id)
    Redirect("/wishlistJson/"+wishlist.user)
  }





}


case class CreateWishForm(user: Int, quantity: Int, product: Int)
case class UpdateWishForm(id: Int, user: Int, quantity: Int, product: Int)