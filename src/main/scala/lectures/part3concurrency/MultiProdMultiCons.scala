package lectures.part3concurrency

import scala.collection.mutable
import java.{util => ju}
import scala.util.Random
import scala.collection.immutable.Stream.Cons

object MultiProdMultiCons extends App {

  /*
   * producer1 -> [? ? ? ] -> consumer1
   * producer2                consumer2
   * producer3                consumer3
   */

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {

      val random = new Random()
      while (true) {
        buffer.synchronized {
          /*
           * producer produces value, two cons are waiting
           * pr notifies ONE consumer, notifies on buffer
           * assume the other consumer is notified
           *
           */
          while (buffer.isEmpty) {
            println(s"[consumer $id] buffer empty, waiting")
            buffer.wait()
          }
          val x = buffer.dequeue() // Oops.!
          println(s"[consumer $id] consumed " + x)

          buffer.notify() // hey producer, theres empty space
        }
        Thread.sleep(random.nextInt(500)) // simulates computation
      }
    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int)
      extends Thread {

    override def run(): Unit = {
      val random = new Random()
      var i = 0
      while (true) {
        buffer.synchronized {
          while (buffer.size == capacity) {
            println(s"[producer $id] buffer is full")
            buffer.wait()
          }

          println(s"[producer $id] producing " + i)
          buffer.enqueue(i)

          buffer.notify() // hey consumer, new food for you
          i += 1
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  def multiProdCons(nConsumer: Int, mProducers: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 20

    (1 to nConsumer).foreach(i => new Consumer(i, buffer).start())
    (1 to mProducers).foreach(i => new Producer(i, buffer, capacity).start())
  }

  multiProdCons(3, 3)
  /*
   * think of an example where nofityAll acts in a different way than notify
   * create a deadlock one or multiple threads block each other
   * create a livelock thread yields execution to each other
   */

  // notifyAll
  def testNotifyAll(): Unit = {
    val bell = new Object
    (1 to 10).foreach(i =>
      new Thread(() => {
        bell.synchronized {
          println(s"[thread $i] waiting...")
          bell.wait()
          println(s"[thread $i] hooray")
        }
      }).start()
    )

    new Thread(() => {
      Thread.sleep(2000)
      println(s"[announcer] rcknroll")
      bell.synchronized {
        bell.notifyAll()
      }
    }).start()
  }
  testNotifyAll()

  // deadlock
  case class Friend(name: String) {
    def bow(other: Friend) = {
      this.synchronized {
        println(s"$this: I am bowing to my friend $other")
        other.rise(this)
        println(s"$this: my friend $other has risen")
      }
    }
    def rise(other: Friend) = {
      this.synchronized {
        println(s"$this: I am rising to my friend $other")

      }
    }

    var side = "right"
    def switchSide(): Unit = {
      if (side == "right") side = "left" else side = "right"
    }

    def pass(other: Friend): Unit = {
      while (this.side == other.side) {
        println(s"$this: Oh, but please, $other, feel free to pass")
        switchSide()
        Thread.sleep(1000)
      }
    }
  }

  val sam = Friend("sam")
  val pierre = Friend("pierre")
  new Thread(() => pierre.bow(sam)).start()
  new Thread(() => sam.bow(pierre)).start()
  // livelock
  new Thread(() => sam.pass(pierre)).start()
  new Thread(() => pierre.pass(sam)).start()

}
