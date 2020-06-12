package lectures.part1

object AdvancedPatternMatching extends App{
 val numbers = List(1)
 val description: Unit = numbers match {
  case head :: Nil => println(s"only element is $head")
  case _ =>
 }

 // pattern match for not case classes
 class Person(val name: String, val age: Int)

 object Person {
  def unapply(person: Person): Option[(String, Int)] = Some((person.name, person.age))
  def unapply(age: Int): Option[String] = Some(if (age < 21) "minor" else "major") // overload unapply
 }
 // we dont need to name the object Person, it can be anything
 val bob = new Person("bob", 10)
 val greeting = bob match {
  case Person(n, a) => s"Hi, my name is $n and i am $a years old"
 }
 println(greeting)

 val legalStatus = bob.age match {
  case Person(status) => s"My legal status is $status"
 }
 println(legalStatus)

 /*
 Exercise.
  */
 val n: Int = 45
 val mathProperty = n match {
  case x if x < 10 => "single digit"
  case x if x %2 == 0 => "an even number"
  case _ => "no propertu"
 } // this is not elegant

 object even {
  def unapply(arg: Int): Boolean = arg % 2 == 0
 }
 object  singleDigit {
  def unapply(arg: Int): Boolean = (arg > -10 && arg < 10)

 }
  val mathPropertyOwn: String = n match {
   case even() => "an even number"
   case singleDigit() => "single digit"
   case _ =>"no property"
  }
  println(mathPropertyOwn)

 // infix patterns (like head :: Nil)
 case class Or[A, B](a: A, b: B)
 val either = Or(2, "two")
 val humandDescription = either match {
  case number Or string => s"$number is written as $string"
 }
 println(humandDescription)

 // decomposing sequences
 val vararg = numbers match {
  case List(1, _*) => "starting with 1"
 }

 // abstract class
}
