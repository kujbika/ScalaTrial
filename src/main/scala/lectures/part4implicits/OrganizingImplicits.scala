package lectures.part4implicits

object OrganizingImplicits extends App {
  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  println(List(1, 4, 5, 3, 2).sorted) // sorted needs an implicit

  // scala.Predef contains that implicit
  /*
   *
   * Implicits (used as implicit parameters):
   *  val/var
   *  object
   *  accessor methods = def with no parentheses
   */

  // Exercise
  case class Person(name: String, age: Int)

  object Person {
    // this is the practice implementation
    implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  val persons = List(Person("Steve", 30), Person("Amy", 20), Person("Jin", 34))

  println(persons.sorted)

  /*
    Implicit scope: hierarch from greatest to lowest
      - normal scope - LOCAL
      - imported scope
      - companion objects of all types involved in the method signature
        - List
        - Ordering
        - all the types involved (Person or any supertype)
   */

  // or you can package them
  // import AlphabeticOrdering._
  object AlphabeticOrdering {
    implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

}

object ImplicitOrdering extends App {
  /*
  Exercise: organize implicits
  - order totalPrice (this is the most used)
  - by unit count
  - by unit price
   */
  case class Purchase(nUnits: Int, unitPrice: Double)

  object Purchase {
    implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => (a.nUnits * a.unitPrice) < (b.nUnits * b.unitPrice))
  }

  object UnitCountOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => (a.nUnits < b.nUnits))
  }
  object UnitPriceOrdering {
    implicit val unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => (a.unitPrice < b.unitPrice))
  }

  // import UnitPriceOrdering._
  val p1 = Purchase(2, 2000)
  val p2 = Purchase(4, 500)
  val p3 = Purchase(1, 2500)
  println(List(p1, p2, p3).sorted)

}
