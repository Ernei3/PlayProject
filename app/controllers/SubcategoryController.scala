package controllers

import javax.inject._
import models.{Category, CategoryRepository, Subcategory, SubcategoryRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext


class  SubcategoryController @Inject()(subRepo: SubcategoryRepository, categoryRepo: CategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val subForm: Form[CreateSubForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "category" -> number,
    )(CreateSubForm.apply)(CreateSubForm.unapply)
  }

  val updateSubForm: Form[UpdateSubForm] = Form {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "category" -> number,
    )(UpdateSubForm.apply)(UpdateSubForm.unapply)
  }


  def subcategories = Action.async { implicit request =>

    var categ:Seq[Category] = Seq[Category]()
    val kategorie = categoryRepo.list().onComplete{
      case Success(cat) => categ = cat
      case Failure(_) => print("fail")
    }

    val podkategorie = subRepo.list()
    podkategorie.map( subcategories => Ok(views.html.subcategories(subcategories, categ)))

  }

  def addSubMenu = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val categories = categoryRepo.list()
    categories.map (cat => Ok(views.html.addSubMenu(subForm, cat)))
  }

  def addSub = Action.async { implicit request =>
    var categ:Seq[Category] = Seq[Category]()
    val categories = categoryRepo.list().onComplete{
      case Success(cat) => categ = cat
      case Failure(_) => print("fail")
    }

    subForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.addSubMenu(errorForm, categ))
        )
      },
      subcat => {
        subRepo.create(subcat.name, subcat.category).map { _ =>
          Redirect(routes.SubcategoryController.addSubMenu()).flashing("success" -> "subcategory created")
        }
      }
    )

  }

  def updateSubMenu(id: Int) = Action.async { implicit request: MessagesRequest[AnyContent] =>
      var categ:Seq[Category] = Seq[Category]()
      val categories = categoryRepo.list().onComplete{
        case Success(cat) => categ = cat
        case Failure(_) => print("fail")
      }

      val subkategoria = subRepo.getById(id)
      subkategoria.map(subcategory => {
        val subForm = updateSubForm.fill(UpdateSubForm(subcategory.id, subcategory.name, subcategory.category))
        Ok(views.html.updateSubMenu(subForm, categ))
      })
  }

  def updateSub = Action.async { implicit request =>
    var categ:Seq[Category] = Seq[Category]()
    val categories = categoryRepo.list().onComplete{
      case Success(cat) => categ = cat
      case Failure(_) => print("fail")
    }

    updateSubForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.updateSubMenu(errorForm, categ))
        )
      },
      sub => {
        subRepo.update(sub.id, Subcategory(sub.id, sub.name, sub.category)).map { _ =>
          Redirect(routes.SubcategoryController.updateSubMenu(sub.id)).flashing("success" -> "Subcategory updated.")
        }
      }
    )
  }


  def removeSub(id: Int) = Action {
    subRepo.delete(id)
    Redirect("/subcategories")
  }

}

case class CreateSubForm(name: String, category: Int)
case class UpdateSubForm(id: Int, name: String, category: Int)