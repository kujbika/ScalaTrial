package exercises


import scala.concurrent.Promise
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try
import scala.util.Random
object FutureExercises extends App {

  /**
    * 1) fulfill a future immediately with a value
    * 2) inSequence(fa, fb) does fb after fa has completed
    * 3) first(fa, fb) => new future depending which finishes first
    * 4) last(fa, fb) => new future with the last value
    * 5) retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T]
    */
  def fulfillImmediately[T](value: T): Future[T] = Future(value)

  def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] = first.flatMap(_ => second)

  // first out of two futures
  //promise signature
  def first[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val promise = Promise[A]

    // def tryComplete(promise: Promise[A], result: Try[A]) = result match {
    //   case Success(r) =>
    //     try {
    //       promise.success(r)
    //     } catch {
    //       case _: Throwable =>
    //     }
    //   case Failure(t) => promise.failure(t)
    // }

    // instead of this, there is a builtin complete function of promise
    // fa.onComplete(result => tryComplete(promise, result))
    // fb.onComplete(result => tryComplete(promise, result))
    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)

    promise.future
  }

  def lastFuture[A](futures: List[Future[A]]): Future[A] = {
    // all try to fulfill the upcoming future
    // that gets returned, who fulfills the last

    val allPromise = futures.map(_ => Promise[A])
    @scala.annotation.tailrec
    def checkAndComplete(result: Try[A], promises: List[Promise[A]]): Unit = {
      if (promises.tail == Nil) promises.head.complete(result)
      else
      if (!promises.head.tryComplete(result)) checkAndComplete(result, promises.tail)
    }

    futures.foreach(_.onComplete(result => checkAndComplete(result, allPromise)))

    allPromise.reverse.head.future
  }

  val fa: Future[Int] = Future {
    Thread.sleep(100)
    println("first future")
    42
  }
  val fb: Future[Int] = Future {
    Thread.sleep(200)
    println("second future")
    45
  }
  val fc: Future[Int] = Future {
    Thread.sleep(250)
    println("third future")
    50
  }

  lastFuture(List(fa, fb, fc)).foreach(result => println("I am the last future, and my value is: " + result))

  Thread.sleep(1000)


  def retryUntil[T](condition: T => Boolean)(body: => Future[T]): Future[T] = {
    body.filter(condition).recoverWith {
      case _ => retryUntil(condition)(body)
    }
  }

  retryUntil((x: Int) => x < 10) {
    val random = new Random
    Future {
      Thread.sleep(100)
      val nextValue = random.nextInt(100)
      println("generated " + nextValue)
      nextValue
    }
  }.foreach(result => println("settled at" + result))
  Thread.sleep(10000)
}

object FuturesTry extends App {
  val f = Future {
    (1 to 2000).foreach(println)
  }
  (-1000 to 1000).foreach(println)
  f.onComplete(_ => println("succesfull"))

  // in sbt i dont need to invoke thread sleep here
}
