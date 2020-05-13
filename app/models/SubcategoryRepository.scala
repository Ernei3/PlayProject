package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class SubcategoryRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, categoryRepository: CategoryRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  class SubcategoryTable(tag: Tag) extends Table[Subcategory](tag, "subcategory") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def category = column[Int]("category")
    private def categoryFk = foreignKey("cat_fk",category, cat)(_.id)

    def * = (id, name, category) <> ((Subcategory.apply _).tupled, Subcategory.unapply)

  }

  import categoryRepository.CategoryTable

  private val subcategory = TableQuery[SubcategoryTable]
  private val cat = TableQuery[CategoryTable]


  def create(name: String, category: Int): Future[Subcategory] = db.run {
    (subcategory.map(s => (s.name, s.category))
      returning subcategory.map(_.id)
      into {case ((name,category),id) => Subcategory(id,name,category)}
      ) += (name,category)
  }

  def list(): Future[Seq[Subcategory]] = db.run {
    subcategory.result
  }

  def getByCategory(categoryId: Int): Future[Seq[Subcategory]] = db.run {
    subcategory.filter(_.category === categoryId).result
  }

  def getById(id: Int): Future[Subcategory] = db.run {
    subcategory.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Subcategory]] = db.run {
    subcategory.filter(_.id === id).result.headOption
  }

  def getByCategories(categoryIds: List[Int]): Future[Seq[Subcategory]] = db.run {
    subcategory.filter(_.category inSet categoryIds).result
  }

  def delete(id: Int): Future[Unit] = db.run(subcategory.filter(_.id === id).delete).map(_ => ())

  def update(id: Int, newSubcategory: Subcategory): Future[Unit] = {
    val subToUpdate: Subcategory = newSubcategory.copy(id)
    db.run(subcategory.filter(_.id === id).update(subToUpdate)).map(_ => ())
  }

}