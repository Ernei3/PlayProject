package controllers

import javax.inject._
import models.{Payment, PaymentRepository, Order, OrderRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}




@Singleton
class PaymentController @Inject()(paymentRepo: PaymentRepository, orderRepo:OrderRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val addPaymentForm: Form[AddPaymentForm] = Form {
    mapping(
      "number" -> nonEmptyText,
      "name" -> nonEmptyText,
      "date" -> nonEmptyText,
      "code" -> number,
      "order" -> number
    )(AddPaymentForm.apply)(AddPaymentForm.unapply)
  }

  val updatePaymentForm: Form[UpdatePaymentForm] = Form {
    mapping(
      "id" -> number,
      "number" -> nonEmptyText,
      "name" -> nonEmptyText,
      "date" -> nonEmptyText,
      "code" -> number,
      "order" -> number
    )(UpdatePaymentForm.apply)(UpdatePaymentForm.unapply)
  }



  def allPayments = Action.async { implicit request =>
    val zaplaty = paymentRepo.list()
    zaplaty.map(payment => Ok(views.html.allPayments(payment)))
  }

  def payment(orderId: Int) = Action.async { implicit request =>

    val zaplaty = paymentRepo.getByOrder(orderId)
    zaplaty.map(payment => Ok(views.html.orderPayment(payment, orderId)))
  }

  def addPayment(orderId: Int) = Action { implicit request =>
    Ok(views.html.addPayment(addPaymentForm, orderId))
  }

  def sendPayment = Action.async { implicit request =>

    addPaymentForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.index("Error while adding payment."))
        )
      },
      payment => {
        val zamowienie = orderRepo.getById(payment.order)
        zamowienie.map { order =>
          orderRepo.update(payment.order, Order(order.id, order.user, "Accepted"))
        }
        paymentRepo.create(payment.number, payment.name, payment.date, payment.code, payment.order).map { _ =>
          Redirect(routes.OrderController.orderDetails(payment.order))
        }
      }
    )
  }


  def changePaymentMenu(id: Integer) = Action.async { implicit request =>
    val zaplata = paymentRepo.getById(id)
    zaplata.map(payment => {
      val payForm = updatePaymentForm.fill(UpdatePaymentForm(payment.id, payment.number, payment.name, payment.date, payment.code, payment.order))
      Ok(views.html.changePaymentMenu(payForm))
    })
  }


  def changePayment = Action.async { implicit request =>

    updatePaymentForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.changePaymentMenu(errorForm))
        )
      },
      payment => {
        paymentRepo.update(payment.id, Payment(payment.id, payment.number, payment.name, payment.date, payment.code, payment.order)).map { _ =>
          Redirect(routes.PaymentController.changePaymentMenu(payment.id)).flashing("success" -> "payment updated")
        }
      }
    )
  }

  def removePayment(id: Int) = Action {
    paymentRepo.delete(id)
    Redirect("/allPayments")
  }

}


case class AddPaymentForm(number: String, name: String, date: String, code: Int, order: Int )
case class UpdatePaymentForm(id: Int, number: String, name: String, date: String, code: Int, order: Int )