package part3concurrency
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference

import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.{ForkJoinTaskSupport, Task, TaskSupport}
import scala.collection.parallel.immutable.ParVector
object ParallelUtils extends App {
  // parallel collections
  val parList = List(1, 2, 3).par
  /*
  Map-reduce model
    - split the elements into chunks - Splitter
    - operation
    - recombine - Combiner
   */

  val aParVector = ParVector[Int](1, 2, 3)

  /*
    Seq
    Array
    Vector
    Map
    Set
   */
  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val list = (1 to 10000000).toList
  val serialTime = measure {
    list.map(_ + 1)
  }
  println("seiral " + serialTime)
  val parallelTime = measure {
    list.par.map(_ + 1)
  }
  println("parallel " + parallelTime)

  // map, flatMap, filter, foreach, reduce, fold

  println(List(1, 2, 3).reduce(_ - _))
  println(List(1, 2, 3).par.reduce(_ - _)) // fold and reduce might got wrong!!! differnet order of chunks

  // synchronization
  var sum = 0
  List(1, 2, 3).par.foreach(sum += _)
  println(sum) // multiple threads computing the same value - 6 is not guaranteed!!

  // configuring
  aParVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))
  /*
    alternatives
    - ThreadPoolTaskSupport - deprecated
    - ExecutionContextTaskSupport(EC)
   */
  aParVector.tasksupport = new TaskSupport {
    override def execute[R, Tp](fjtask: Task[R, Tp]): () => R = ???

    override def executeAndWaitResult[R, Tp](task: Task[R, Tp]): R = ???

    override def parallelismLevel: Int = ???

    override val environment: AnyRef = ??? // forkjoinpool, execution context, anything...
  }

  // 2 - atomic ops and references

  val atomic = new AtomicReference[Int](2)

  val currentValue = atomic.get() // thread-safe read
  atomic.set(4)                // thread-safe write
  atomic.getAndSet(5)          // thread-safe combo
  atomic.compareAndSet(38, 56) // if the value is 38, set to 56 (thread-safe)

  atomic.updateAndGet(_ + 1) // thread safe function run
  atomic.getAndUpdate(_ + 1)

  atomic.accumulateAndGet(12, _ + _) // thread safe accumulation
  atomic.getAndAccumulate(12, _ + _)

}
