package lectures.part5ts

object RockingInheritance extends App {
  // convenience
  trait Writer[T] {
    def write(value: T): Unit
  }

  trait Closeable {
    def close(status: Int): Unit
  }

  trait GenericStream[T] {
    def foreach(f: T => Unit): Unit
  }

  def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // diamond problem
  trait Animal { def name: String }
  trait Lion   extends Animal { override def name: String = "Lion" }
  trait Tiger  extends Animal { override def name: String = "Tiger" }
  class Mutant extends Lion with Tiger

  val m = new Mutant
  println(m.name)
  // the super problem + type linearization
  // i didnt care here
}
