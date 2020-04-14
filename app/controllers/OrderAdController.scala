package controllers

import javax.inject._
import models.{OrderAd, OrderAdRepository, OrderRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}




@Singleton
class OrderAdController @Inject()(orderAdRepo: OrderAdRepository, orderRepo:OrderRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val createAddressForm: Form[CreateAddressForm] = Form {
    mapping(
      "country" -> nonEmptyText,
      "city" -> nonEmptyText,
      "street" -> nonEmptyText,
      "number" -> nonEmptyText,
      "order" -> number
    )(CreateAddressForm.apply)(CreateAddressForm.unapply)
  }

  val updateAddressForm: Form[UpdateAddressForm] = Form {
    mapping(
      "id" -> number,
      "country" -> nonEmptyText,
      "city" -> nonEmptyText,
      "street" -> nonEmptyText,
      "number" -> nonEmptyText,
      "order" -> number
    )(UpdateAddressForm.apply)(UpdateAddressForm.unapply)
  }


  def orderAddress(orderId: Int) = Action.async { implicit request =>

    val adresy = orderAdRepo.getByOrder(orderId)
    adresy.map(orderad => Ok(views.html.orderAddress(orderad, orderId)))
  }

  def allAddresses = Action.async { implicit request =>
    val adresy = orderAdRepo.list()
    adresy.map(orderad => Ok(views.html.allAddresses(orderad)))
  }

  def addAddressMenu(orderId: Int) = Action { implicit request =>
    Ok(views.html.addAddressMenu(createAddressForm, orderId))
  }

  def addAddress = Action.async { implicit request =>

    createAddressForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.index("Error while adding the address to order."))
        )
      },
      orderad => {
        orderAdRepo.create(orderad.country, orderad.city, orderad.street, orderad.number, orderad.order).map { _ =>
          Redirect(routes.PaymentController.addPayment(orderad.order))
        }
      }
    )
  }

  def changeAddressMenu(id: Integer) = Action.async { implicit request =>
    val adres = orderAdRepo.getById(id)
    adres.map(orderad => {
      val ordadForm = updateAddressForm.fill(UpdateAddressForm(orderad.id, orderad.country, orderad.city, orderad.street, orderad.number, orderad.order))
      Ok(views.html.changeAddressMenu(ordadForm))
    })
  }

  def changeAddress = Action.async { implicit request =>

    updateAddressForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.changeAddressMenu(errorForm))
        )
      },
      orderad => {
        orderAdRepo.update(orderad.id, OrderAd(orderad.id, orderad.country, orderad.city, orderad.street, orderad.number, orderad.order)).map { _ =>
          Redirect(routes.OrderAdController.changeAddressMenu(orderad.id)).flashing("success" -> "address updated")
        }
      }
    )
  }


  def removeAddress(id: Int) = Action {
    orderAdRepo.delete(id)
    Redirect("/allAddresses")
  }


}


case class CreateAddressForm(country: String, city: String, street: String, number: String,order: Int)
case class UpdateAddressForm(id: Int, country: String, city: String, street: String, number: String,order: Int)