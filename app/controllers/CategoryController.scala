package controllers

import javax.inject._
import models.CategoryRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext


class  CategoryController @Inject()(categoryRepo: CategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val categoryForm: Form[CreateCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }


    def categories = Action.async { implicit request =>
      val kategorie = categoryRepo.list()
      kategorie.map( categories => Ok(views.html.categories(categories)))
    }


    def addCategoryMenu = Action { implicit request =>
      Ok(views.html.addCategoryMenu(categoryForm))
  }

    def addCategory = Action {
    Ok(views.html.index("Adding a new category"))
  }

    def getCategory(id: Integer) = Action {
    Ok(views.html.index("Category: "+id))
  }

    def updateCategory = Action {
    Ok(views.html.index("Updating a category"))
  }

    def removeCategory = Action {
    Ok(views.html.index("Removing a category"))
  }

}

case class CreateCategoryForm(name: String)