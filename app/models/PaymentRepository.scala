package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class PaymentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, orderRepository: OrderRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  class PaymentTable(tag: Tag) extends Table[Payment](tag, "payment") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def number = column[String]("number")
    def name = column[String]("name")
    def date = column[String]("date")
    def code = column[String]("code")
    def order = column[Int]("order")
    private def orderFk = foreignKey("ord_fk",order, ord)(_.id)

    def * = (id, number, name, date, code, order) <> ((Payment.apply _).tupled, Payment.unapply)

  }

  import orderRepository.OrderTable

  private val payment = TableQuery[PaymentTable]
  private val ord = TableQuery[OrderTable]


  def create(number: String, name: String, date: String, code: String, order: Int ): Future[Payment] = db.run {
    (payment.map(p => (p.number, p.name, p.date, p.code, p.order))
      returning payment.map(_.id)
      into {case ((number, name, date, code, order),id) => Payment(id, number, name, date, code, order)}
      ) += (number, name, date, code, order)
  }


  def list(): Future[Seq[Payment]] = db.run {
    payment.result
  }

  def getByOrder(orderId: Int): Future[Seq[Payment]] = db.run {
    payment.filter(_.order === orderId).result
  }

  def getById(id: Int): Future[Payment] = db.run {
    payment.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Payment]] = db.run {
    payment.filter(_.id === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run(payment.filter(_.id === id).delete).map(_ => ())

  def update(id: Int, newPayment: Payment): Future[Unit] = {
    val paymentToUpdate: Payment = newPayment.copy(id)
    db.run(payment.filter(_.id === id).update(paymentToUpdate)).map(_ => ())
  }


}
