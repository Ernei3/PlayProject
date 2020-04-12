package controllers

import javax.inject._
import models.{Basket, BasketRepository, Order, OrderRepository, Product, ProductRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}




@Singleton
class OrderController @Inject()(orderRepo:OrderRepository, productRepo:ProductRepository, basketRepo:BasketRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {


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
    var prod:Seq[Product] = Seq[Product]()
    val produkty = productRepo.list().onComplete{
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    var bask:Seq[Basket] = Seq[Basket]()
    val koszyk = basketRepo.getByUser(userId).onComplete{
      case Success(b) => bask = b
      case Failure(_) => print("fail")
    }

    val zamowienia = orderRepo.create(userId, "Created")
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