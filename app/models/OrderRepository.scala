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
    def user = column[Int]("user")
    def status = column[String]("status")
    def address = column[Int]("address")
    private def address_fk = foreignKey("address_fk", address, orderad)(_.id)
    def * = (id, user, status, address) <> ((Order.apply _).tupled, Order.unapply)
  }

  import orderAdRepository.OrderAdTable

  val order = TableQuery[OrderTable]
  private val orderad = TableQuery[OrderAdTable]

  def create(user: Int, status: String, address: Int): Future[Order] = db.run {
    (order.map(o => (o.user, o.status, o.address))
      returning order.map(_.id)
      into {case ((user, status, address),id) => Order(id, user, status, address)}
      ) += (user, status, address)
  }

  def list(): Future[Seq[Order]] = db.run {
    order.result
  }

  def getByUser(user_id: Int): Future[Seq[Order]] = db.run {
    order.filter(_.user === user_id).result
  }

  def getById(id: Int): Future[Order] = db.run {
    order.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Order]] = db.run {
    order.filter(_.id === id).result.headOption
  }


  def delete(id: Int): Future[Unit] = db.run(order.filter(_.id === id).delete).map(_ => ())

  def update(id: Int, new_order: Order): Future[Unit] = {
    val orderToUpdate: Order = new_order.copy(id)
    db.run(order.filter(_.id === id).update(orderToUpdate)).map(_ => ())
  }


}