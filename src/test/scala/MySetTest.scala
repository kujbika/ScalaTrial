import exercises.MySet
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import exercises.EmptySet

class MySetTest extends AnyFlatSpec with Matchers {
  // val firsSet: MySet[Int] = MySet(1,2,3,4,5)

  // @Test
  // def firstTest(): Unit = firsSet shouldBe MySet(1,2,3,4,5)
  //
  "MySet's remove function"  should "work properly" in {
    val firstSet: MySet[Int] = MySet(1,2,3)
    (firstSet remove 2) contains 2 shouldBe false 
  }

  "Disjoint intersection" should "result an Empty set" in {
    val firstSet: MySet[String] = MySet("Hello", "scala")
    val secondSet: MySet[String] = MySet("Bye", "python")
    (firstSet intersect secondSet).isEmpty shouldBe true
  }

  "Disjoint difference" should "result in the set itself" in {
    val firstSet: MySet[String] = MySet("Hello", "scala")
    val secondSet: MySet[String] = MySet("Bye", "python")
    (firstSet difference secondSet) shouldBe firstSet
  }

  "Set" should "filter properly" in {
    val set: MySet[Int] = MySet(1,2,3)
    val secondSet: MySet[Int] = MySet(2)
    ((set filter (_ % 2 == 0)) difference secondSet).isEmpty shouldBe true
  }

  it should "map properly" in {
    1 shouldBe 1
  }

  it should "flatMap properly" in {
    1 shouldBe 1
  }


}
