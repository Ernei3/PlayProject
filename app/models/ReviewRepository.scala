package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class ReviewRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, productRepository: ProductRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  class ReviewTable(tag: Tag) extends Table[Review](tag, "review") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def content = column[String]("content")
    def product = column[Int]("product")
    private def product_fk = foreignKey("prod_fk",product, prod)(_.id)

    def * = (id, title, content, product) <> ((Review.apply _).tupled, Review.unapply)

  }

  import productRepository.ProductTable

  private val prod = TableQuery[ProductTable]
  private val review = TableQuery[ReviewTable]


  def create(title: String, content: String, product: Int): Future[Review] = db.run {
    (review.map(r => (r.title, r.content,r.product))
      returning review.map(_.id)
      into {case ((title,content,product),id) => Review(id, title, content, product)}
      ) += (title, content, product)
  }


  def list(): Future[Seq[Review]] = db.run {
    review.result
  }

  def getByProduct(product_id: Int): Future[Seq[Review]] = db.run {
    review.filter(_.product === product_id).result
  }

  def getById(id: Int): Future[Review] = db.run {
    review.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Review]] = db.run {
    review.filter(_.id === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run(review.filter(_.id === id).delete).map(_ => ())

  def update(id: Int, new_review: Review): Future[Unit] = {
    val reviewToUpdate: Review = new_review.copy(id)
    db.run(review.filter(_.id === id).update(reviewToUpdate)).map(_ => ())
  }

}