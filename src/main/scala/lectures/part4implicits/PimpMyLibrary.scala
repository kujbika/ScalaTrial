package lectures.part4implicits

object PimpMyLibrary extends App {
  // 2.isPrime

  // implicit classes!
  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0
    def sqrt: Double    = Math.sqrt(value)
  }

  implicit class RicherInt(richInt: RichInt) {
    def isOdd: Boolean = richInt.value % 2 != 0
  }
  new RichInt(42).sqrt

  // type enrichment = pimping
  42.isEven // new RichInt(42).isEven
  1 to 10   // same
  // 42.isOdd not working

  /*
   * Enrich the string class
   *  -asInt
   *  -encrypt
   *    John -> Lnjp
   *  -Keep enriching the int class
   *    times(function): 3.time(() => )
   *    3 * List(1,2) => List(1,2,1,2,1,2)
   */

  implicit class StringEnricher(val str: String) extends AnyVal {
    def asInt: Int      = Integer.valueOf(str)
    def encrypt: String = str.reverse
  }
  println("2".asInt)
  println("John".encrypt)

  // implicit conversions "3" /4 AVOID IMPLICIT DEFS
  implicit def stringToInt(string: String): Int = Integer.valueOf(string)

  println("6" / 2)

  // danger zone: Extremely hard to debug
  implicit def intToBoolean(i: Int): Boolean = i != 0

  val aConditionedValue = if (3) "ok" else "wrong"
  println(aConditionedValue)

}
