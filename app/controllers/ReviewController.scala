package controllers

import javax.inject._
import models.{Product, ProductRepository, Review, ReviewRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
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
    val produkty = productRepo.list()
    val prod = Await.result(produkty, Duration.Inf)

    val recenzje = reviewRepo.list()
    recenzje.map( reviews => Ok(views.html.allReviews(reviews, prod)))
  }

  def reviews(productId: Int) = Action.async { implicit request =>
    val produkt = productRepo.getById(productId)

    val recenzje = reviewRepo.getByProduct(productId)
    val product = Await.result(produkt, Duration.Inf)
    recenzje.map( reviews => Ok(views.html.reviews(reviews, product)))
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
    val produkty = productRepo.list()
    val prod = Await.result(produkty, Duration.Inf)

    val recenzja = reviewRepo.getById(id)
    recenzja.map(review => {
      val revForm = updateReviewForm.fill(UpdateReviewForm(review.id, review.title, review.content, review.product))
      Ok(views.html.updateReviewMenu(revForm, prod))
    })
  }

  def updateReview = Action.async { implicit request =>
    val produkty = productRepo.list()
    val prod = Await.result(produkty, Duration.Inf)

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


  def getReviewsJson(productId: Int) = Action.async {
    val recenzje = reviewRepo.getByProduct(productId)
    recenzje.map( reviews => Ok(Json.toJson(reviews)))

  }

  def getAllReviewsJson = Action.async {

    val recenzje = reviewRepo.list()
    recenzje.map( reviews => Ok(Json.toJson(reviews)))

  }


  def addReviewJson: Action[AnyContent] = Action { implicit request =>
    val review:Review = request.body.asJson.get.as[Review]
    reviewRepo.create(review.title, review.content, review.product)
    Redirect("/reviewsJson/"+review.product)
  }

  def updateReviewMenuJson(id: Int) = Action.async { implicit request =>
    val recenzja = reviewRepo.getById(id)
    recenzja.map(review => {
      Ok(Json.toJson(review))
    })
  }

  def updateReviewJson = Action { implicit request =>
    val review:Review = request.body.asJson.get.as[Review]
    reviewRepo.update(review.id, Review(review.id, review.title, review.content, review.product))
    Redirect("/reviewsJson/"+review.product)
  }

  def removeReviewJson = Action { implicit request =>
    val review:Review = request.body.asJson.get.as[Review]
    reviewRepo.delete(review.id)
    Redirect("/reviewsJson/"+review.product)
  }



}


case class CreateReviewForm(title: String, content: String, product: Int)
case class UpdateReviewForm(id: Int, title: String, content: String, product: Int)