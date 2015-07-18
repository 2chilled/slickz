package org.slickz

object Schema {
  import TestConfig.driver._

  class PersonT(tag: Tag) extends Table[Person](tag, "PERSON") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc) // This is the primary key column
    def name = column[String]("name")
    def street = column[String]("street")
    def city = column[String]("city")
    def zip = column[String]("zip")
    def * = (id.?, name, street, city, zip) <> (Person.tupled, Person.unapply)
  }
  val persons = TableQuery[PersonT]

  case class Person(id: Option[Int] = None, name: String, street: String, city: String, zip: String)
}
