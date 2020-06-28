import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import exercises.MyLazy
import org.scalatest.PrivateMethodTester
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
}
