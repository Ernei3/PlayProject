package controllers

import javax.inject._
import models.{Basket, BasketRepository, Order, OrderProd, OrderProdRepository, OrderRepository, Product, ProductRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}




@Singleton
class OrderProdController @Inject()(orderProdRepo:OrderProdRepository, orderRepo:OrderRepository, productRepo:ProductRepository, basketRepo:BasketRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val addToOrderForm: Form[AddToOrderForm] = Form {
    mapping(
      "orderId" -> number,
      "userId" -> number,
    )(AddToOrderForm.apply)(AddToOrderForm.unapply)
  }

  val updateOrderedForm: Form[UpdateOrderedForm] = Form {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "price" -> number,
      "quantity" -> number,
      "order" -> number
    )(UpdateOrderedForm.apply)(UpdateOrderedForm.unapply)
  }


  def orderedProducts(orderId: Int) = Action.async { implicit request =>

    val produkty = orderProdRepo.getByOrder(orderId)
    produkty.map(orderprod => Ok(views.html.orderedProducts(orderprod, orderId)))
  }

  def allOrderedProducts = Action.async { implicit request =>
    val produkty = orderProdRepo.list()
    produkty.map(orderprod => Ok(views.html.allOrderedProducts(orderprod)))
  }

  def addToOrder = Action.async { implicit request =>

    var prod:Seq[Product] = Seq[Product]()
    val produkty = productRepo.list().onComplete{
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }


    addToOrderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.index("Error while adding products to the order."))
        )
      },
      ordered => {

          val koszyk = basketRepo.getByUser(ordered.userId)

          koszyk.map { bask =>
            for(ba <- bask) {
              if(prod.exists(_.id == ba.product)){
                orderProdRepo.create(prod.find(_.id == ba.product).get.name, prod.find(_.id == ba.product).get.price, ba.quantity, ordered.orderId)
                basketRepo.delete(ba.id)
              }
            }
            Redirect(routes.OrderAdController.addAddressMenu(ordered.orderId))
          }


      }
    )

  }


  def changeOrderedMenu(id: Integer) = Action.async { implicit request =>
    val zamowione = orderProdRepo.getById(id)
    zamowione.map(orderedprod => {
      val ordprodForm = updateOrderedForm.fill(UpdateOrderedForm(orderedprod.id, orderedprod.name, orderedprod.price, orderedprod.quantity, orderedprod.order))
      Ok(views.html.changeOrderedMenu(ordprodForm))
    })
  }

  def changeOrdered = Action.async { implicit request =>

    updateOrderedForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.changeOrderedMenu(errorForm))
        )
      },
      orderprod => {
        orderProdRepo.update(orderprod.id, OrderProd(orderprod.id, orderprod.name, orderprod.price, orderprod.quantity, orderprod.order)).map { _ =>
          Redirect(routes.OrderProdController.changeOrderedMenu(orderprod.id)).flashing("success" -> "ordered product updated")
        }
      }
    )
  }


  def removeOrdered(id: Int) = Action {
    orderProdRepo.delete(id)
    Redirect("/allOrderedProducts")
  }




  def orderedProductsJson(orderId: Int) = Action.async { implicit request =>

    val produkty = orderProdRepo.getByOrder(orderId)
    produkty.map(orderprod => Ok(Json.toJson(orderprod, orderId)))
  }

  def allOrderedProductsJson = Action.async { implicit request =>
    val produkty = orderProdRepo.list()
    produkty.map(orderprod => Ok(Json.toJson(orderprod)))
  }

  def addToOrderJson = Action.async { implicit request =>

    val order:Order = request.body.asJson.get.as[Order]

    var prod: Seq[Product] = Seq[Product]()
    val produkty = productRepo.list().onComplete {
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    val koszyk = basketRepo.getByUser(order.user)

    koszyk.map { bask =>
      for (ba <- bask) {
        if (prod.exists(_.id == ba.product)) {
          orderProdRepo.create(prod.find(_.id == ba.product).get.name, prod.find(_.id == ba.product).get.price, ba.quantity, order.id)
          basketRepo.delete(ba.id)
        }
      }
      Ok(Json.toJson(order))
    }

  }

  def changeOrderedMenuJson(id: Int) = Action.async { implicit request =>

    val produkty = orderProdRepo.getById(id)
    produkty.map(orderprod => Ok(Json.toJson(orderprod)))
  }

  def changeOrderedJson: Action[AnyContent] = Action { implicit request =>
    val orderprod:OrderProd = request.body.asJson.get.as[OrderProd]
    orderProdRepo.update(orderprod.id, OrderProd(orderprod.id, orderprod.name, orderprod.price, orderprod.quantity, orderprod.order))
    Redirect("/orderedProductsJson/"+orderprod.order)
  }

  def removeOrderedJson: Action[AnyContent] = Action { implicit request =>
    val orderprod:OrderProd = request.body.asJson.get.as[OrderProd]
    orderProdRepo.delete(orderprod.id)
    Redirect("/orderedProductsJson/"+orderprod.order)
  }



}

case class AddToOrderForm(orderId: Int, userId: Int)
case class UpdateOrderedForm(id: Int, name: String, price: Int, quantity: Int, order: Int)