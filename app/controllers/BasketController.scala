package controllers

import javax.inject._
import models.{Basket, BasketRepository, Product, ProductRepository}
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
class BasketController @Inject()(basketRepo:BasketRepository, productRepo: ProductRepository,
                                 silhouette: Silhouette[DefaultEnv], cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val basketForm: Form[CreateBasketForm] = Form {
    mapping(
      "user" -> nonEmptyText,
      "quantity" -> number,
      "product" -> number
    )(CreateBasketForm.apply)(CreateBasketForm.unapply)
  }

  val updateBasketForm: Form[UpdateBasketForm] = Form {
    mapping(
      "id" -> number,
      "user" -> nonEmptyText,
      "quantity" -> number,
      "product" -> number
    )(UpdateBasketForm.apply)(UpdateBasketForm.unapply)
  }

  def basket(userId: String) = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val produkty = productRepo.list()
    val prod = Await.result(produkty, Duration.Inf)

    val koszyk = basketRepo.getByUser(userId)
    koszyk.map( baskets => Ok(views.html.basket(baskets, prod, userId, updateBasketForm)))
  }

  def allBaskets = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val produkty = productRepo.list()
    val prod = Await.result(produkty, Duration.Inf)

    val koszyk = basketRepo.list()
    koszyk.map( baskets => Ok(views.html.allBaskets(baskets, prod, updateBasketForm)))
  }

  def addToBasketMenu(productId: Int) = Action {implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.addToBasketMenu(basketForm, productId))
  }

  def addToBasket(productId: Int) = Action.async { implicit request =>

    basketForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.addToBasketMenu(errorForm, productId))
        )
      },
      basket => {
        basketRepo.create(basket.user, basket.quantity, basket.product).map { _ =>
          Redirect(routes.BasketController.addToBasketMenu(productId)).flashing("success" -> "product added to basket")
        }
      }
    )
  }


  def updateBasket = Action.async { implicit request =>
    val produkty = productRepo.list()
    val prod = Await.result(produkty, Duration.Inf)

    val koszyk = basketRepo.list()
    val bask = Await.result(koszyk, Duration.Inf)

    updateBasketForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.allBaskets(bask, prod, errorForm))
        )
      },
      basket => {
        basketRepo.update(basket.id, Basket(basket.id, basket.user, basket.quantity, basket.product)).map { _ =>
          Redirect(routes.BasketController.basket(basket.user))
        }
      }
    )
  }

  def removeFromBasket(id: Int) = Action {
    basketRepo.delete(id)
    Redirect("/allBaskets")
  }


  def basketJson(userId: String) = silhouette.SecuredAction(HasRole(UserRoles.User)).async { securedRequest =>
    if(userId == securedRequest.identity.userID) {
      val koszyk = basketRepo.getByUser(userId)
      koszyk.map(baskets => Ok(Json.toJson(baskets)))
    }else{
      Future.successful(Unauthorized)
    }
  }

  def allBasketsJson = Action.async { implicit request =>

    val koszyk = basketRepo.list()
    koszyk.map( baskets => Ok(Json.toJson(baskets)))
  }

  def addToBasketJson: Action[AnyContent] = silhouette.SecuredAction(HasRole(UserRoles.User)) { securedRequest =>
    val basket:Basket = securedRequest.body.asJson.get.as[Basket]

    if(basket.user == securedRequest.identity.userID) {
      basketRepo.create(basket.user, basket.quantity, basket.product)
      Ok(Json.toJson("Success."))
    }else{
      Unauthorized
    }

  }

  def updateBasketJson: Action[AnyContent] = silhouette.SecuredAction(HasRole(UserRoles.User)) { securedRequest =>
    val basket:Basket = securedRequest.body.asJson.get.as[Basket]

    if(basket.user == securedRequest.identity.userID){
      basketRepo.update(basket.id, Basket(basket.id, basket.user, basket.quantity, basket.product))
      Ok(Json.toJson("Success."))
    }else{
      Unauthorized
    }

  }

  def removeFromBasketJson: Action[AnyContent] = silhouette.SecuredAction(HasRole(UserRoles.User)) { securedRequest =>
    val basket:Basket = securedRequest.body.asJson.get.as[Basket]

    if(basket.user == securedRequest.identity.userID) {
      basketRepo.delete(basket.id)
      Redirect("/basketJson/" + basket.user)
    }else{
      Unauthorized
    }

  }



}

case class CreateBasketForm(user: String, quantity: Int, product: Int)
case class UpdateBasketForm(id: Int, user: String, quantity: Int, product: Int)