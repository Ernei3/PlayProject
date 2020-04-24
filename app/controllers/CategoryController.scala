package controllers

import javax.inject._
import models.{Category, CategoryRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext


class  CategoryController @Inject()(categoryRepo: CategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val categoryForm: Form[CreateCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText,
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }

  val updateCategoryForm: Form[UpdateCategoryForm] = Form {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
    )(UpdateCategoryForm.apply)(UpdateCategoryForm.unapply)
  }


  def categories = Action.async { implicit request =>
    val kategorie = categoryRepo.list()
    kategorie.map( categories => Ok(views.html.categories(categories)))
  }


  def addCategoryMenu = Action { implicit request =>
      Ok(views.html.addCategoryMenu(categoryForm))
  }

  def addCategory = Action.async { implicit request =>
    categoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.addCategoryMenu(errorForm))
        )
      },
      category => {
        categoryRepo.create(category.name).map { _ =>
          Redirect(routes.CategoryController.addCategoryMenu()).flashing("success" -> "category created")
        }
      }
    )
  }


  def updateCategoryMenu(id: Integer) = Action.async { implicit request =>
    val kategoria = categoryRepo.getById(id)
    kategoria.map(category => {
      val catForm = updateCategoryForm.fill(UpdateCategoryForm(category.id, category.name))
      Ok(views.html.updateCategoryMenu(catForm))
    })
  }


  def updateCategory = Action.async { implicit request =>

    updateCategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.updateCategoryMenu(errorForm))
        )
      },
      category => {
        categoryRepo.update(category.id, Category(category.id, category.name)).map { _ =>
          Redirect(routes.CategoryController.updateCategoryMenu(category.id)).flashing("success" -> "category updated")
        }
      }
    )
  }

  def removeCategory(id: Int) = Action {
    categoryRepo.delete(id)
    Redirect("/categories")
  }



  def getCategoriesJson = Action.async { implicit request =>
    val kategorie = categoryRepo.list()
    kategorie.map( categories => Ok(Json.toJson(categories)))
  }

  def addCategoryJson: Action[AnyContent] = Action { implicit request =>
    var category:Category = request.body.asJson.get.as[Category]
    categoryRepo.create(category.name)
    Redirect("/categories")
  }

  def updateCategoryMenuJson(id: Integer) = Action.async { implicit request =>
    val kategoria = categoryRepo.getById(id)
    kategoria.map(category => Ok(Json.toJson(category)))
  }

  def updateCategoryJson = Action { implicit request =>
    var category:Category = request.body.asJson.get.as[Category]
    categoryRepo.update(category.id, Category(category.id, category.name))
    Redirect("/categories")
  }

  def removeCategoryJson = Action { implicit request =>
    var category:Category = request.body.asJson.get.as[Category]
    categoryRepo.delete(category.id)
    Redirect("/categories")
  }


}

case class CreateCategoryForm(name: String)
case class UpdateCategoryForm(id: Int, name: String)