package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class ProductRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, subRepository: SubcategoryRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  class ProductTable(tag: Tag) extends Table[Product](tag, "product") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def description = column[String]("description")
    def price = column[Int]("price")
    def subcategory = column[Int]("subcategory")
    private def subcategoryFk = foreignKey("sub_fk",subcategory, sub)(_.id)

    def * = (id, name, description, price, subcategory) <> ((Product.apply _).tupled, Product.unapply)

  }

  import subRepository.SubcategoryTable

  private val product = TableQuery[ProductTable]
  private val sub = TableQuery[SubcategoryTable]


  def create(name: String, description: String, price: Int, subcategory: Int): Future[Product] = db.run {
    (product.map(p => (p.name, p.description, p.price, p.subcategory))
      returning product.map(_.id)
      into {case ((name,description,price,subcategory),id) => Product(id,name,description,price,subcategory)}
      ) += (name,description,price,subcategory)
  }


  def list(): Future[Seq[Product]] = db.run {
    product.result
  }

  def getBySub(subcategoryId: Int): Future[Seq[Product]] = db.run {
    product.filter(_.subcategory === subcategoryId).result
  }

  def getById(id: Int): Future[Product] = db.run {
    product.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Product]] = db.run {
    product.filter(_.id === id).result.headOption
  }

  def getBySubs(subcategoryIds: List[Int]): Future[Seq[Product]] = db.run {
    product.filter(_.subcategory inSet subcategoryIds).result
  }

  def delete(id: Int): Future[Unit] = db.run(product.filter(_.id === id).delete).map(_ => ())

  def update(id: Int, newProduct: Product): Future[Unit] = {
    val productToUpdate: Product = newProduct.copy(id)
    db.run(product.filter(_.id === id).update(productToUpdate)).map(_ => ())
  }

}