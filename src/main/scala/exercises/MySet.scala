package exercises

import scala.annotation.tailrec
import com.typesafe.scalalogging.LazyLogging

trait MySet[A] extends (A => Boolean) with LazyLogging {
  /*
    Exercise - implement a functional set 
  */
  def apply(v1: A): Boolean = contains(v1) 
  def contains(elem: A):Boolean
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
  def remove(elem: A ): MySet[A]
  def intersect(anotherSet: MySet[A]): MySet[A]
  def difference(anotherSet: MySet[A]): MySet[A]
  def isEmpty: Boolean
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

  override def remove(elem: A): MySet[A] = new EmptySet[A]

  override def intersect(anotherSet: MySet[A]): MySet[A] = new EmptySet[A]

  override def difference(anotherSet: MySet[A]): MySet[A] = new EmptySet[A]

  def equals(x: MySet[A]): Boolean = x.isEmpty
    

}

case class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  def equals(that: MySet[A]): Boolean = that match {
    case NonEmptySet(_,_) => (this difference that).isEmpty && (that difference this).isEmpty
    case _ => false
  }
  override def isEmpty: Boolean = false
  override def contains(elem: A): Boolean = 
    elem == head || (tail contains elem)

  override def +(elem: A) : MySet[A] = 
    if (this contains elem) this 
    else NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = 
    tail ++ anotherSet + head
  /*
    [1 2, 3] ++ [4 5] = [2 3] ++ [4 5] + 1 = 
  */

  override def map[B](f: A => B): MySet[B] = (tail map f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] = (tail flatMap f) ++ f(head)

  override def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }
  override def remove(elem: A): MySet[A] = 
    if (this contains elem) { if(head == elem) tail else NonEmptySet(head, tail.remove(elem)) }
    else this

  override def intersect(anotherSet: MySet[A]): MySet[A] = 
    if (anotherSet contains head) tail.intersect(anotherSet) + head
    else tail.intersect(anotherSet)

  override def difference(anotherSet: MySet[A]): MySet[A] = {
    val intersection: MySet[A] = this intersect anotherSet
    intersection match {
    case NonEmptySet(head, tail) => (this remove head) difference anotherSet
    case _ => this
    }
  }

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
  val is: MySet[Int] = firstSet intersect secondSet
  // println(firstSet contains 2)
  is foreach println
  secondSet remove 3 foreach println
  println("____")
  // println
  //(secondSet intersect firstSet) foreach(println)
  (secondSet remove 1) foreach(println)
  println("now comes the difference")
  (secondSet difference firstSet) foreach println
  println("asdasdasdasda")
  (firstSet remove 2) foreach println

}
