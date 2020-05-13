package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class OrderAdRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  class OrderAdTable(tag: Tag) extends Table[OrderAd](tag, "orderad") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def country = column[String]("country")
    def city = column[String]("city")
    def street = column[String]("street")
    def number = column[String]("number")
    def * = (id, country, city, street, number) <> ((OrderAd.apply _).tupled, OrderAd.unapply)

  }

  private val orderad = TableQuery[OrderAdTable]


  def create(country: String, city: String, street: String, number: String ): Future[OrderAd] = db.run {
    (orderad.map(o => (o.country, o.city, o.street, o.number))
      returning orderad.map(_.id)
      into {case ((country, city, street, number),id) => OrderAd(id, country, city, street, number)}
      ) += (country, city, street, number)
  }


  def list(): Future[Seq[OrderAd]] = db.run {
    orderad.result
  }

  def getById(id: Int): Future[OrderAd] = db.run {
    orderad.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[OrderAd]] = db.run {
    orderad.filter(_.id === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run(orderad.filter(_.id === id).delete).map(_ => ())

  def update(id: Int, newOrderad: OrderAd): Future[Unit] = {
    val orderadToUpdate: OrderAd = newOrderad.copy(id)
    db.run(orderad.filter(_.id === id).update(orderadToUpdate)).map(_ => ())
  }



}
