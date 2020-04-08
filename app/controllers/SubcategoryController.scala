package controllers

import javax.inject._
import models.{Category, CategoryRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext


class  SubcategoryController @Inject()(categoryRepo: CategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def subcategories = Action {
    Ok(views.html.index("Subcategories"))
  }

  def getSub(id: Int) = Action {
    Ok(views.html.index("Subcategory: "+id))
  }

  def addSubMenu = Action {
    Ok(views.html.index("Menu to add a subcategory"))
  }

  def addSub = Action {
    Ok(views.html.index("Adding a subcategory"))
  }

  def updateSub = Action {
    Ok(views.html.index("Updating a subcategory"))
  }

  def removeSub = Action {
    Ok(views.html.index("Removing a subcategory"))
  }

}
