// group 30
// 101441610 Wentao Xie
// 101619329 Anselme DONATO 

package reactor

import reactor.api.{Event, EventHandler}

final class Dispatcher(private val queueLength: Int = 10) {
  require(queueLength > 0)

  // Create the BlockingQueue to store the incoming events
  private val eventQueue = new BlockingEventQueue[Any](queueLength)
  // Create a set to store the registered handlers
  private val handlers = scala.collection.mutable.Set.empty[EventHandler[_]]
  // Create a map to track the workerThreads of corresponding handler
  private val workerThreads = scala.collection.mutable.Map.empty[EventHandler[_], WorkerThread[_]]

  @throws[InterruptedException]
  def handleEvents(): Unit = {
    // repeatedly handle events if there are still registered handlers
    while (handlers.nonEmpty) {
      // get the first event from the blocking queue
      val event = eventQueue.dequeue
      // if the corresponding handler of the event has been removed, don't handle the event
      if (handlers.contains(event.getHandler))
        event.dispatch()
    }
    // when all registered handlers have been removed, handleEvents will have done it job and return
  }

  def addHandler[T](handler: EventHandler[T]): Unit = {
    // throw an exception if trying to register an already registered handler
    if (handlers.contains(handler)) {
      throw new IllegalArgumentException("Handler already registered")
    }
    // add the handler to the set, create and start its workThread
    handlers.add(handler)
    val workerThread = new WorkerThread(handler, eventQueue)
    workerThreads.put(handler, workerThread)
    workerThread.start()
  }

  def removeHandler[T](handler: EventHandler[T]): Unit = {
    // remove handler from the set, call cancelThread method of the thread to stop it
    handlers.remove(handler)
    val workerThread = workerThreads.remove(handler)
    workerThread.foreach(_.cancelThread())
  }

}


// every handler has its own WorkerThread, it interacts with the dispatcher's eventQueue
final class WorkerThread[T](handler: EventHandler[T], eventQueue: BlockingEventQueue[Any]) extends Thread {

  override def run(): Unit = {
    // work until get interrupted
    while (!Thread.interrupted()) {
      // read the data from the handle of the handler, push the event to the blockingQueue of the dispatcher
      val handle = handler.getHandle
      val data = handle.read()
      val event = new Event(data, handler)
      eventQueue.enqueue(event)
      // if the data from the handle is null, the thread should be stoped. Stop it by interruption
      if (data == null)
        Thread.currentThread().interrupt()
    }
  }

  def cancelThread(): Unit = {
  //cancel the thread by interruption
    interrupt()
  }
}
