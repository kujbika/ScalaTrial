package lectures.part2fp

object PartialFunctions extends App {
  val aFunction = (x: Int) => x + 1

  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  val aNicerFussyFunction = (x: Int) =>
    x match {
      case 1 => 42
      case 2 => 56
    }
  // domain {1,2} => Int. This is called a Partial function from Int to Int
  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
  } // partial function value
  println(aPartialFunction(2))
  // println(aPartialFunction(10))
  println(aPartialFunction.isDefinedAt(67))

  // lift
  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(2))
  println(lifted(20))

  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }
  println(pfChain(2))
  println(pfChain(45))

  // PF extend normal function
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // HOF'S accept PF as well
  val aMappedList = List(1, 2, 4).map {
    case 1 => 42
    case 4 => 78
    case 2 => 100
  }
  println(aMappedList)

  /*
  Note: a partial function only has one parameter
   */
  /**
    * Exercises
    * 1: construct a PF instance
    * 2: dumb chatbot as a PF
    */
  val a = new PartialFunction[Int, Int] {
    override def isDefinedAt(x: Int): Boolean = x == 1 || x == 2

    override def apply(v1: Int): Int = v1 match {
      case 1 => 42
      case 2 => 3
    }
  }
  println(a(1))
  val chatbot: PartialFunction[String, String] = {
    case "I am" => "Dont tell me lies bitch"
    case "Why"  => "Didnt said you can ask"
  }
  // scala.io.Source.stdin.getLines().foreach(line => println("chatbot says" + chatbot(line)))
  scala.io.Source.stdin.getLines().map(chatbot).foreach(println)
}
