package lectures.part4implicits

import java.{util => ju}

object JSONSerialization extends App {

  case class User(name: String, age: Int, email: String)
  case class Post(content: String, createdAt: ju.Date)
  case class Feed(user: User, posts: List[Post])

  /*
   * 1 - intermediate data types: Int, String, List, Date
   * 2 - type classes for conversions
   * 3 - serialize
   */
  sealed trait JSONValue {
    def stringify: String
    override def toString: String = stringify
  }

  final case class JSONString(value: String) extends JSONValue {
    def stringify: String = "\"" + value + "\""
  }
  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    def stringify =
      values
        .map {
          case (key, value) => "\"" + key + "\":" + value.stringify
        }
        .mkString("{", ",", "}")
  }
  final case class JSONNumber(value: Int) extends JSONValue {
    def stringify: String = value.toString()
  }

  final case class JSONArray(value: List[JSONValue]) extends JSONValue {
    def stringify: String = value.map(_.stringify).mkString("[", ",", "]")
  }

  val data = JSONObject(
    Map(
      "user" -> JSONString("Daniel"),
      "posts" -> JSONArray(
        List(
          JSONString("Scala rocks"),
          JSONNumber(42)
        )
      )
    )
  )
  println(data.stringify)
  // type class

  /*
   * 1 - type class
   * 2 - type class instances (implicit)
   * 3 - pimp library to use type class instances
   */
  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }

  // conversion
  implicit class JSONOps[T](value: T) {
    def toJSON(implicit converter: JSONConverter[T]): JSONValue =
      converter.convert(value)
  }
  // existing data types
  implicit object StringConverter extends JSONConverter[String] {
    def convert(value: String): JSONValue = JSONString(value)
  }

  implicit object IntConverter extends JSONConverter[Int] {
    def convert(value: Int): JSONValue = JSONNumber(value)
  }

  // custom data types
  implicit object UserConverter extends JSONConverter[User] {
    def convert(value: User): JSONValue =
      JSONObject(
        Map(
          "name"  -> JSONString(value.name),
          "age"   -> JSONNumber(value.age),
          "email" -> JSONString(value.email)
        )
      )
  }
  implicit object PostConverter extends JSONConverter[Post] {
    def convert(value: Post): JSONValue =
      JSONObject(
        Map(
          "content"  -> JSONString(value.content),
          "created:" -> JSONString(value.createdAt.toString())
        )
      )
  }

  implicit object FeedConverter extends JSONConverter[Feed] {
    def convert(value: Feed): JSONValue =
      JSONObject(
        Map(
          "user"  -> feed.user.toJSON,
          "posts" -> JSONArray(value.posts.map(_.toJSON))
        )
      )
  }

  // call stringify on result
  val feed = Feed(User("bela", 23, "bela@gmail.com"), posts = List(Post("hello, bela vagyok", new ju.Date(2020, 4, 6)), Post("hello, mizu", new ju.Date(2020, 4, 7))))
  println(feed.toJSON)

}
