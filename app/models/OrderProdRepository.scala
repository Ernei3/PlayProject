package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class OrderProdRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, orderRepository: OrderRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  class OrderProdTable(tag: Tag) extends Table[OrderProd](tag, "orderprod") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def price = column[Int]("price")
    def quantity = column[Int]("quantity")
    def order = column[Int]("order")
    private def order_fk = foreignKey("ord_fk",order, ord)(_.id)

    def * = (id, name, price, quantity, order) <> ((OrderProd.apply _).tupled, OrderProd.unapply)

  }

  import orderRepository.OrderTable

  private val orderprod = TableQuery[OrderProdTable]
  private val ord = TableQuery[OrderTable]


  def create(name: String, price: Int, quantity: Int, order: Int ): Future[OrderProd] = db.run {
    (orderprod.map(o => (o.name, o.price, o.quantity, o.order))
      returning orderprod.map(_.id)
      into {case ((name, price, quantity, order),id) => OrderProd(id, name, price, quantity, order)}
      ) += (name, price, quantity, order)
  }


  def list(): Future[Seq[OrderProd]] = db.run {
    orderprod.result
  }

  def getByOrder(order_id: Int): Future[Seq[OrderProd]] = db.run {
    orderprod.filter(_.order === order_id).result
  }

  def getById(id: Int): Future[OrderProd] = db.run {
    orderprod.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[OrderProd]] = db.run {
    orderprod.filter(_.id === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run(orderprod.filter(_.id === id).delete).map(_ => ())

  def update(id: Int, new_orderprod: OrderProd): Future[Unit] = {
    val orderprodToUpdate: OrderProd = new_orderprod.copy(id)
    db.run(orderprod.filter(_.id === id).update(orderprodToUpdate)).map(_ => ())
  }

}
