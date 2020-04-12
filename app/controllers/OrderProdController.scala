package controllers

import javax.inject._
import models.{Basket, BasketRepository, OrderProd, OrderProdRepository, OrderRepository, Product, ProductRepository}
import play.api.data.Form
import play.api.data.Forms._
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
            var productCount = 0
            for(ba <- bask) {
              if(prod.exists(_.id == ba.product)){
                orderProdRepo.create(prod.find(_.id == ba.product).get.name, prod.find(_.id == ba.product).get.price, ba.quantity, ordered.orderId)
                productCount += 1
                basketRepo.delete(ba.id)
              }
            }
            Redirect(routes.HomeController.addAddressMenu(ordered.orderId, ordered.userId, productCount))
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

}

case class AddToOrderForm(orderId: Int, userId: Int)
case class UpdateOrderedForm(id: Int, name: String, price: Int, quantity: Int, order: Int)