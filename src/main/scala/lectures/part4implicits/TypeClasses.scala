package lectures.part4implicits

import java.{util => ju}

object TypeClasses extends App {

  trait HTMLWritable {
    def tohtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def tohtml: String = s"<div>$name ($age yo) <a href=$email/></div>"
  }

  User("John", 32, "john@gmail.com").tohtml

  /*
   * 1 only works for the types we write
   * 2 ONE implementation
   */

  // option 2 - use pattern matching
  object HTMLSerializerPM {
    def serializeToHtml(value: Any) = value match {
      case User(n, a, e)     =>
      case _: java.util.Date =>
      case _                 =>
    }
  }

  /*
   * 1: lost type safety
   * 2: need to modify the code every time
   * 3: still ONE implementation
   */

  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {

    def serialize(user: User): String = s"<div>${user.name} (${user.age} yo) <a href=${user.email}/></div>"
  }

  val john = User("john", 32, "asd@gmail.com")
  println(UserSerializer.serialize(john))

  // 1 we can define serializer for other types
  import java.util.Date
  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: ju.Date): String = s"<div>${date.toString}</div>"
  }

  // 2 we can define multiple serializer
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(value: User): String = s"<div>${value.name}</div>"
  }

  // TYPE CLASS
  trait MyTypeClassTemplate[T] {
    def action(value: T): String
  }

  object MyTypeClassTemplate {
    def apply[T](implicit instance: MyTypeClassTemplate[T]) = instance
  }

  // implicit type class instances
  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div style: colour=blue>$value</div>"
  }

  println(HTMLSerializer.serialize(42))
  println(HTMLSerializer.serialize(john))
  // access to the entire type class interface
  println(HTMLSerializer[User].serialize(john))

  // part 3
  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

  println(john.toHTML) //println(new HTMLEnrichment[User](john).toHTML(UserSerializer))
  println(john.toHTML(PartialUserSerializer))
  // COOL!
  /*
   * - extend to new types
   * - choose implementation
   * - super expressive
   */
  println(2.toHTML)

  /*
   * - type class itself HTMLSerializer[T]
   * - type class instances (some of them are implicits) UserSerializer, IntSerializer
   * - conversion with implicit classes HTMLEnrichment[T]
   */

  // context bounds
  def htmlBoilerplate[T](content: T)(implicit serializer: HTMLSerializer[T]): String =
    s"<html><body>${content.toHTML(serializer)}</body></html>"

  def htmlSugar[T: HTMLSerializer](content: T): String = {
    val serializer = implicitly[HTMLSerializer[T]]
    // use serializer
    s"<html><body>${content.toHTML(serializer)}</body></html>"
  }

  // implicitly
  case class Permissions(mask: String)
  implicit val defaultPermissions: Permissions = Permissions("0744")

  // in some other part of the code
  val standardPerms = implicitly[Permissions]

}
