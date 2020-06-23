package exercises

import scala.annotation.tailrec

/** A stream is a special type of a collection where
  *  - the head is always accessible
  *  - the tail is lazily evaluated
  *  Thus it is highly efficient when working with infinite collections
  */
abstract class MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B] // prepend operator
  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] // concatenate

  def foreach(f: A => Unit): Unit
  def map[B](f: A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]): MyStream[B]
  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // takes the firs n elements out of the this stream
  def takeAsList(n: Int): List[A] = take(n).toList()

  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] =
    if (isEmpty) acc.reverse
    else tail.toList(head :: acc)
}

object EmptyStream extends MyStream {

  override def #::[B](element: B): MyStream[B] =
    new MyStreamFactory(element, this)

  override def ++[B](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

  override def foreach(f: Nothing => Unit): Unit = ()

  override def map[B](f: Nothing => B): MyStream[B] = this

  override def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

  override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  override def take(n: Int): MyStream[Nothing] = this

  override def takeAsList(n: Int): List[Nothing] = Nil

  def isEmpty = true
  def head = throw new NoSuchElementException
  def tail = throw new NoSuchElementException
}

class MyStreamFactory[+A](h: A, t: => MyStream[A]) extends MyStream[A] {

  def isEmpty: Boolean = false

  override val head: A = h

  override lazy val tail: MyStream[A] = t // CALL BY NEED TECHNIQUE

  /*
   *val s = new MyStreamFactory(1, EmptyStream)
    val prepended = 1 #:: s = new MyStreamFactory(1, s) lazy evaluation preserved
   */
  def #::[B >: A](element: B): MyStream[B] = new MyStreamFactory(element, this)

  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] =
    new MyStreamFactory(head, tail ++ anotherStream) // ++ preserves lazyness

  def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  /*
   s = new MyStreamFactory(1, ?)
   mapped = s.map(_ + 1) = new MyStreamFactory(2, s.tail.map(_ + 1))
   BUT ONLY GETS EVALUATED IF I CALL mapped.tail !!
   */

  def map[B](f: A => B): MyStream[B] = new MyStreamFactory(f(head), tail map f)

  def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)

  def filter(predicate: A => Boolean): MyStream[A] =
    if (predicate(head)) new MyStreamFactory(head, tail.filter(predicate))
    else tail.filter(predicate) // preserves lazyness!

  def take(n: Int): MyStream[A] =
    if (n <= 0) EmptyStream
    else if (n == 1) new MyStreamFactory(head, EmptyStream)
    else new MyStreamFactory(head, tail.take(n - 1)) // preserves lazyness

}

/** Factory for MyStream instances. */
object MyStream {

  /** MyStream.from(1)(x => x + 1) = stream of naturals
    *  naturals.take(100).foreach(println) lazily evaluated stream of the first 100 naturals
    *  naturals.foreach(println) CRASH!
    *  naturals.map(_ * 2) stream of all even numbers
    */
  def from[A](start: A)(genearator: A => A): MyStream[A] =
    new MyStreamFactory(start, MyStream.from(genearator(start))(genearator))

  def from[A](
      start: MyStream[A]
  )(generator: MyStream[A] => A): MyStream[A] =
    MyStream.from(generator(start) #:: start)(generator)
}

object StreamsPlayground extends App {
  val naturals = MyStream.from(1)(_ + 1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  // tests from now on
  val startFrom0 = 0 #:: naturals // naturals.#::(0)
  println(startFrom0.head)

  println(naturals.take(10000).foreach(println))
  naturals.map(_ * 2).take(100).foreach(println)
  println(naturals.map(_ * 2).takeAsList(100))

  println(
    naturals
      .flatMap(x =>
        new MyStreamFactory(x, new MyStreamFactory(x + 1, EmptyStream))
      )
      .take(10)
      .toList()
  )

  println(
    naturals.filter(_ < 10).take(9).toList()
  ) // it cant be greater than the number in the filter func

  // exercises on stream
  // 1 - stream of Fibonacci numbers
  // 2 - stream of prime numbers with Eratosthenes' sieve

  val starter =
    new MyStreamFactory(1, new MyStreamFactory(1, EmptyStream))
  val fibonacci =
    MyStream.from(starter)(stream => stream.head + stream.tail.head)
  println(fibonacci.take(10).toList())
}
