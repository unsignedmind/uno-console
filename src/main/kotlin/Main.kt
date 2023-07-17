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
    private var currentCard: UnoCard? = null
    private var gameOver = false
    private var stack = mutableListOf<UnoCard>()

    init {
        val colors = arrayOf("Red", "Green", "Blue", "Yellow")
        val values = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9")

        // For each color create a card with every number
        for (color in colors) {
            for (value in values) {
                deck.add(UnoCard(color, value))
            }
        }
        Collections.shuffle(deck)

        // For each player add seven cards to the players hand
        for (i in 0 until numPlayers) {
            val hand = mutableListOf<UnoCard>()
            for (j in 0 until 7) {
                hand.add(deck.removeAt(0))
            }
            playerHands.add(hand)
        }

        // Add a starter card to the stack of played cards
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

    private fun playCard(cardIndex: Int) {
        val card = getCurrentPlayerHand()[cardIndex]
        if (card.color == currentCard?.color || card.value == currentCard?.value) {
            getCurrentPlayerHand().removeAt(cardIndex)
            stack.add(card)
            println("Player $currentPlayer plays: $card")
            currentCard = card
        } else {
            println("Invalid card! Cannot play $card")
        }
    }

    private fun hasValidCard(playerHand: List<UnoCard>): Boolean {
        for (card in playerHand) {
            if (card.color == currentCard?.color || card.value == currentCard?.value) {
                return true
            }
        }
        return false
    }

    /*
    * Function sets the player count to 0 when the end of list of players is reached
    * */
    private fun getNextPlayerIndex(): Int {
        if(currentPlayer < (numPlayers -1)) {
            return currentPlayer + 1
        } else {
            return 0
        }
    }

    /*
    * Takes the used cards from the stack of played cards and puts them shuffled back to the deck
    * */
    private fun drawCards(amountOfCardsToDraw: Int) {
        repeat(amountOfCardsToDraw) {
            if (deck.isEmpty()) {
                deck.addAll(stack.subList(0, stack.size - 1))
                Collections.shuffle(deck)
                stack.clear()
                stack.add(deck.removeAt(0))
            }

            // player get gets a card
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
                drawCard()
                println("Press Enter")
                readln()
            } else {
                var validUserInput = false
                while (!validUserInput) {
                    println("Enter the index of the card you want to play:")
                    val input = readLine()?.toIntOrNull()
                    if (input != null && input in 0 until getCurrentPlayerHand().size) {
                        playCard(input)
                        validUserInput = true
                    } else {
                        println("Invalid card index!")
                    }
                }

                // one could change this so that the remaining players can play on
                if (getCurrentPlayerHand().isEmpty()) {
                    gameOver = true
                    println("Player $currentPlayer wins!")
                }
            }

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
