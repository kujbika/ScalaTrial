package lectures.part5ts

object PathDependentTypes extends App {

  class Outer {
    class Inner
    object InnerObject
    type InnerType
    def print(i: Inner)              = println(i)
    def printGeneral(i: Outer#Inner) = println(i)
  }

  def aMethod: Int = {
    class HelperClass
    type HelperType = String
    42
  }

  // per-instance
  val outer = new Outer
  val inner = new outer.Inner // outer.Inner is a TYPE

  val oo = new Outer
  // val inner2: oo.Inner = new outer.Inner
  outer.print(inner)

  // Outer#Inner
  outer.printGeneral(inner)
  oo.printGeneral(inner)
  /*
   * Exercies
   * DB keyed by Int or String, byt maybe others
   */

  trait ItemLike {
    type Key
  }
  trait Item[K] extends ItemLike {
    type Key = K
  }
  trait IntItem    extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???

  get[IntItem](42)
  get[StringItem]("scala")
  // get[IntItem]("scala") // no compile

}
