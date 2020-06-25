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
  it should "map correctly" in {
    naturals.map(_ * 2).tail.head shouldBe 4
  }
  /*
   [first, [...]
   [first, fibo(second, first + second)
   */
  def fibonacci(first: BigInt, second: BigInt): MyStream[BigInt] =
    new MyStreamFactory(first, fibonacci(second, first + second))
  "The stream of the fibonacci numbers" should "work with big indexes" in {
    val a = fibonacci(1, 1).takeAsList(1000)
    a should have size 1000
  }

  // eratosthenes sieve
  def eratosthenes(numbers: MyStream[Int]): MyStream[Int] =
    if (numbers.isEmpty) numbers
    else
      new MyStreamFactory(
        numbers.head,
        eratosthenes(numbers.tail.filter(n => n % numbers.head != 0))
      )

  "The stream of prime numbers" should "work with big indexes" in {
    val a = eratosthenes(MyStream.from(2)(_ + 1)).takeAsList(1000)
    a should have size 1000
  }

}

