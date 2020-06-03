package controllers

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject._
import models.auth.UserRoles
import models.{Order, OrderRepository, Payment, PaymentRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import utils.auth.{DefaultEnv, HasRole}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}




@Singleton
class PaymentController @Inject()(paymentRepo: PaymentRepository, orderRepo:OrderRepository,
                                  silhouette: Silhouette[DefaultEnv], cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val addPaymentForm: Form[AddPaymentForm] = Form {
    mapping(
      "number" -> nonEmptyText,
      "name" -> nonEmptyText,
      "date" -> nonEmptyText,
      "code" -> nonEmptyText,
      "order" -> number
    )(AddPaymentForm.apply)(AddPaymentForm.unapply)
  }

  val updatePaymentForm: Form[UpdatePaymentForm] = Form {
    mapping(
      "id" -> number,
      "number" -> nonEmptyText,
      "name" -> nonEmptyText,
      "date" -> nonEmptyText,
      "code" -> nonEmptyText,
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
          orderRepo.update(payment.order, Order(order.id, order.user, "Accepted", order.address))
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

  def allPaymentsJson = Action.async { implicit request =>
    val zaplaty = paymentRepo.list()
    zaplaty.map(payment => Ok(Json.toJson(payment)))
  }

  def paymentJson(orderId: Int) = Action.async { implicit request =>

    val zaplaty = paymentRepo.getByOrder(orderId)
    zaplaty.map(payment => Ok(Json.toJson(payment)))
  }

  def sendPaymentJson = silhouette.SecuredAction(HasRole(UserRoles.User)) { securedRequest =>
    val payment:Payment = securedRequest.body.asJson.get.as[Payment]
    paymentRepo.create(payment.number, payment.name, payment.date, payment.code, payment.order)
    Ok(Json.toJson("Success"))
  }

  def changePaymentMenuJson(id: Int) = Action.async { implicit request =>
    val zaplaty = paymentRepo.getById(id)
    zaplaty.map(payment => Ok(Json.toJson(payment)))
  }

  def changePaymentJson = Action { implicit request =>
    val payment:Payment = request.body.asJson.get.as[Payment]
    paymentRepo.update(payment.id, Payment(payment.id, payment.number, payment.name, payment.date, payment.code, payment.order))
    Redirect("/paymentJson/"+payment.order)
  }

  def removePaymentJson = Action { implicit request =>
    val payment:Payment = request.body.asJson.get.as[Payment]
    paymentRepo.delete(payment.id)
    Redirect("/paymentJson/"+payment.order)
  }



}


case class AddPaymentForm(number: String, name: String, date: String, code: String, order: Int )
case class UpdatePaymentForm(id: Int, number: String, name: String, date: String, code: String, order: Int )