package lectures.part4implicits

import java.util.concurrent.Future

object MagnetPattern extends App {
  // method overloading

  class P2PRequest
  class P2PResponse
  class Serializer[T]
  trait Actor {
    def recieve(statusCode: Int): Int
    def recieve(request: P2PRequest): Int
    def recieve(response: P2PResponse): Int
    def recieve[T: Serializer](message: T): Int
    def reviece[T: Serializer](message: T, statusCode: Int): Int
    def recieve(future: Future[P2PRequest]): Int
    // def recieve(future: Future[P2PResponse]): Int
    // lots of overloading
  }
  /*
   * 1 - type erasure
   * 2 - lifting doestn work for all overloads
   * val reieveFC = recieve _ // ?!
   * 2 - code duplication!
   * 4 - type inference and default args
   * actor.recieve(?!)
   */

  trait MessageMagnet[Result] {
    def apply(): Result
  }
  def receive[R](magnet: MessageMagnet[R]): R = magnet()

  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
    def apply(): Int = {
      // logic for handling a request
      println("Handling p2p request")
      42
    }
  }

  implicit class FromP2PResponse(request: P2PResponse) extends MessageMagnet[Int] {
    def apply(): Int = {
      // logic for handling a request
      println("Handling p2p response")
      24
    }
  }
  receive(new P2PRequest)
  receive(new P2PResponse)

  // no more type erasure problems
  // lifting works
  // trait MathLib {
  //   def add1(x: Int)    = x + 1
  //   def add1(x: String) = x.toInt + 1
  // }

  // magnetize
  trait AddMagnet {
    def apply(): Int
  }
  def add1(magnet: AddMagnet): Int = magnet()

  implicit class AddInt(x: Int) extends AddMagnet {
    def apply(): Int = x + 1
  }
  implicit class AddString(x: String) extends AddMagnet {
    def apply(): Int = x.toInt + 1
  }

  val addFV = add1 _
  println(addFV(1))
  println(addFV("2"))

  /*
   * Drawbacks
   * 1 - verbosity and harder to read
   * 2 - you cant name or place default arguments
   * 3 - call by name doesnt work correctly
   * (exercise: prove it!)
   */
  class Handler {
    def handle(s: => String) = {
      println(s)
      println(s)
    }
    // other overloads
  }
  trait HandleMagnet {
    def apply(): Unit
  }
  def handle(magnet: HandleMagnet) = magnet.apply()

  implicit class StringHandle(s: => String) extends HandleMagnet {
    def apply(): Unit = {
      println(s)
      println(s)
    }
  }

  def sideEffectMethod(): String = {
    println("Hello, Scala")
    "hahah"
  }
  handle(sideEffectMethod())
  handle {
    println("hello scala")
    "hahha"
  }
}
