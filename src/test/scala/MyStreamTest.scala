import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import exercises.MyStream
import exercises.MyStreamFactory
import exercises.EmptyStream
class MyStreamTest extends AnyFlatSpec with Matchers {
  val naturals = MyStream.from(1)(_ + 1)
  "The stream of naturals" should "start with 1" in {
    naturals.head shouldBe 1
  }
  /*
   [first, [...]
   [first, fibo(second, first + second)
   */
  def fibonacci(first: BigInt, second: BigInt): MyStream[BigInt] =
    new MyStreamFactory(first, fibonacci(second, first + second))

  println(fibonacci(1, 1).take(500).toList())
}
