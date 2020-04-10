package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class WishlistRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, productRepository: ProductRepository)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class WishlistTable(tag: Tag) extends Table[Wishlist](tag, "wishlist") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def user = column[Int]("user")

    def quantity = column[Int]("quantity")

    def product = column[Int]("product")

    private def product_fk = foreignKey("prod_fk", product, prod)(_.id)

    def * = (id, user, quantity, product) <> ((Wishlist.apply _).tupled, Wishlist.unapply)

  }

    import productRepository.ProductTable

    private val prod = TableQuery[ProductTable]
    private val wishlist = TableQuery[WishlistTable]

    def create(user: Int, quantity: Int, product: Int): Future[Wishlist] = db.run {
      (wishlist.map(w => (w.user, w.quantity,w.product))
        returning wishlist.map(_.id)
        into {case ((user,quantity,product),id) => Wishlist(id, user, quantity, product)}
        ) += (user, quantity, product)
    }

    def list(): Future[Seq[Wishlist]] = db.run {
      wishlist.result
    }

    def getByUser(user_id: Int): Future[Seq[Wishlist]] = db.run {
      wishlist.filter(_.user === user_id).result
    }

    def getById(id: Int): Future[Wishlist] = db.run {
      wishlist.filter(_.id === id).result.head
    }

    def getByIdOption(id: Int): Future[Option[Wishlist]] = db.run {
      wishlist.filter(_.id === id).result.headOption
    }

    def delete(id: Int): Future[Unit] = db.run(wishlist.filter(_.id === id).delete).map(_ => ())

    def update(id: Int, new_wish: Wishlist): Future[Unit] = {
      val wishToUpdate: Wishlist = new_wish.copy(id)
      db.run(wishlist.filter(_.id === id).update(wishToUpdate)).map(_ => ())
    }


}
