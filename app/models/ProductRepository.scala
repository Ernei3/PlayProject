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
    def subcategory = column[Int]("subcategory")
    private def subcategory_fk = foreignKey("sub_fk",subcategory, sub)(_.id)

    def * = (id, name, description, subcategory) <> ((Product.apply _).tupled, Product.unapply)

  }

  import subRepository.SubcategoryTable

  private val product = TableQuery[ProductTable]
  private val sub = TableQuery[SubcategoryTable]


  def create(name: String, description: String, subcategory: Int): Future[Product] = db.run {
    (product.map(p => (p.name, p.description,p.subcategory))
      returning product.map(_.id)
      into {case ((name,description,subcategory),id) => Product(id,name, description,subcategory)}
      ) += (name, description,subcategory)
  }


  def list(): Future[Seq[Product]] = db.run {
    product.result
  }

  def getBySub(subcategory_id: Int): Future[Seq[Product]] = db.run {
    product.filter(_.subcategory === subcategory_id).result
  }

  def getById(id: Int): Future[Product] = db.run {
    product.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Product]] = db.run {
    product.filter(_.id === id).result.headOption
  }

  def getBySubs(subcategory_ids: List[Int]): Future[Seq[Product]] = db.run {
    product.filter(_.subcategory inSet subcategory_ids).result
  }

  def delete(id: Int): Future[Unit] = db.run(product.filter(_.id === id).delete).map(_ => ())

  def update(id: Int, new_product: Product): Future[Unit] = {
    val productToUpdate: Product = new_product.copy(id)
    db.run(product.filter(_.id === id).update(productToUpdate)).map(_ => ())
  }

}