import java.util.*

class UnoCard(val color: String, val value: String) {
    override fun toString(): String {
        return "$color $value"
    }
}

class UnoGame(private val numPlayers: Int) {
    private val deck = mutableListOf<UnoCard>()
    private val playerHands = mutableListOf<MutableList<UnoCard>>()
    private var currentPlayer = 0
    private var clockwisePlayerTurns = true
    private var currentCard: UnoCard? = null
    private var gameOver = false
    private var drawTwoCount = 0
    private var drawTwoPlayed = false
    private var stack = mutableListOf<UnoCard>()

    init {
        val colors = arrayOf("Red", "Green", "Blue", "Yellow")
        val values = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "Skip", "Reverse", "Draw Two")

        // Create deck
        for (color in colors) {
            for (value in values) {
                deck.add(UnoCard(color, value))
                if (value != "0") {
                    deck.add(UnoCard(color, value))
                }
            }
        }
        Collections.shuffle(deck)

        // Deal cards to players
        for (i in 0 until numPlayers) {
            val hand = mutableListOf<UnoCard>()
            for (j in 0 until 7) {
                hand.add(deck.removeAt(0))
            }
            playerHands.add(hand)
        }

        // Set initial card
        val initialCard = deck.removeAt(0)
        stack.add(initialCard)
        currentCard = initialCard
        println("Starting card: $initialCard")
    }

    private fun getCurrentPlayerHand(): MutableList<UnoCard> {
        return playerHands[currentPlayer]
    }

    private fun printCurrentPlayerHand() {
        println("Current hand:")
        getCurrentPlayerHand().forEachIndexed { index, card ->
            println("$index: $card")
        }
    }

    private fun printTopCard() {
        println("Top card: ${stack.last()}")
    }

    private fun checkForDrawTwoCount(): Boolean {
        if (drawTwoCount > 0 && !drawTwoPlayed) {
            drawCards(drawTwoCount)
            println("Player $currentPlayer draws $drawTwoCount cards due to accumulated Draw Two cards.")
            drawTwoCount = 0
            return true
        }

        return false
    }

    private fun playCard(cardIndex: Int) {
        val card = getCurrentPlayerHand()[cardIndex]
        if (card.color == currentCard?.color || card.value == currentCard?.value || card.value == "Wild") {
            getCurrentPlayerHand().removeAt(cardIndex)
            stack.add(card)
            println("Player $currentPlayer plays: $card")
            currentCard = card
            if (card.value == "Reverse") {
                clockwisePlayerTurns = !clockwisePlayerTurns
            }
            if (card.value == "Skip") {
                checkForDrawTwoCount()
                currentPlayer = getNextPlayerIndex()
            }
            if (card.value == "Draw Two") {
                drawTwoCount += 2
                drawTwoPlayed = true
            }

        } else {
            println("Invalid card! Cannot play $card")
        }
    }

    private fun hasValidCard(playerHand: List<UnoCard>): Boolean {
        for (card in playerHand) {
            if (card.color == currentCard?.color || card.value == currentCard?.value || card.value == "Wild") {
                return true
            }
        }
        return false
    }

    private fun getNextPlayerIndex(): Int {
        if(clockwisePlayerTurns) {
            if(currentPlayer < (numPlayers -1)) {
                return currentPlayer + 1
            } else {
                return 0
            }
        } else {
            if(currentPlayer > 0) {
                return currentPlayer - 1
            } else {
                return numPlayers - 1
            }
        }
    }

    private fun drawCards(numCards: Int) {
        repeat(numCards) {
            if (deck.isEmpty()) {
                deck.addAll(stack.subList(0, stack.size - 1))
                Collections.shuffle(deck)
                stack.clear()
                stack.add(deck.removeAt(0))
            }
            playerHands[currentPlayer].add(deck.removeAt(0))
        }
    }

    private fun drawCard() {
        drawCards(1)
        println("Player $currentPlayer draws: ${getCurrentPlayerHand().last()}")
    }

    fun startGame() {
        while (!gameOver) {
            println("Player $currentPlayer's turn")
            printTopCard()
            printCurrentPlayerHand()

            if (!hasValidCard(getCurrentPlayerHand())) {
                println("No valid card to play. Drawing a card...")
                val drawnTwoCount = checkForDrawTwoCount()
                if(drawnTwoCount) {
                    drawCard()
                }
                println("Press Enter")
                readln()
            } else {
                var validInput = false
                while (!validInput) {
                    println("Enter the index of the card you want to play:")
                    val input = readLine()?.toIntOrNull()
                    if (input != null && input in 0 until getCurrentPlayerHand().size) {
                        playCard(input)
                        validInput = true
                    } else {
                        println("Invalid card index!")
                    }
                }
                checkForDrawTwoCount() // todo player could play a skip card and avoid to draw the extra cards
                if (getCurrentPlayerHand().isEmpty()) {
                    gameOver = true
                    println("Player $currentPlayer wins!")
                }
            }

            drawTwoPlayed = false
            currentPlayer = getNextPlayerIndex()
            println()
        }
    }
}

fun main() {
    val numPlayers = 3
    val game = UnoGame(numPlayers)
    game.startGame()
}
