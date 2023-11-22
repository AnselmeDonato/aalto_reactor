// group 30
// 101441610 Wentao Xie
// 101619329 Anselme DONATO 

package reactor

import reactor.api.Event

final class BlockingEventQueue[T] (private val capacity: Int) {
  // create a queue to store the events with type upper bound T
  private val queue = new scala.collection.mutable.Queue[Event[_ <: T]]

  require(capacity > 0, "Capacity must be positive")

  // use synchronized to make operation to the blocking queue muture exclusive
  @throws[InterruptedException]
  def enqueue[U <: T](e: Event[U]): Unit = synchronized {
    // Check if the event is null
    if (e == null) {
      throw new IllegalArgumentException("Event cannot be null")
    }
    // block the queue when it is full
    while (queue.size >= capacity){
      this.wait()
    }
    // when the queue is not full, put the element to the tail
    queue.enqueue(e)
    // notify other threads that might be waiting for the queue to be not empty to perform dequeue
    this.notifyAll()
  }

  @throws[InterruptedException]
  def dequeue: Event[T] = synchronized{
    // block the queue when it is empty
    while (queue.isEmpty){
      this.wait()
    }
    // when the queue is not empty, take a element from the head
    // cast the type to Event[T] as the queue can contain instance of subtype of T
    val event = queue.dequeue().asInstanceOf[Event[T]]
    // notify other threads that might be waiting for the queue to be not full to perform enqueue
    this.notifyAll()
    event
  }

  def getAll: Seq[Event[T]] = synchronized{
    // get all element in a sequence and cast the type to Event[T] as the queue can contain instance of subtype of T
    val allEvents = queue.toSeq.asInstanceOf[Seq[Event[T]]]
    // empty the queue
    queue.clear()
    // notify other threads that might be waiting for the queue to be not full to perform enqueue
    this.notifyAll()
    allEvents
  }

  def getSize: Int = synchronized {
    queue.size
  }

  def getCapacity: Int = capacity

}
