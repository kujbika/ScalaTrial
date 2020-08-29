package lectures.part5ts

object StructuralTypes extends App {

  // structural types
  type JavaCloseable = java.io.Closeable

  class HipsterCloseable {
    def close(): Unit         = println("yeah yeah")
    def closeSilently(): Unit = println("lol")
  }
  // def closeQuietly(closeable: JavaCloseable OR HipsterCloseable)
  type UnifiedCloseable = {
    def close(): Unit
  } // STRUCTURAL TYPE
  def closeQuietly(unifiedCloseable: UnifiedCloseable): Unit = unifiedCloseable.close()

  closeQuietly(new JavaCloseable { override def close(): Unit = ??? })
  closeQuietly(new HipsterCloseable)

  // TYPE REFINEMENTS, ENRICHMENT
  type AdvancedCloseable = JavaCloseable {
    def closeSilently(): Unit
  }

  class AdvancedJavaCloseable extends JavaCloseable {
    override def close(): Unit = println("Java closes")
    def closeSilently(): Unit  = println("Java closes silently")
  }
  def closeShh(advancedCloseable: AdvancedCloseable): Unit = advancedCloseable.closeSilently()

  closeShh(new AdvancedJavaCloseable)
  // closeShh(new HipsterCloseable) // not ok!

  // using structural types as standalone types
  def altClose(closeable: { def close(): Unit }): Unit = closeable.close()
  altClose(new HipsterCloseable)

  // type-checking => duck-typing
  type SoundMaker = {
    def makeSound(): Unit
  }
  class Dog {
    def makeSound(): Unit = println("bark")
  }
  class Car {
    def makeSound(): Unit = println("vroom")
  }

  val dog: SoundMaker = new Dog
  val car: SoundMaker = new Car
  // static duck typing
  // CAVEAT: based on reflection: impact on performance

  /*
   * Exercies
   */
  trait CBL[+T] {
    def head: T
    def tail: CBL[T]
  }

  class Human {
    def head: Brain = new Brain
  }

  class Brain {
    override def toString(): String = "BRAIN"
  }
  def f[T](somethingWithAHead: { def head: T }): Unit = println(somethingWithAHead.head)
  // f is compatible with CBL and with a Human?

  object HeadEqualizer {
    type Headable[T] = { def head: T }
    def ===[T](a: Headable[T], b: Headable[T]): Boolean = a.head == b.head
  }

}
