package exercises

import scala.runtime.LazyInt

class MyLazy[+A](elem: => A) {
  lazy val inside = elem
  def flatMap[B](f: A => MyLazy[B]): MyLazy[B] = f(inside)

  final override def equals(x: Any): Boolean = {
    val that = x.asInstanceOf[MyLazy[A]]
    if (that == null) false
    else this.inside == that.inside
  }
}
object MyLazy {
  def apply[A](value: => A): MyLazy[A] = new MyLazy[A](value)
}

object PlayLazy extends App {
  val lazyInt = MyLazy {
    println("you should see me once in the run")
    43
  }
  val f1 = lazyInt.flatMap(x => MyLazy(x + 2))
  val f2 = lazyInt.flatMap(x => MyLazy(x + 2))
  f1.inside
  f2.inside

}
