package controllers

import javax.inject._
import models.{Order, OrderRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}




@Singleton
class OrderController @Inject()(orderRepo:OrderRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val updateOrderForm: Form[UpdateOrderForm] = Form {
    mapping(
      "id" -> number,
      "user" -> number,
      "status" -> nonEmptyText,
    )(UpdateOrderForm.apply)(UpdateOrderForm.unapply)
  }



  def orders(userId: Int) = Action.async { implicit request =>
    val zamowienia = orderRepo.getByUser(userId)
    zamowienia.map( orders => Ok(views.html.order(orders, userId)))
  }


  def allOrders = Action.async { implicit request =>
    val zamowienia = orderRepo.list()
    zamowienia.map( orders => Ok(views.html.allOrders(orders)))
  }


  def orderDetails(id: Int) = Action {
    Ok(views.html.index("Order details of: "+id))
  }

  def addOrder(userId: Int) = Action.async { implicit request =>

    val zamowienia = orderRepo.create(userId, "Created")
    zamowienia.map(orders => Ok(views.html.index("Created an order with id: " + orders.id)))
  }

  def updateOrderMenu(id: Integer) = Action.async { implicit request =>
    val zamowienie = orderRepo.getById(id)
    zamowienie.map(order => {
      val ordForm = updateOrderForm.fill(UpdateOrderForm(order.id, order.user, order.status))
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
        orderRepo.update(order.id, Order(order.id, order.user, order.status)).map { _ =>
          Redirect(routes.OrderController.updateOrderMenu(order.id)).flashing("success" -> "order updated")
        }
      }
    )
  }


  def removeOrder(id: Int) = Action {
    orderRepo.delete(id)
    Redirect("/allOrders")
  }


}


case class UpdateOrderForm(id: Int, user: Int, status: String)