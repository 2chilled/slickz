package org.slickz

import org.scalatest.prop.PropertyChecks
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

import scala.slick.jdbc.JdbcBackend

object TestConfig {
  val driver = scala.slick.driver.H2Driver.simple
  val db = JdbcBackend.Database.forURL(
    driver = "org.h2.Driver",
    url = "jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1"
  )
}

trait TestConfig extends WordSpec
with PropertyChecks with Matchers with BeforeAndAfter {

  import TestConfig._
  import driver._

  before {
    db.withSession { implicit session =>
      Schema.persons.ddl.create.run
    }
  }

  after {
    db.withSession { implicit session =>
      Schema.persons.ddl.drop.run
    }
  }
}
