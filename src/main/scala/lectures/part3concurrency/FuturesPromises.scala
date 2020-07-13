package lectures.part3concurrency

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure
import scala.util.Random
import scala.concurrent.Await
import scala.concurrent.Promise

object FuturesPromises extends App {
  // futures are a functional wasy to calculate something in parallel
  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    // needed to import ExecutionContext
    // need to pass a function here
    calculateMeaningOfLife // calculates meaning of life on another thread }
  }
  println(
    aFuture.value
  ) //reutrns None Option[Try[Int]] The computaiton might have failed, and the future might have not finished
  println("Waiting on the future")
  aFuture.onComplete {
    case Success(meaningOfLife) => println(meaningOfLife)
    case Failure(exception)     => println(s"I have failed with $exception")
  } // SOME thread executes
  Thread.sleep(3000)

  // mini social network - asynchronously
  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile) =
      println(s"${this.name} poking ${anotherProfile.name}")
  }

  object SocialNetwork {
    // "database"
    val names = Map(
      "fb.id.1-zuck"  -> "Mark",
      "fb.id.2-bill"  -> "Bill",
      "fb.id.0-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    // API
    def fetchProfile(id: String): Future[Profile] = Future {
      Thread.sleep(random.nextInt(300)) // computation
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }

  }

  // client: Mark to poke Bill - not nice
  // val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
  // mark.onComplete {
  //   case Success(markProfile) => {
  //     val bill = SocialNetwork.fetchBestFriend(markProfile)
  //     bill.onComplete {
  //       case Success(billProfile) => markProfile poke billProfile
  //       case Failure(e)           => e.printStackTrace()
  //     }
  //   }
  //   case Failure(ex) => ex.printStackTrace()
  // }
  // Thread.sleep(1000)

  // functional composition of futures
  // val nameOnTheWall = mark.map(profile => profile.name) // future of profile to a future of string
  // val marksBestFriend =
  //   mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))

  // val zucksBestFriendRestricted = marksBestFriend.filter(_.name.startsWith("Z"))

  // for comprehension
  for {
    mark <- SocialNetwork.fetchProfile(
      "fb.id.1-zuck"
    ) // given this profile after computing the future...
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark poke bill
  Thread.sleep(1000)

  // in case the future fails FALLBACKS
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case e: Throwable => Profile("fb.id.0-dummy", "Forever alone")
  }

  val aFetchedProfileNoMatterWhat =
    SocialNetwork.fetchProfile("unknown id").recoverWith {
      case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
    }

  val fallbackResult = SocialNetwork
    .fetchProfile("unknown id")
    .fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))

  // online banking app
  case class User(name: String)
  case class Transaction(
    sender: String,
    reciever: String,
    amount: Double,
    status: String
  )

  object BankinkApp {
    val name = "Rock the JVM Banking"

    def fetchUser(name: String): Future[User] = Future {
      //simulate fetching from the DB
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      //simulate some processes
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
      // fetch the user from the DB
      // create transaction
      // WAIT for the transaction to finish
      val transactionStatusFuture = for {
        user        <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds) // implicit conversions: pimp my library
      // if the duration is passed, it throws a timeout

    }
  }
  println(BankinkApp.purchase("Marc", "Iphone12", "TrStore", 3000))

  // promises
  val promise = Promise[Int]() // controller over a future
  val future  = promise.future // the future is under the management of the promise

  // thread 1 - "consumer"
  future.onComplete {
    case Success(r) => println("[consumer] Ive received" + r)
  }

  // thread 2 - producer
  val producer = new Thread(() => {
    println("[producer] crunghing numbers...")
    Thread.sleep(500)
    // fulfillig the promise
    promise.success(42)
    //promise.failure(e)
    println("[producer] done")
  })
  producer.start()
  Thread.sleep(1000)

}
