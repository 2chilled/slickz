package org.slickz

import org.slickz.Schema.Person
import org.slickz.TestConfig._
import org.slickz.TestConfig.driver._
import org.slickz.syntax._

import scala.slick.jdbc.JdbcBackend
import scalaz.concurrent.Task
import scalaz.std.list._
import scalaz.syntax.apply._
import scalaz.syntax.traverse._

class SlickzTest extends TestConfig {
  val futterer = Person(
    name = "Futterer",
    street = "Futtererstreet",
    city = "Balingen",
    zip = "72336"
  )

  val mueller = Person(
    name = "Müller",
    street = "Müllerstreet",
    city = "Balingen",
    zip = "72336"
  )

  val exception = new Exception("This went wrong")

  def failingTransaction(implicit s: JdbcBackend#Session) = {
    val insertFutterer = Task.delay(Schema.persons += futterer)
    val throwException = Task.delay(throw exception)
    val insertMueller = Task.delay(Schema.persons += mueller)

    List(insertFutterer, throwException, insertMueller).sequenceU
  }

  "Slickz.withSessionPure" should {
    "create a valid session" in {
      val task = db.withSessionPure { implicit session =>
        val insert = Task.delay(Schema.persons += futterer)
        val fetch = Task.delay(Schema.persons.first)

        (insert |@| fetch)((_, p) => p)
      }

      val result = task.run
      db.withSession { implicit session =>
        result shouldEqual Schema.persons.first
      }
    }

    "write to the db even when there occured an error later on" in {
      an[Exception] shouldBe thrownBy(db.withSessionPure(failingTransaction(_)).run)
      db.withSession(implicit s => Schema.persons.firstOption).map(_.copy(id = None)) shouldBe Some(futterer)
    }

    "throw exceptions within the Task Monad" in {
      val task: Task[Unit] = db.withSessionPure { _ =>
        throw exception
      }

      an[Exception] should be thrownBy task.run
      task.handle { case _ => () }.run shouldBe(())
    }
  }

  "Slickz.withTransactionPure" should {
    "rollback the transaction when an error occured" in {
      val testOne = db.withTransactionPure(failingTransaction(_))
      val testTwo = db.withSessionPure(implicit s => s.withTransactionPure(failingTransaction))

      for(t <- List(testOne, testTwo)) {
        t.handle { case _ => Nil }.run shouldBe Nil
        db.withSession(implicit s => Schema.persons.firstOption) shouldBe None
      }
    }
  }
}
