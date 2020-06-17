package lectures.part2fp

object CurriesPAF extends App{
  
  // curried functions
  val superAdder: Int => Int => Int = 
    x => y => x + y

  val add3 = superAdder(3)
  println(add3(5))
  println(superAdder(3)(5)) // curried function
  // METHOD!
  def curriedAdder(x: Int)(y: Int): Int = x + y // curried method

  val add4: Int => Int = curriedAdder(4)

  // lifting: transforming method into function: ETA-EXPANSION
  
  // functions != methods (JVM limitation)
  def inc(x: Int): Int = x + 1
  List(1,2,3).map(inc) //ETA-expansion inside map(x => inc(x))

  // Partial function applications
  val add5 = curriedAdder(5) _ // this does the ETA-expansion: Int => Int

  // EXERCISE
  //
  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedAddMethod(x: Int)(y: Int) = x +y

  //add7 implementation
  def add7_1(y: Int): Int = curriedAddMethod(7)(y) 
  val add7_2: Int => Int = curriedAddMethod(7)
  val add7_3 = curriedAddMethod(7) _
  val add7_8 = curriedAddMethod(7)(_)
  val add7_4 = (y: Int) => simpleAddFunction(7, y)
  val add7_6 = (y: Int) => simpleAddMethod(7, y)
  val add7_9 = simpleAddMethod(7, _: Int) // y => simpleAddMethod(7, y)
  val add7_7 = simpleAddFunction.curried(7)

  // underscores are powerful
  def concatenator(a: String, b: String, c:String) = a + b + c
  val insertName = concatenator("Hello, I'm ", _: String, ", how are you") // x: String => concatenator(...)
  println(insertName("Daniel"))
  // underscore forces the compiler to transform methods into function values
  val fillIntTheBlanks = concatenator("Hello", _: String, _: String)
  println(fillIntTheBlanks("Daniel", "Scala is awesome"))


  // EXERCISES
  /*
   * process a list of numbers and return their string repres. with different formats
   * use %4.2f, %8.6g and %14.12f with a curried fomatter function
   */
  println("%4.2f".format(Math.PI))
  def curriedFormatter(frm: String)(number: Double): String = frm.format(number)
  val numbers = List(1.2, Math.PI, 0.01, Math.E)

  val simpleFormat = curriedFormatter("%4.2f") _
  val preciseFormat = curriedFormatter("%14.12f") _
  println(numbers.map(simpleFormat))
  /*
   * difference between 
   * -functions vs methods
   *-parameters: by name cs 0-lambda
   */
  def byName(n: => Int) = n + 1
  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  byName(23) // ok
  byName(method) //ok
  byName(parenMethod()) // ok
  byName(parenMethod) // ok but beware

  // byFunction(method) // not ok!!!1 parameterless fucntion cant be ETA epxanded
  byFunction(parenMethod)
}
