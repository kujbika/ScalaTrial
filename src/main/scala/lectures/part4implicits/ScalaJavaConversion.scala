package lectures.part4implicits

import java.{util => ju}

object ScalaJavaConversion extends App {
  import collection.JavaConverters._

  val javaSet: ju.Set[Int] = new ju.HashSet[Int]()
  (1 to 5).foreach(javaSet.add)
  println(javaSet)

  val scalaSet = javaSet.asScala

  // iterator, iterable, ju.List - scala.mutable.Buffer
  // ju.Set - scala.mutable.Set
  // ju.Map - scala.mutable.Map
  import collection.mutable._
  val numbersBuffer   = ArrayBuffer[Int](1, 2, 3)
  val juNumbersBuffer = numbersBuffer.asJava

  val numbers     = List(1, 2, 3)
  val juNumber    = numbers.asJava
  val backToScala = juNumber.asScala // it creates a mutable list from an immutable

  // exercise
  class ToScala[T](value: => T) {
    def asScala: T = value
  }
  implicit def asScalaOptional[T](o: ju.Optional[T]): ToScala[Option[T]] = new ToScala[Option[T]](
    if (o.isPresent) Some(o.get) else None
  )

  val juOptional: ju.Optional[Int] = ju.Optional.of(2)
  val scalaOption                  = juOptional.asScala
}
