package controllers

import javax.inject._
import models.{Product, ProductRepository, Review, ReviewRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}




@Singleton
class ReviewController @Inject()(reviewRepo: ReviewRepository, productRepo: ProductRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val reviewForm: Form[CreateReviewForm] = Form {
    mapping(
      "title" -> nonEmptyText,
      "content" -> nonEmptyText,
      "product" -> number
    )(CreateReviewForm.apply)(CreateReviewForm.unapply)
  }

  val updateReviewForm: Form[UpdateReviewForm] = Form {
    mapping(
      "id" -> number,
      "title" -> nonEmptyText,
      "content" -> nonEmptyText,
      "product" -> number
    )(UpdateReviewForm.apply)(UpdateReviewForm.unapply)
  }


  def allReviews = Action.async { implicit request =>
    var prod:Seq[Product] = Seq[Product]()
    val produkty = productRepo.list().onComplete{
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    val recenzje = reviewRepo.list()
    recenzje.map( reviews => Ok(views.html.allReviews(reviews, prod)))
  }

  def reviews(productId: Int) = Action.async { implicit request =>
    var prod:Seq[Product] = Seq[Product]()
    val produkty = productRepo.list().onComplete{
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    val recenzje = reviewRepo.getByProduct(productId)
    recenzje.map( reviews => Ok(views.html.reviews(reviews, prod, productId)))
  }

  def addReviewMenu(productId: Int) = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.addReviewMenu(reviewForm, productId))

  }

  def addReview(productId: Int) = Action.async { implicit request =>

    reviewForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.addReviewMenu(errorForm, productId))
        )
      },
      review => {
        reviewRepo.create(review.title, review.content, review.product).map { _ =>
          Redirect(routes.ReviewController.addReviewMenu(productId)).flashing("success" -> "new review added")
        }
      }
    )
  }

  def updateReviewMenu(id: Int) = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var prod:Seq[Product] = Seq[Product]()
    val produkty = productRepo.list().onComplete{
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    val recenzja = reviewRepo.getById(id)
    recenzja.map(review => {
      val revForm = updateReviewForm.fill(UpdateReviewForm(review.id, review.title, review.content, review.product))
      Ok(views.html.updateReviewMenu(revForm, prod))
    })
  }

  def updateReview = Action.async { implicit request =>
    var prod:Seq[Product] = Seq[Product]()
    val produkty = productRepo.list().onComplete{
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    updateReviewForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.updateReviewMenu(errorForm, prod))
        )
      },
      review => {
        reviewRepo.update(review.id, Review(review.id, review.title, review.content, review.product)).map { _ =>
          Redirect(routes.ReviewController.updateReviewMenu(review.id)).flashing("success" -> "review updated")
        }
      }
    )

  }

  def removeReview(id: Int) = Action {
    reviewRepo.delete(id)
    Redirect("/allReviews")
  }


}


case class CreateReviewForm(title: String, content: String, product: Int)
case class UpdateReviewForm(id: Int, title: String, content: String, product: Int)