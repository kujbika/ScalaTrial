package exercises

import lectures.part4implicits.TypeClasses.User

object EqualityPlayground extends App {

  /**
    * Exercise: Equality type class
    */
  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  implicit object NameEquality extends Equal[User] {
    def apply(a: User, b: User): Boolean = a.name == b.name
  }
  implicit object SetEquality extends Equal[MySet[Int]] {
    def apply(a: MySet[Int], b: MySet[Int]): Boolean = (a -- b).isEmpty && (b -- a).isEmpty
  }
  object FullEquality extends Equal[User] {
    def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }

  /*
   * Exercise: implement the TC pattern for the Equality TC
   */
  object Equal {
    def apply[T](a: T, b: T)(implicit instance: Equal[T]) = instance.equals(a, b)
  }

  val john        = User("john", 32, "asd@gmail.com")
  val anotherJohn = User("john", 32, "joghn2@gmail.com")
  println(Equal(john, anotherJohn)) // AD-HOC polymorphism

  /*
   * Exercise: improve the Equal TC with an implicit conversion class
   * ===(another value: T)
   * !==(another value: T)
   */

  implicit class TypeSafeEqual[T](value: T) {
    def ===(anotherValue: T)(implicit equalizer: Equal[T]) = equalizer(value, anotherValue)
    def !==(anotherValue: T)(implicit equalizer: Equal[T]) = !equalizer(value, anotherValue)
  }
  println(john === anotherJohn)
  val set1 = MySet(1, 2, 3)
  val set2 = MySet(2, 3)
  println(set1 === set2)
  println(set1 !== set2)

  /*
   * TYPE SAGE
   */
  println(john == 43) // no compile error
  // println(john === 43) // compile error
}
