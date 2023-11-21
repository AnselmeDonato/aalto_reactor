// group 1
// 123456 Firstname Lastname
// 654321 Firstname Lastname

package reactor

import reactor.api.Event
import scala.collection.mutable.Queue

final class BlockingEventQueue[T] (private val capacity: Int) {
	/* Holds the elements of this BlockingQueue. */
  val queue = Queue[Event[T]]()

  @throws[InterruptedException]
  def enqueue[U <: T](e: Event[U]): Unit = synchronized {
		if(Thread.interrupted()){
			throw new InterruptedException(); 
		}
		while(queue.length >= capacity){
			wait()
		}

		if(e != null){
			queue += e.asInstanceOf[Event[T]]
			notifyAll()
		}
	}

  @throws[InterruptedException]
  def dequeue: Event[T] = synchronized {
		if(Thread.interrupted()){
			throw new InterruptedException()
		}
		while(queue.isEmpty){
			wait()
		}

		val last_item = queue.dequeue().asInstanceOf[Event[T]]
		notifyAll()
		last_item
	}

  def getAll: Seq[Event[T]] = synchronized {
		val seq: Seq[Event[T]] = queue.toSeq
		queue.clear()
		notifyAll()
		seq
	}

  def getSize: Int = synchronized {
		queue.size
	}

  def getCapacity: Int = capacity

}
