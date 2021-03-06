package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, orderAdRepository: OrderAdRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class OrderTable(tag: Tag) extends Table[Order](tag, "order") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def user = column[String]("user")
    def status = column[String]("status")
    def address = column[Int]("address")
    def * = (id, user, status, address) <> ((Order.apply _).tupled, Order.unapply)
  }

  import orderAdRepository.OrderAdTable

  val order = TableQuery[OrderTable]
  private val orderad = TableQuery[OrderAdTable]

  def create(user: String, status: String, address: Int): Future[Order] = db.run {
    (order.map(o => (o.user, o.status, o.address))
      returning order.map(_.id)
      into {case ((user, status, address),id) => Order(id, user, status, address)}
      ) += (user, status, address)
  }

  def list(): Future[Seq[Order]] = db.run {
    order.result
  }

  def getByUser(userId: String): Future[Seq[Order]] = db.run {
    order.filter(_.user === userId).result
  }

  def getById(id: Int): Future[Order] = db.run {
    order.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Order]] = db.run {
    order.filter(_.id === id).result.headOption
  }


  def delete(id: Int): Future[Unit] = db.run(order.filter(_.id === id).delete).map(_ => ())

  def update(id: Int, newOrder: Order): Future[Unit] = {
    val orderToUpdate: Order = newOrder.copy(id)
    db.run(order.filter(_.id === id).update(orderToUpdate)).map(_ => ())
  }


}