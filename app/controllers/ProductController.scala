package controllers

import javax.inject._
import models.{Product, ProductRepository, Subcategory, SubcategoryRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
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
    val podkategorie = subRepo.list()
    val subcateg = Await.result(podkategorie, Duration.Inf)

    val produkty = productsRepo.list()
    produkty.map( products => Ok(views.html.products(products, subcateg)))
  }


  def productsBySub(subId: Int) = Action.async { implicit request =>

    val podkategoria = subRepo.getById(subId)
    val produkty = productsRepo.getBySub(subId)
    val subcateg = Await.result(podkategoria, Duration.Inf)
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
    val podkategorie = subRepo.list()
    val subcateg = Await.result(podkategorie, Duration.Inf)

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
    val podkategorie = subRepo.list()
    val subcateg = Await.result(podkategorie, Duration.Inf)

    val produkt = productsRepo.getById(id)
    produkt.map(product => {
      val prodForm = updateProductForm.fill(UpdateProductForm(product.id, product.name, product.description, product.price, product.subcategory))
      Ok(views.html.updateProductMenu(prodForm, subcateg))
    })
  }

  def updateProduct = Action.async { implicit request =>
    val podkategorie = subRepo.list()
    val subcateg = Await.result(podkategorie, Duration.Inf)

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




  def getProductsJson: Action[AnyContent] = Action.async { implicit request =>

    val produkty = productsRepo.list()
    produkty.map( products => Ok(Json.toJson(products)))
  }

  def productsBySubJson(subId: Int) = Action.async { implicit request =>

    val produkty = productsRepo.getBySub(subId)
    produkty.map( products => Ok(Json.toJson(products)))
  }

  def productDetailsJson(id: Int) = Action.async { implicit request =>
    val produkt = productsRepo.getByIdOption(id)
    produkt.map(product => product match {
      case Some(p) => Ok(Json.toJson(p))
      case None => Redirect(routes.ProductController.getProductsJson())
    })
  }

  def addProductJson: Action[AnyContent] = Action { implicit request =>
    var product:Product = request.body.asJson.get.as[Product]
    productsRepo.create(product.name, product.description, product.price, product.subcategory)
    Redirect("/products")
  }

  def updateProductMenuJson(id: Int) = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val produkt = productsRepo.getById(id)
    produkt.map(product => Ok(Json.toJson(product)))
  }

  def updateProductJson: Action[AnyContent] = Action { implicit request =>
    var product:Product = request.body.asJson.get.as[Product]
    productsRepo.update(product.id, Product(product.id, product.name, product.description, product.price, product.subcategory))
    Redirect("/products")
  }


  def removeProductJson: Action[AnyContent] = Action { implicit request =>
    var product:Product = request.body.asJson.get.as[Product]
    productsRepo.delete(product.id)
    Redirect("/products")
  }


}

case class CreateProductForm(name: String, description: String, price: Int, subcategory: Int)
case class UpdateProductForm(id: Int, name: String, description: String, price: Int, subcategory: Int)