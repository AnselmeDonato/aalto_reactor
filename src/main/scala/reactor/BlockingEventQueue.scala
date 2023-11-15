// group 1
// 123456 Firstname Lastname
// 654321 Firstname Lastname

package reactor

import reactor.api.Event
import scala.collection.mutable.ArrayBuffer

final class BlockingEventQueue[T] (private val capacity: Int) {
	val queue = ArrayBuffer[Any]() //97 hmmmpf bof le any 

  @throws[InterruptedException]
  def enqueue[U <: T](e: Event[U]): Unit = {
		if(Thread.interrupted()){
			throw new InterruptedException(); 
		}

		println("enqueue") //97
		queue.prepend(e)
	}

  @throws[InterruptedException]
  def dequeue: Event[T] = {
		if(Thread.interrupted()){
			throw new InterruptedException()
		}
		if(queue.length == 0){
			throw new InterruptedException("Queue is empty")
		}

		println("dequeue") //97
		queue.remove(0).asInstanceOf[Event[T]] //97 What if error? 
	}

  def getAll: Seq[Event[T]] = queue.asInstanceOf[Seq[Event[T]]]

  def getSize: Int = queue.length

  def getCapacity: Int = capacity

}
