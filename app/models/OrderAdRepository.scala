package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class OrderAdRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, orderRepository: OrderRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  class OrderAdTable(tag: Tag) extends Table[OrderAd](tag, "orderad") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def country = column[String]("country")
    def city = column[String]("city")
    def street = column[String]("street")
    def number = column[String]("number")
    def order = column[Int]("order")
    private def order_fk = foreignKey("ord_fk",order, ord)(_.id)

    def * = (id, country, city, street, number, order) <> ((OrderAd.apply _).tupled, OrderAd.unapply)

  }

  import orderRepository.OrderTable

  private val orderad = TableQuery[OrderAdTable]
  private val ord = TableQuery[OrderTable]


  def create(country: String, city: String, street: String, number: String, order: Int ): Future[OrderAd] = db.run {
    (orderad.map(o => (o.country, o.city, o.street, o.number, o.order))
      returning orderad.map(_.id)
      into {case ((country, city, street, number, order),id) => OrderAd(id, country, city, street, number, order)}
      ) += (country, city, street, number, order)
  }


  def list(): Future[Seq[OrderAd]] = db.run {
    orderad.result
  }

  def getByOrder(order_id: Int): Future[Seq[OrderAd]] = db.run {
    orderad.filter(_.order === order_id).result
  }

  def getById(id: Int): Future[OrderAd] = db.run {
    orderad.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[OrderAd]] = db.run {
    orderad.filter(_.id === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run(orderad.filter(_.id === id).delete).map(_ => ())

  def update(id: Int, new_orderad: OrderAd): Future[Unit] = {
    val orderadToUpdate: OrderAd = new_orderad.copy(id)
    db.run(orderad.filter(_.id === id).update(orderadToUpdate)).map(_ => ())
  }



}
