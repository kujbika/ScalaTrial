package lectures.part5ts

object HigherKindedTypes extends App {

  trait AHigherKindedType[F[_]]
  trait MyList[T] {
    def flatMap[B](f: T => B): MyList[B]
  }
  trait MyOption[T] {
    def flatMap[B](f: T => B): MyOption[B]
  }
  trait MyFuture[T] {
    def flatMap[B](f: T => B): MyFuture[B]
  }

  // combine/multiplu List(1,2) X List("a", "b") => List(1a, 1b, 2a, 2b)

  // def multiply[A, B](listA: List[A], listb: List[B]): List[(A, B)] =
  //   for {
  //     a <- listA
  //     b <- listb
  //   } yield (a, b)

  // def multiply[A, B](listA: Option[A], listb: Option[B]): Option[(A, B)] =
  //   for {
  //     a <- listA
  //     b <- listb
  //   } yield (a, b)

  // we want a common API!!!
  // use Higher Kinded Type

  trait Monad[F[_], A] {
    def flatMap[B](f: A => F[B]): F[B]
    def map[B](f: A => B): F[B]
  }
  implicit class MonadList[A](list: List[A]) extends Monad[List, A] {
    override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)
    override def map[B](f: A => B): List[B]           = list.map(f)
  }
  implicit class MonadOption[A](list: Option[A]) extends Monad[Option, A] {
    override def flatMap[B](f: A => Option[B]): Option[B] = list.flatMap(f)
    override def map[B](f: A => B): Option[B]             = list.map(f)
  }
  val monadList = new MonadList(List(1, 2, 3))
  monadList.flatMap(x => List(x, x + 1)) // List[Int]
  monadList.map(_ * 2)

  def multiply[F[_], A, B](implicit ma: Monad[F, A], mb: Monad[F, B]): F[(A, B)] = {
    for {
      a <- ma
      b <- mb
    } yield (a, b)
  }
  println(multiply(List(2, 3), List("a", "b")))
  println(multiply(Some(2), Some("dof")))
  // println(multiply(new MonadList(List(1,2,3), ...)))

}
