package controllers

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject._
import models.auth.UserRoles
import models.{Order, OrderAd, OrderAdRepository, OrderRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.json._
import utils.auth.{DefaultEnv, HasRole}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}




@Singleton
class OrderAdController @Inject()(orderAdRepo: OrderAdRepository, orderRepo:OrderRepository,
                                  silhouette: Silhouette[DefaultEnv], cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

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
      "number" -> nonEmptyText
    )(UpdateAddressForm.apply)(UpdateAddressForm.unapply)
  }


  def orderAddress(id: Int) = Action.async { implicit request =>

    val adresy = orderAdRepo.getById(id)
    adresy.map(orderad => Ok(views.html.orderAddress(orderad)))
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
        val zamowienie = orderRepo.getById(orderad.order)
        val address = orderAdRepo.create(orderad.country, orderad.city, orderad.street, orderad.number)

        val adr = Await.result(address, Duration.Inf)
        zamowienie.map( order => orderRepo.update(order.id, Order(order.id, order.user, order.status, adr.id)))
        Future.successful(
          Redirect(routes.PaymentController.addPayment(orderad.order))
        )
      }
    )
  }

  def changeAddressMenu(id: Integer) = Action.async { implicit request =>
    val adres = orderAdRepo.getById(id)
    adres.map(orderad => {
      val ordadForm = updateAddressForm.fill(UpdateAddressForm(orderad.id, orderad.country, orderad.city, orderad.street, orderad.number))
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
        orderAdRepo.update(orderad.id, OrderAd(orderad.id, orderad.country, orderad.city, orderad.street, orderad.number)).map { _ =>
          Redirect(routes.OrderAdController.changeAddressMenu(orderad.id)).flashing("success" -> "address updated")
        }
      }
    )
  }


  def removeAddress(id: Int) = Action {
    orderAdRepo.delete(id)
    Redirect("/allAddresses")
  }



  def allAddressesJson = Action.async { implicit request =>
    val adresy = orderAdRepo.list()
    adresy.map(orderad => Ok(Json.toJson(orderad)))
  }

  def orderAddressJson(id: Int) = Action.async { implicit request =>
    val adresy = orderAdRepo.getById(id)
    adresy.map(orderad => Ok(Json.toJson(orderad)))
  }

  def addAddressJson(orderId: Int) = silhouette.SecuredAction(HasRole(UserRoles.User)).async { securedRequest =>

    val orderad:OrderAd = securedRequest.body.asJson.get.as[OrderAd]

    val zamowienie = orderRepo.getById(orderId)

    val address = orderAdRepo.create(orderad.country, orderad.city, orderad.street, orderad.number)

    val adr = Await.result(address, Duration.Inf)
    zamowienie.map( order =>
      if(securedRequest.identity.userID == order.user){
        orderRepo.update(order.id, Order(order.id, order.user, order.status, adr.id))
        Ok(Json.toJson("Success"))
      }else{
        Unauthorized
      }
    )

  }

  def changeAddressMenuJson(id: Int) = Action.async { implicit request =>
    val adresy = orderAdRepo.getById(id)
    adresy.map(orderad => Ok(Json.toJson(orderad)))
  }

  def changeAddressJson = Action { implicit request =>
    val orderad:OrderAd = request.body.asJson.get.as[OrderAd]
    orderAdRepo.update(orderad.id, OrderAd(orderad.id, orderad.country, orderad.city, orderad.street, orderad.number))
    Redirect("/orderAddressJson/"+orderad.id)
  }

  def removeAddressJson = Action { implicit request =>
    val orderad:OrderAd = request.body.asJson.get.as[OrderAd]
    orderAdRepo.delete(orderad.id)
    Redirect("/allAddressesJson")
  }




}


case class CreateAddressForm(country: String, city: String, street: String, number: String,order: Int)
case class UpdateAddressForm(id: Int, country: String, city: String, street: String, number: String)