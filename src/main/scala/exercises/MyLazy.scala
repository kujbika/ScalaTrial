package exercises

class MyLazy[+A](elem: => A) {
  lazy val inside = elem
  def flatMap[B >: A](f: A => MyLazy[B]): MyLazy[B] = f(elem)

  final override def equals(x: Any): Boolean = {
    val that = x.asInstanceOf[MyLazy[A]]
    if (that == null) false
    else this.inside == that.inside
  }
}
object MyLazy {
  def apply[A](value: => A): MyLazy[A] = {
    lazy val elem = value
    new MyLazy[A](elem)
  }
}
