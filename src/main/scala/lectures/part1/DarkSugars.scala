package lectures.part1

import scala.util.Try

object DarkSugars extends App{
  //syntax sugar #1: methods with single param
  def singleArgMethod(arg: Int): String = s"$arg little ducks"

  val description = singleArgMethod {
    // write some code
    42
  }
  val aTryInstance = Try { // java's try {...}
    throw new RuntimeException("hello")
  }

  List(1,2,3).map { x=>
    x + 1
  }

  // syntax sugar #2: single abstract method
  trait Action {
    def act(x: Int): Int
  }
  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }
  val anInstance2: Action = (x: Int) => x + 1 // magic
  // exaple: Runnables
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("hello Scala")
  })

  val aSweeterThread = new Thread(() => println("sweet, Scala!"))

  abstract class AnAbstractType {
    def implemented: Int = 23
    def f(a: Int): Unit
  }
  val anAbstractInstance:AnAbstractType = (a: Int) => println("sweet")

  // syntax sugar #3 :: and #:: methods are special. methods that ends with a : are right associative
  val prependedList = 2 :: List(1,2)
  // 2.::List(1,2)
  // List(1,2).::2
  // ?!

  // scala spec: last char decides associativity of method
  1 :: 2 :: 3 :: List(1,2,3)
  // List(1,2,3) :: 3) :: 2 ) :: 1
  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this // actual impl here
  }
  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  // syntax sugar #4 multi word method naming
  class TeenGirl(name: String) {
    def `and then said`(gossip: String): Unit = println(s"$name said $gossip")
  }
  val lilly = new TeenGirl("Lilly")
  lilly `and then said` "Scala is so sweet!"

  // syntaz sugar #5 infix types
  class Composite[A, B]
  // val composite: Composite[Int, String] = ???
  // val composite2: Int Composite String = ???

  class -->[A, B]
  val towards: Int --> String = ???

  //syntax sugar #6 update() is very special
  val anArray = Array(1,2,3)
  anArray(2) = 7 // rewritten to anArray.update(2, 7)
  // used in mutable collections
  // remember apply AND update

  //syntax sugar #7 setters for mutable containers
  class Mutable {
    private var internalMember: Int = 0 // private for OO encapsulation
    def member: Int = internalMember // getter
    def member_=(value: Int): Unit = {
      internalMember = value
    }
  }
  val aMutableContainer = new Mutable
  aMutableContainer.member = 42



}
