import exercises.MySet
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import exercises.EmptySet

class Person(age: String) {
  println("age")
}
object Person {
  def apply(age: String) = new Person(age)
}
// trait
// object
// class
// case class
class MySetTest extends AnyFlatSpec with Matchers {
  "MySet's remove function" should "work properly" in {
    val firstSet: MySet[Int] = MySet(1, 2, 3)
    // (firstSet - 2) contains 2 shouldBe false
    (firstSet - 2) shouldBe MySet(1, 3)
  }

  "Disjoint intersection" should "result an Empty set" in {
    val firstSet: MySet[String] = MySet("Hello", "scala")
    val secondSet: MySet[String] = MySet("Bye", "python")
    (firstSet & secondSet) shouldBe new EmptySet[String]
  }

  "Disjoint difference" should "result in the set itself" in {
    val firstSet: MySet[String] = MySet("Hello", "scala")
    val secondSet: MySet[String] = MySet("Bye", "python")
    (firstSet -- secondSet) shouldBe firstSet
  }

  "Set" should "filter properly" in {
    val set: MySet[Int] = MySet(1, 2, 3)
    val secondSet: MySet[Int] = MySet(2)
    ((set filter (_ % 2 == 0)) -- secondSet) shouldBe new EmptySet[Int]
  }

  it should "handle negations correctly" in {
    val set = MySet(1, 2, 3)
    (!set contains 1) shouldBe false
    (!set contains 4) shouldBe true
  }

}
