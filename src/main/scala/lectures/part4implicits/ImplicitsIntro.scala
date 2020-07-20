package lectures.part4implicits

object ImplicitsIntro extends App {
  val pair    = "daniel" -> "555"
  val intPair = 1        -> 2 // how the hell does this compile?

  case class Person(name: String) {
    def greet = s"Hi, my name is $name"
  }

  implicit def fromStringtoPerson(str: String): Person = Person(str)

  println("Peter".greet) // how??

  // implicit parameters
  def increment(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount                  = 10

  increment(2)

}
