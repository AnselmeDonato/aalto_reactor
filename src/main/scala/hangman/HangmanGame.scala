// group 30
// 101441610 Wentao Xie
// 101619329 Anselme DONATO 

package hangman

import reactor.api.{EventHandler, Handle}
import reactor.Dispatcher
import hangman.util.{AcceptHandle, TCPTextHandle}
import java.net.Socket

class HangmanGame(val hiddenWord: String, val initialGuessCount: Int) {
  require(hiddenWord != null && hiddenWord.length > 0)
  require(initialGuessCount > 0)

	// TODO implement
  val port = Some(7800)
  var gameState = new GameState(hiddenWord, initialGuessCount, Set())
  var acceptHandler: AcceptHandler = _
  val playerHandlers = scala.collection.mutable.Set.empty[PlayerHandler]
  val dispatcher = new Dispatcher(10)

  def start() : Unit = {
    val acceptHandle = new AcceptHandle(port)
    acceptHandler = new AcceptHandler(acceptHandle, this)
    dispatcher.addHandler(acceptHandler)
    dispatcher.handleEvents()
  }

  def close() : Unit = {
    acceptHandler.closeHandle() //97 
    dispatcher.removeHandler(acceptHandler)
    playerHandlers.foreach(
      h => {
        h.closeHandle() //97 
        dispatcher.removeHandler(h)
      })
  }

  def broadcastState(guesser: String, guess: Char) : Unit = {
    playerHandlers.foreach(
      h => {
        h.writeHandle("%c %s %d %s".format(guess, gameState.getMaskedWord, gameState.guessCount, guesser)) //97 
      }
    )
  }
}

class AcceptHandler(val handle: AcceptHandle, val game: HangmanGame) extends EventHandler[Socket] {
  override def getHandle: AcceptHandle = { handle }
  override def handleEvent(socket: Socket): Unit = {
		val tcpTextHandle = new TCPTextHandle(socket)
		val playerHandler = new PlayerHandler(tcpTextHandle, game)
		game.playerHandlers.add(playerHandler)
		game.dispatcher.addHandler(playerHandler)
  }

	def closeHandle(): Unit = { handle.close() } //97 
}

class PlayerHandler(val handle: TCPTextHandle, val game: HangmanGame) extends EventHandler[String] {
  var name: String = null
  override def getHandle: TCPTextHandle = { handle }
  override def handleEvent(msg: String): Unit = {
		if (msg == "" || msg == "KILL" ){ //97
			game.close()
		}
		else if (name == null){
			name = msg
			handle.write("%s %d".format(game.gameState.getMaskedWord, game.gameState.guessCount))
		}
		else{
			try{
				val guessChar = msg.charAt(0)
				if (msg.length == 1 && guessChar.isLower){
					game.gameState = game.gameState.makeGuess(guessChar)
					game.broadcastState(name, guessChar)
					if (game.gameState.isGameOver)
						game.close()
				} else { //97
					handle.write("Please write only one (lowercase) letter at a time")
				}
			}
			catch{
				case e:Exception => {}
			}
		}
  }

	def writeHandle(text: String ): Unit = { handle.write(text)} //97 

	def closeHandle(): Unit = { handle.close() } //97 
  
}


object HangmanGame {

  def main(args: Array[String]): Unit = {
    val word: String = args(0) //first program argument
    val guessCount: Int = args(1).toInt //second program argument

    val game: HangmanGame = new HangmanGame(word, guessCount)
    //TODO start the game
    game.start()
  }

}
