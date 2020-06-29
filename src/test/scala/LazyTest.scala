import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import exercises.MyLazy
class LazyTest extends AnyFlatSpec with Matchers {

  "Lazy values" should "be evaluated when needed" in {
    val lazyBool: MyLazy[Boolean] = MyLazy {
      throw new RuntimeException(
        "this block would throw an error if gets evaluated"
      )
      true
    }

    val cond = false
    val a: Int = if (cond && lazyBool.inside) 10 else 20
    a shouldBe 20
  }

  "Layy monad" should "obey to right identity law" in {
    val a = 3

    MyLazy(3).flatMap(x => MyLazy(x)) shouldBe MyLazy(a)
  }

  it should "obey to left identity law" in {
    val a = "hello"
    val f: String => MyLazy[String] = x => MyLazy(x + ", oh hi")

    MyLazy(a).flatMap(f) shouldBe f(a)
  }

  it should "be associative" in {
    // like 1 + (2 + 3) = (1 + 2) + 3
    // this is to make flatMap chainable, as it is a binary operator
    val a = "hello"
    val f: String => MyLazy[String] = x => MyLazy(x + ", oh hi")
    val g: String => MyLazy[String] = x => MyLazy(x + ", whatsup?")

    MyLazy(a).flatMap(f).flatMap(g) shouldBe {
      MyLazy(a).flatMap(x => f(x).flatMap(g))
    }

  }
}
