package controllers

import javax.inject._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("This is the main page."))
  }




  def orderAddress(orderId: Int) = Action {
    Ok(views.html.index("Address of the order:" + orderId))
  }

  def addAddressMenu(orderId: Int, userId: Int, productCount: Int) = Action {
    Ok(views.html.index("Address Menu for order and user: "+ orderId + " " + userId + " with "+ productCount+" products."))
  }

  def addAddress = Action {
    Ok(views.html.index("Adding an address."))
  }

  def changeAddress = Action {
    Ok(views.html.index("Changing the address."))
  }

  def removeAddress = Action {
    Ok(views.html.index("Removing the address."))
  }



  def payment(orderId: Int) = Action {
    Ok(views.html.index("Payment to the order: "+orderId))
  }

  def sendPayment = Action {
    Ok(views.html.index("Sending payment."))
  }

  def changePayment = Action {
    Ok(views.html.index("Changing payment."))
  }

  def removePayment = Action {
    Ok(views.html.index("Removing payment."))
  }


}
