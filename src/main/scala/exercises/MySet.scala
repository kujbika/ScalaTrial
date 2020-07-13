package exercises

import scala.annotation.tailrec
import com.typesafe.scalalogging.LazyLogging

/** Own implementation of the functional Set collection
  *  (This is to try the Scaladoc.)
  *  (Hit K when at a MySet implementation, and see this doc!)
  *  For other Scaladoc types, see https://docs.scala-lang.org/style/scaladoc.html
  */
trait MySet[A] extends (A => Boolean) with LazyLogging {
  /*
    Exercise - implement a functional set
   */
  def apply(v1: A): Boolean = contains(v1)
  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A] // Union

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(predicate: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit
  /*
    Exercise
      removing an element
      intersection with another set
      difference with another set
   */
  def -(elem: A): MySet[A]
  def &(anotherSet: MySet[A]): MySet[A] =
    filter(anotherSet) // filter(x => anotherSet.contains(x))
  def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)
  def isEmpty: Boolean

  // exercise: implement unary_! that is the complement of a set!
  // basically implement the sigma algebra properties
  def unary_! : MySet[A]
  // possibly infinite collections
  final override def equals(x: Any): Boolean = {
    val that = x.asInstanceOf[MySet[A]]
    if (that == null) false
    else (this -- that).isEmpty && (that -- this).isEmpty
  }

}

class EmptySet[A] extends MySet[A] {
  override def apply(v1: A): Boolean = false
  override def isEmpty: Boolean = true
  override def contains(elem: A): Boolean = false

  override def +(elem: A): MySet[A] = NonEmptySet[A](elem, this)
  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  override def filter(predicate: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = ()

  override def -(elem: A): MySet[A] = this

  override def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)

}

// all elements of type A satisying property
// {x in A | property(x)}
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {

  override def contains(elem: A): Boolean = property(elem)

  override def +(elem: A): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || x == elem)

  override def ++(anotherSet: MySet[A]): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || anotherSet(x))

  override def map[B](f: A => B): MySet[B] = politelyFail

  override def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail

  override def foreach(f: A => Unit): Unit = politelyFail

  override def filter(predicate: A => Boolean): MySet[A] =
    new PropertyBasedSet[A](x => property(x) && predicate(x))

  override def -(elem: A): MySet[A] = filter(x => x != elem)

  override def isEmpty: Boolean = false

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

  def politelyFail =
    throw new IllegalArgumentException("Really deep rabbit hole")

}
case class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  override def isEmpty: Boolean = false
  override def contains(elem: A): Boolean =
    elem == head || (tail contains elem)

  override def +(elem: A): MySet[A] =
    if (this contains elem) this
    else NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] =
    tail ++ anotherSet + head
  /*
    [1 2, 3] ++ [4 5] = [2 3] ++ [4 5] + 1 =
   */

  override def map[B](f: A => B): MySet[B] = (tail map f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] =
    (tail flatMap f) ++ f(head)

  override def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }
  override def -(elem: A): MySet[A] =
    if (this contains elem) {
      if (head == elem) tail else NonEmptySet(head, tail - elem)
    } else this

  override def unary_! : MySet[A] = new PropertyBasedSet(x => !this.contains(x))

}

object MySet {
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)

    buildSet(values.toSeq, new EmptySet[A])
  }
}

object Play extends App {
  val firstSet: MySet[Int] = MySet(1, 2, 3)
  val secondSet: MySet[Int] = MySet(1, 4, 5, 3)
  val is: MySet[Int] = firstSet & secondSet
  // println(firstSet contains 2)
  is foreach println
  secondSet - 3 foreach println
  println("____")
  // println
  //(secondSet intersect firstSet) foreach(println)
  (secondSet - 1) foreach (println)
  println("now comes the difference")
  (secondSet -- firstSet) foreach println
  println("asdasdasdasda")
  (firstSet - 2) foreach println

  println("the new functional style")
  val negative = !firstSet // all the naturals not equal to 1,2,3
  println(negative(2))
  println(negative(6))

  val negativeEven = negative.filter(_ % 2 == 0)
  println(negativeEven(5))

  val negativeEven5 = negativeEven + 5
  println(negativeEven5(5))
}
