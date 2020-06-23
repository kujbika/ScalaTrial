package lectures.part2fp

object LazyEvaluation extends App {
  // lazy DELAYS the evaluation
  lazy val x: Int = throw new RuntimeException
  // println(x)

  lazy val y: Int = {
    println("hello")
    42
  }
  println(y)
  println(y)

  def sideEffectCondition: Boolean = {

    println("Boo")
    true
  }

  def simpleCondition: Boolean = false
  lazy val lazyCondition = sideEffectCondition
  println(if (simpleCondition && lazyCondition) "yes" else "no")

  // lazy is not evaluated, as it is not neccessary
  // in conjuction with call by nem
  //
  def byNameMethod(n: => Int): Int = {
    lazy val t = n
    t + t + t + 1
  }
  def retrieveMagicValue = {
    Thread.sleep(1000)
    println("waiting")
    42
  }
  println(byNameMethod(retrieveMagicValue))

  // filreting with lazy vals
  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  // filreting with lazy vals
  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 20)
  val lt30 = numbers.filter(lessThan30)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)
  println("hello")

  val numbers_1 = List(1, 25, 40, 5, 20)
  val lt30_1 = numbers_1.withFilter(lessThan30) // lazy filter
  val gt20_1 = lt30_1.withFilter(greaterThan20) //lazy filter
  // println(gt20_1) // no side effects aka printings
  gt20_1.foreach(println) // lazy in only evaluated in need!!
  println("hello")
// gt20_1 is a filter monadic!
// for comprehensions are evaluated lazyly!

  for {
    a <- List(1, 2, 3) if a % 2 == 0
  } yield a + 1
  List(1, 2, 3).withFilter(_ % 2 == 0).map(_ + 1)
}
