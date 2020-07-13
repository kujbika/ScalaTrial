package part3concurrency

import java.util.concurrent.Executors

object Intro extends App {

  // JVM threads
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Running in parallel")
  })

  aThread
    .start() // create a JVM thread, gets executed at a different thread than Intro
  // you always call the start method, not the run of the Runnable - that doestn do anythin gin paralle

  aThread.join() // blocks until aThread finishes running

  val threadHello = new Thread(() => (1 to 4).foreach(_ => println("helloj")))
  val threadHello1 = new Thread(() => (1 to 4).foreach(_ => println("goodbye")))

  threadHello.start()
  threadHello1.start()
  // differnet runs produce different resutlts!
  // question? how to execute them in a predictable way

  // threads are expensive to start and kill
  // executors needed
  val pool = Executors.newFixedThreadPool(10)
  pool.execute(() => println("Something in the thread pool")) // gets executed in one of the ten threads in the pool

  // these actions are executed in the same time
  pool.execute(() => {
    Thread.sleep(1000)
    println("done after one second")
  })
  pool.execute(() => {
    Thread.sleep(1000)
    println("almost done")
    Thread.sleep(1000)
    println("done after 2 seconds")
  })

  pool.shutdown()
  // pool.execute(() => println("should not appear")) throws an exception in the calling thread
  // pool.shutdownNow() // if they are sleeping they throw exceptions
  println(pool.isShutdown())

  /*
   * MAIN PROBLEMS WITH PARALLEL RUNNING IN THE JVM
   */
  def runInParallel = {
    var x = 0

    val thread1 = new Thread(() => x = 1)
    val thread2 = new Thread(() => x = 1)
    thread1.start()
    thread2.start()
    println(x)
  }
  for (_ <- 1 to 100) runInParallel
  // the prinln got executed before the threads (sometimes thats not the case)
  // this is called race condition: two threads are setting the same values at the same time
  class BankAccount(var amount: Int) {
    override def toString(): String = "" + amount

  }
  def buy(account: BankAccount, thing: String, price: Int) = {
    account.amount -= price
    // println("Ive bought " + thing)
    // println("my account is now " + account)
  }

  for (_ <- 1 to 1000) {
    val account = new BankAccount(50000)
    val thread1 = new Thread(() => buy(account, "shoes", 3000))
    val thread2 = new Thread(() => buy(account, "iphone", 4000))
    thread1.start()
    thread2.start()
    Thread.sleep(10)
    if (account.amount != 43000) println("AHA " + account.amount)
    /*
     * thread1: 50000 -> 47000
     * thread2: 50000 -> 46000 + overwrites
     */

    // option 1: use synchronized()
  }
  def buySafe(account: BankAccount, thing: String, price: Int) = {
    account.synchronized(
      // no two threads can evaluate this at the same time
      account.amount -= price
    )
    println("Ive bought " + thing)
    println("my account is now " + account)
  }
  // option 2: use @volatile
  class BankAccount2(@volatile var amount: Int) // this is the same: amount can be evalueated parallelly
  // synchronized is more often used

  /**
    * Exercices
    * 1) Construct 50 "inception" threads
    *    Thread1 -> Thread2 -> Thread3 -> ...
    *    println("hello from thread #4")
    *    in REVERSE order
    */
  /*
   * 2
   */
  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x += 1))
  threads.foreach(_.start())

  /*
   * What is the biggest value possible for x?
   * lowest?
   */

  /*
   * 3
   */
  var message = ""
  val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "Scala is awesome"
  })
  message = "scala sucks"
  awesomeThread
    .start() // to get from here: this is a very bad programming practice
  Thread.sleep(2000)
  println(message)
  // we can see Scala sucks in the console
  // solution?
  // after Thread.sleep(2000) put awesomeThread.join()
}

object Exercices extends App {

  def inceptionThreads(maxThreads: Int, i: Int = 1): Thread =
    new Thread(() => {
      if (i < maxThreads) {
        val newThread = inceptionThreads(maxThreads, i + 1)
        newThread.start()
        newThread.join()
      }
      println(s"hello from thread $i")

      inceptionThreads(50).start()
    })

  // how do we fix the problems
  // synhronizing not works
}
