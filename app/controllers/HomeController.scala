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


  def products = Action {
    Ok(views.html.index("Products."))
  }

  def productsByCat(catId: Int) = Action {
    Ok(views.html.index("Products from category: "+ catId))
  }

  def productsBySub(subId: Int) = Action {
    Ok(views.html.index("Products from subcategory: "+subId))
  }

  def productDetails(id: Int) = Action {
    Ok(views.html.index("Product details: "+id))
  }

  def addProductMenu = Action {
    Ok(views.html.index("Menu to add a product."))
  }

  def addProduct = Action {
    Ok(views.html.index("Adding a product."))
  }

  def updateProduct = Action {
    Ok(views.html.index("Updating a product."))
  }

  def removeProduct = Action {
    Ok(views.html.index("Removing a product."))
  }


  def reviews(productId: Int) = Action {
    Ok(views.html.index("Reviews to product: "+productId))
  }

  def addReviewMenu = Action {
    Ok(views.html.index("Menu to add a review."))
  }

  def addReview = Action {
    Ok(views.html.index("Adding a review."))
  }

  def updateReview = Action {
    Ok(views.html.index("updating a review."))
  }

  def removeReview = Action {
    Ok(views.html.index("Removing a review."))
  }

  def wishlist = Action {
    Ok(views.html.index("Wishlist."))
  }

  def addToWishlist = Action {
    Ok(views.html.index("Adding to the wishlist."))
  }

  def updateWishlist = Action {
    Ok(views.html.index("Updating the wishlist."))
  }

  def removeFromWishlist = Action {
    Ok(views.html.index("Removing from the wishlist."))
  }

  def basket = Action {
    Ok(views.html.index("Basket."))
  }

  def addToBasket = Action {
    Ok(views.html.index("Adding to basket."))
  }

  def changeQuantity = Action {
    Ok(views.html.index("Changing the quantity in basket."))
  }

  def removeFromBasket = Action {
    Ok(views.html.index("Removing from basket."))
  }

  def orders = Action {
    Ok(views.html.index("Orders' list."))
  }

  def orderDetails(id: Int) = Action {
    Ok(views.html.index("Order details of: "+id))
  }

  def addOrder = Action {
    Ok(views.html.index("Adding an order."))
  }

  def updateOrder = Action {
    Ok(views.html.index("Updating an order."))
  }

  def removeOrder = Action {
    Ok(views.html.index("Removing an order."))
  }



  def orderedProducts(orderId: Int) = Action {
    Ok(views.html.index("Products from the order: "+orderId))
  }
  def addToOrder = Action {
    Ok(views.html.index("Adding a product to the order."))
  }
  def changeOrdered = Action {
    Ok(views.html.index("Changing a product from the order."))
  }
  def removeOrdered = Action {
    Ok(views.html.index("Removing products from the order."))
  }



  def orderAddress(orderId: Int) = Action {
    Ok(views.html.index("Address of the order: "+orderId))
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
