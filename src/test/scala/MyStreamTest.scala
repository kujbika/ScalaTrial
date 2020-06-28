import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import exercises.MyStream
import exercises.MyStreamFactory
import exercises.EmptyStream
import scala.runtime.LazyInt

class MyStreamTest extends AnyFlatSpec with Matchers {

  "The stream of naturals" should "start with 1" in {
    val naturals = MyStream.from(1)(_ + 1)
    naturals.head shouldBe 1
  }

  it should "map correctly" in {
    val naturals = MyStream.from(1)(_ + 1)
    naturals.map(_ * 2).tail.head shouldBe 4
  }

  "The stream of the fibonacci numbers" should "work with big indexes" in {
    /*
     [first, [...]
     [first, fibo(second, first + second)
     */
    def fibonacci(first: BigInt, second: BigInt): MyStream[BigInt] =
      new MyStreamFactory(first, fibonacci(second, first + second))
    val a = fibonacci(1, 1).takeAsList(1000)
    a should have size 1000
  }

  "The stream of prime numbers" should "work with big indexes" in {
    // eratosthenes sieve
    def eratosthenes(numbers: MyStream[Int]): MyStream[Int] =
      if (numbers.isEmpty) numbers
      else
        new MyStreamFactory(
          numbers.head,
          eratosthenes(numbers.tail.filter(n => n % numbers.head != 0))
        )
    val a = eratosthenes(MyStream.from(2)(_ + 1)).takeAsList(1000)
    a should have size 1000
  }
}
