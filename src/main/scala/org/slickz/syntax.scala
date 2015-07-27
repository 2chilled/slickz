package org.slickz

import scala.slick.jdbc.JdbcBackend
import scalaz.concurrent.Task

trait Syntax {

  implicit class JdbcBackendDatabaseSyntax(b: JdbcBackend#Database) {
    def withSessionPure[T](f: JdbcBackend#Session => Task[T]) = Slickz.withSessionPure(b)(f)

    def withTransactionPure[T](f: JdbcBackend#Session => Task[T]) = Slickz.withTransactionPure(b)(f)
  }

  implicit class JdbcBackendSessionSyntax(s: JdbcBackend#Session) {
    def withTransactionPure[T](work: Task[T]) = Slickz.withTransactionPure(s)(work)
  }

}

object syntax extends Syntax
