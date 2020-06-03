package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class BasketRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, productRepository: ProductRepository)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class BasketTable(tag: Tag) extends Table[Basket](tag, "basket") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def user = column[String]("user")
    def quantity = column[Int]("quantity")
    def product = column[Int]("product")
    private def productFk = foreignKey("prod_fk", product, prod)(_.id)

    def * = (id, user, quantity, product) <> ((Basket.apply _).tupled, Basket.unapply)

  }

  import productRepository.ProductTable

  private val prod = TableQuery[ProductTable]
  private val basket = TableQuery[BasketTable]

  def create(user: String, quantity: Int, product: Int): Future[Basket] = db.run {
    (basket.map(b => (b.user, b.quantity,b.product))
      returning basket.map(_.id)
      into {case ((user,quantity,product),id) => Basket(id, user, quantity, product)}
      ) += (user, quantity, product)
  }

  def list(): Future[Seq[Basket]] = db.run {
    basket.result
  }

  def getByUser(userId: String): Future[Seq[Basket]] = db.run {
    basket.filter(_.user === userId).result
  }

  def getById(id: Int): Future[Basket] = db.run {
    basket.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Basket]] = db.run {
    basket.filter(_.id === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run(basket.filter(_.id === id).delete).map(_ => ())

  def update(id: Int, newBasket: Basket): Future[Unit] = {
    val basketToUpdate: Basket = newBasket.copy(id)
    db.run(basket.filter(_.id === id).update(basketToUpdate)).map(_ => ())
  }


}
