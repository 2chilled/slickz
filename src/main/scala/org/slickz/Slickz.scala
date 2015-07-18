package org.slickz

import scala.slick.jdbc.JdbcBackend
import scalaz.concurrent.Task

trait Slickz {
  outer =>

  def withSessionPure[T](b: JdbcBackend#Database)(f: JdbcBackend#Session => Task[T]): Task[T] = for {
    session <- Task.delay(b.createSession())
    t <- f(session) onFinish {
      case Some(fException) => Task.delay(session.close()).handle { case _: Throwable => throw fException }
      case None => Task.delay(session.close())
    }
  } yield t

  def withTransactionPure[T](b: JdbcBackend#Database)(f: JdbcBackend#Session => Task[T]): Task[T] =
    withSessionPure(b)(s => Task.delay(s.withTransaction(f(s).run)))

  def withTransactionPure[T](s: JdbcBackend#Session)(work: Task[T]): Task[T] =
    Task.delay(s.withTransaction(work.run))
}

object Slickz extends Slickz
