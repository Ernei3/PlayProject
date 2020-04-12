package controllers

import javax.inject._
import models.{Subcategory, SubcategoryRepository, Product, ProductRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}




@Singleton
class ProductController @Inject()(productsRepo: ProductRepository, subRepo: SubcategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "price" -> number,
      "subcategory" -> number
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "price" -> number,
      "subcategory" -> number
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  def products: Action[AnyContent] = Action.async { implicit request =>
    var subcateg:Seq[Subcategory] = Seq[Subcategory]()
    val podkategorie = subRepo.list().onComplete{
      case Success(sub) => subcateg = sub
      case Failure(_) => print("fail")
    }

    val produkty = productsRepo.list()
    produkty.map( products => Ok(views.html.products(products, subcateg)))
  }


  def productsBySub(subId: Int) = Action.async { implicit request =>
    var subcateg:Seq[Subcategory] = Seq[Subcategory]()
    val podkategorie = subRepo.list().onComplete{
      case Success(sub) => subcateg = sub
      case Failure(_) => print("fail")
    }

    val produkty = productsRepo.getBySub(subId)
    produkty.map( products => Ok(views.html.productsBySub(products, subcateg)))
  }

  def productDetails(id: Int) = Action.async { implicit request =>
    val produkt = productsRepo.getByIdOption(id)
    produkt.map(product => product match {
      case Some(p) => Ok(views.html.productDetails(p))
      case None => Redirect(routes.ProductController.products())
    })
  }

  def addProductMenu: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val subcategories = subRepo.list()
    subcategories.map (sub => Ok(views.html.addProductMenu(productForm, sub)))
  }

  def addProduct: Action[AnyContent] = Action.async { implicit request =>
    var subcateg:Seq[Subcategory] = Seq[Subcategory]()
    val subcategories = subRepo.list().onComplete{
      case Success(sub) => subcateg = sub
      case Failure(_) => print("fail")
    }

    productForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.addProductMenu(errorForm, subcateg))
        )
      },
      product => {
        productsRepo.create(product.name, product.description, product.price, product.subcategory).map { _ =>
          Redirect(routes.ProductController.addProductMenu()).flashing("success" -> "product created")
        }
      }
    )
  }

  def updateProductMenu(id: Int) = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var subcateg:Seq[Subcategory] = Seq[Subcategory]()
    val subcategories = subRepo.list().onComplete{
      case Success(sub) => subcateg = sub
      case Failure(_) => print("fail")
    }

    val produkt = productsRepo.getById(id)
    produkt.map(product => {
      val prodForm = updateProductForm.fill(UpdateProductForm(product.id, product.name, product.description, product.price, product.subcategory))
      Ok(views.html.updateProductMenu(prodForm, subcateg))
    })
  }

  def updateProduct = Action.async { implicit request =>
    var subcateg:Seq[Subcategory] = Seq[Subcategory]()
    val subcategories = subRepo.list().onComplete{
      case Success(sub) => subcateg = sub
      case Failure(_) => print("fail")
    }

    updateProductForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.updateProductMenu(errorForm, subcateg))
        )
      },
      product => {
        productsRepo.update(product.id, Product(product.id, product.name, product.description, product.price, product.subcategory)).map { _ =>
          Redirect(routes.ProductController.updateProductMenu(product.id)).flashing("success" -> "product updated")
        }
      }
    )
  }

  def removeProduct(id: Int) = Action {
    productsRepo.delete(id)
    Redirect("/products")
  }


}

case class CreateProductForm(name: String, description: String, price: Int, subcategory: Int)
case class UpdateProductForm(id: Int, name: String, description: String, price: Int, subcategory: Int)