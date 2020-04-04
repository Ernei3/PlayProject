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

  def categories = Action {
    Ok(views.html.index("Categories"))
  }

  def subcategories = Action {
    Ok(views.html.index("Subcategories"))
  }

  def products = Action {
    Ok(views.html.index("Products."))
  }

  def reviews = Action {
    Ok(views.html.index("Reviews."))
  }

  def wishlist = Action {
    Ok(views.html.index("Wishlist."))
  }

  def basket = Action {
    Ok(views.html.index("Basket."))
  }

  def orders = Action {
    Ok(views.html.index("Orders' status."))
  }

  def orderedProducts = Action {
    Ok(views.html.index("Products from the order."))
  }

  def orderAddress = Action {
    Ok(views.html.index("Where to send the order."))
  }

  def payment = Action {
    Ok(views.html.index("Payment"))
  }


}
