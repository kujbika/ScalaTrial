package lectures.part5ts

object Reflection extends App {
  // reflection + macros + quasiquotes => METAPROGRAMMING
  //
  case class Person(name: String) {
    def sayMyName(): Unit = println(s"Hi, my name is $name")
  }

  // 0 - import
  import scala.reflect.runtime.{universe => ru}

  // 1 - MIRROR
  val m = ru.runtimeMirror(getClass.getClassLoader)

  // 2 - create a class object = "description"
  val clazz = m.staticClass("lectures.part5ts.Reflection.Person") // creating a class object by name

  // 3 - create a reflected mirror = "can do things"
  val cm = m.reflectClass(clazz)

  // 4 - get the constructor
  val constructor = clazz.primaryConstructor.asMethod

  // 5 - reflect the constructor
  val constructorMirror = cm.reflectConstructor(constructor)

  // 6 - invoke
  val instance = constructorMirror.apply("John")
  println(instance)

  // i have an instance
  val p = Person("Mary") // from the wire az a serialized object

  // method name computed from somewhere else
  val methodName = "sayMyName"

  val reflected    = m.reflect(p)
  val methodSymbol = ru.typeOf[Person].decl(ru.TermName(methodName)).asMethod

  val method = reflected.reflectMethod(methodSymbol)
  method.apply()

  // type erasure
  // paint point 1L differentiate types at runtime
  val numbers = List(1, 2, 3)
  numbers match {
    case listOfStrings: List[String] => println("List of strings")
    case listOfNumbers: List[Number] => println("listOfStrings")
  }

  // paint point 2: limitations on overloads
  def processList(list: List[Int]): Int = 42
  // def processList(list: List[String]): Int = 45 // List instead of List[String]
  //
  // TypeTags
  // 0 - import

  import ru._
  // creating a type tag 'manuall'
  val ttag = typeTag[Person]
  println(ttag.tpe) // fully qualifed name of the type

  class MyMap[K, V]
  // pass type tags through implicit conversion
  def getTypeArguments[T](value: T)(implicit typeTag: TypeTag[T]) = typeTag.tpe match {
    case TypeRef(_, _, typeArguments) => typeArguments
    case _                            => List()
  }

  val myMap         = new MyMap[Int, String]
  val typeArguments = getTypeArguments(myMap)
  println(typeArguments)

  def isSubtype[A, B](implicit ttagA: TypeTag[A], ttagB: TypeTag[B]): Boolean = {
    ttagA.tpe <:< ttagB.tpe
  }

  class Animal

  class Dog extends Animal
  println(isSubtype[Dog, Animal])
  val anotherMethodSymbol = typeTag[Person].tpe.decl(ru.TermName(methodName)).asMethod

  val anotherMethod = reflected.reflectMethod(anotherMethodSymbol)
  anotherMethod.apply()
}
