package lectures.part2fp

object Monads extends App {
  // simple implementation of the try monad

  trait MyTry[+A] {
    def flatMap[B](f: A => MyTry[B]): MyTry[B]
  }

  object MyTry {
    def apply[A](a: => A): MyTry[A] =
      try {
        Succes(a)
      } catch {
        case e: Throwable => Fail(e)
      }
  }

  case class Succes[+A](value: A) extends MyTry[A] {
    def flatMap[B](f: A => MyTry[B]): MyTry[B] =
      try {
        f(value)
      } catch {
        case e: Throwable => Fail(e)
      }
  }

  case class Fail(e: Throwable) extends MyTry[Nothing] {
    def flatMap[B](f: Nothing => MyTry[B]): MyTry[B] = this
  }
  /*
   * this was the implmenetation of the try monad
   * the remaining is to prove the monad laws
   * maybe well do in the testing section
   * but maybe not :)
   */

  val attempt = MyTry {
    throw new RuntimeException("My own monad yes!")
  }
  println(attempt)
  /* Exercises
 * 1. lazy monad implementation
 * 2. given a monad defined by apply and flatMap, how would you define
 *    map and flatten
 *    ANSWERS: map(x => f(x)) = flatmap(x => unit(f(x))) = flatmap(x => apply(f(x)))
 *            flatten(A: Monad[Monad[T]]): Monad[T] = flatMap(x => x)
 */
}
