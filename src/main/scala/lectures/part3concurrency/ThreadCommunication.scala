package lectures.part3concurrency

import scala.collection.mutable
import java.{util => ju}
import scala.util.Random

object ThreadCommunication extends App {

  /*
   * the producer-consumer problem
   * producer -> [ x ] -> consumer
   * producer sets, consumers gets
   * producer needs to be the first, even in parallel running
   */
  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0
    def get = {
      val result = value
      value = 0
      result
    }
    def set(newValue: Int) = value = newValue
  }

  def naiveProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting")
      while (container.isEmpty) {
        println("[consumer] actively waiting")
      }
      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] computing...")
      Thread.sleep(500)
      val value = 42
      println("[producer] I have produced" + value)
      container.set(value)
    })
    consumer.start()
    producer.start()

  }
  // naiveProdCons()
  // this is dumb, the while loop is expensive

  // WAIT AND NOTIFY!
  def smartProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("consumer waiting")
      container.synchronized {
        container.wait()

      }
      // container must have some value
      println("consumer i have consumed" + container.get)
    })

    val producer = new Thread(() => {
      println("hard at work")
      Thread.sleep(2000)
      val value = 42

      container.synchronized {
        println("producer im producing " + value)
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()
  }
  // smartProdCons()
  // this is the smart way of paralllel programming

  /*
   * produer -> [ ? ? ? ] -> consumer
   * both producer and consumer can block each other
   *
   */
  def prodConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()
      while (true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("[consumer] buffer empty, waiting")
            buffer.wait()
          }
          val x = buffer.dequeue()
          println("[consumer] consumed " + x)

          buffer
            .notify() // hey producer, theres empty space // hey producer, theres empty space
        }

        Thread.sleep(random.nextInt(500)) // simulates computation
      }
    })
    val producer = new Thread(() => {
      val random = new Random()
      var i = 0
      while (true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("[producer] buffer is full")
            buffer.wait()
          }

          println("[producer] producing " + i)
          buffer.enqueue(i)

          buffer.notify() // hey consumer, new food for you
          i += 1
        }
        Thread.sleep(random.nextInt(500))
      }
    })
    consumer.start()
    producer.start()
  }
  prodConsLargeBuffer()
  // multi thread application working on the same thing
}

