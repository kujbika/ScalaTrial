package exercises

import lectures.part4implicits.TypeClasses.User

object EqualityPlayground extends App {

  /* INGREDIENTS FOR TYPE CLASSES
   * - type class itself HTMLSerializer[T]
   * - type class instances (some of them are implicits) UserSerializer, IntSerializer
   * - conversion with implicit classes HTMLEnrichment[T]
   */
  trait Equality[T] {
    def apply(a: T, b: T): Boolean
  }

  object Equality {
    def apply[T](a: T, b: T)(implicit instance: Equality[T]) = instance(a, b)
  }

  implicit object NameEquality extends Equality[User] {
    def apply(a: User, b: User): Boolean = a.name == b.name
  }
  object FullEquality extends Equality[User] {
    def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }
  implicit object SetEquality extends Equality[MySet[Int]] {
    def apply(a: MySet[Int], b: MySet[Int]): Boolean = (a -- b).isEmpty && (b -- a).isEmpty
  }

  implicit class TypeSafeEqual[T](value: T) {
    def ===(anotherValue: T)(implicit equalizer: Equality[T]) = equalizer(value, anotherValue)
    def !==(anotherValue: T)(implicit equalizer: Equality[T]) = !equalizer(value, anotherValue)
  }

  val john        = User("john", 32, "asd@gmail.com")
  val anotherJohn = User("john", 32, "joghn2@gmail.com")
  println(Equality(john, anotherJohn)) // AD-HOC polymorphism

  println(john === anotherJohn)
  val set1 = MySet(1, 2, 3)
  val set2 = MySet(2, 3)
  println(set1 === set2)
  println(set1 !== set2)

  /*
   * TYPE SAFE
   */
  println(john == 43) // no compile error just a warning
  // println(john === 43) // compile error
}
