package controllers

import javax.inject._
import models.{BasketRepository, Order, OrderRepository, ProductRepository}
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
class OrderController @Inject()(orderRepo:OrderRepository, productRepo:ProductRepository, basketRepo:BasketRepository,
                                silhouette: Silhouette[DefaultEnv], cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {


  val updateOrderForm: Form[UpdateOrderForm] = Form {
    mapping(
      "id" -> number,
      "user" -> nonEmptyText,
      "status" -> nonEmptyText,
      "address" -> number
    )(UpdateOrderForm.apply)(UpdateOrderForm.unapply)
  }



  def orders(userId: String) = Action.async { implicit request =>
    val zamowienia = orderRepo.getByUser(userId)
    zamowienia.map( orders => Ok(views.html.order(orders, userId)))
  }


  def allOrders = Action.async { implicit request =>
    val zamowienia = orderRepo.list()
    zamowienia.map( orders => Ok(views.html.allOrders(orders)))
  }


  def orderDetails(id: Int) = Action.async {
    val zamowienia = orderRepo.getById(id)
    zamowienia.map( order => Ok(views.html.orderDetails(order)))
  }

  def addOrder(userId: String) = Action.async { implicit request =>

    val produkty = productRepo.list()
    val koszyk = basketRepo.getByUser(userId)

    val prod = Await.result(produkty, Duration.Inf)
    val bask = Await.result(koszyk, Duration.Inf)

    val zamowienia = orderRepo.create(userId, "Created", 0)
    var priceSum = 0
    for(ba <- bask) {
      if(prod.exists(_.id == ba.product)){
        priceSum += prod.find(_.id == ba.product).get.price * ba.quantity
      }
    }

    zamowienia.map(orders => Ok(views.html.addOrderedProductsMenu(orders.id, prod, bask, priceSum, userId)))
  }

  def updateOrderMenu(id: Integer) = Action.async { implicit request =>
    val zamowienie = orderRepo.getById(id)
    zamowienie.map(order => {
      val ordForm = updateOrderForm.fill(UpdateOrderForm(order.id, order.user, order.status, order.address))
      Ok(views.html.updateOrderMenu(ordForm, order.status))
    })
  }

  def updateOrder = Action.async { implicit request =>

    updateOrderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.updateOrderMenu(errorForm, ""))
        )
      },
      order => {
        orderRepo.update(order.id, Order(order.id, order.user, order.status, order.address)).map { _ =>
          Redirect(routes.OrderController.updateOrderMenu(order.id)).flashing("success" -> "order updated")
        }
      }
    )
  }


  def removeOrder(id: Int) = Action {
    orderRepo.delete(id)
    Redirect("/allOrders")
  }

  def dropOrder(id: Int) = Action {
    orderRepo.delete(id)
    Redirect("/categories")
  }


  def ordersJson(userId: String) = silhouette.SecuredAction(HasRole(UserRoles.User)).async { securedRequest =>
    if(securedRequest.identity.userID == userId){
      val zamowienia = orderRepo.getByUser(userId)
      zamowienia.map( orders => Ok(Json.toJson(orders)))
    }else{
      Future.successful(Unauthorized)
    }

  }

  def allOrdersJson = Action.async { implicit request =>
    val zamowienia = orderRepo.list()
    zamowienia.map( orders => Ok(Json.toJson(orders)))
  }

  def orderDetailsJson(id: Int) = silhouette.SecuredAction(HasRole(UserRoles.User)).async { securedRequest =>

    val zamowienia = orderRepo.getById(id)
    zamowienia.map( order =>
        if(securedRequest.identity.userID == order.user){
          Ok(Json.toJson(order))
        }else{
          Unauthorized
        }
      )

  }

  def addOrderJson = Action.async { implicit request =>
    val order:Order = request.body.asJson.get.as[Order]
    val zamowienie = orderRepo.create(order.user, order.status, order.address)
    zamowienie.map(ord => Ok(Json.toJson(ord)))
  }

  def updateOrderMenuJson(id: Integer) = Action.async { implicit request =>
    val zamowienie = orderRepo.getById(id)
    zamowienie.map(order => {
      Ok(Json.toJson(order))
    })
  }

  def updateOrderJson: Action[AnyContent] = silhouette.SecuredAction(HasRole(UserRoles.User)) { securedRequest =>
    val order:Order = securedRequest.body.asJson.get.as[Order]
    if(securedRequest.identity.userID == order.user){
      orderRepo.update(order.id, Order(order.id, order.user, order.status, order.address))
      Ok(Json.toJson("Success"))
    }else{
      Unauthorized
    }
  }

  def dropOrderJson: Action[AnyContent] = Action { implicit request =>
    val order:Order = request.body.asJson.get.as[Order]
    orderRepo.delete(order.id)
    Redirect("/ordersJson/"+order.user)
  }



}


case class UpdateOrderForm(id: Int, user: String, status: String, address: Int)