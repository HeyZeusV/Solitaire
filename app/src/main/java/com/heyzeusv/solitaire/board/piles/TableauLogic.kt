package com.heyzeusv.solitaire.board.piles

import com.heyzeusv.solitaire.board.animation.FlipCardInfo
import com.heyzeusv.solitaire.board.animation.TableauCardFlipInfo
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.util.faceDownExists
import com.heyzeusv.solitaire.util.isMultiSuit
import com.heyzeusv.solitaire.util.notInOrder
import com.heyzeusv.solitaire.util.notInOrderOrAltColor

/**
 *  In Solitaire, the Tableau refers to the piles where users can move [Cards][Card] between in
 *  order to sort before moving to a [Foundation] pile.
 *
 *  @property gamePile Which pile this instantiation represents.
 *  @param initialPile Cards that [Pile] is initialized with.
 */
class Tableau(
    val gamePile: GamePiles,
    initialPile: List<Card> = emptyList(),
) : Pile(initialPile) {

    /**
     *  Adds a [Card], face up, to the end [truePile].
     *
     *  @param card The [Card] to be added.
     */
    override fun add(card: Card) {
        _truePile.add(card.copy(faceUp = true))
        animatedPiles.add(_truePile.toList())
        appendHistory()
    }

    /**
     *  Adds multiple [Cards][Card], to the end [truePile].
     *
     *  @param cards The [Cards][Card] to be added.
     */
    override fun addAll(cards: List<Card>) {
        _truePile.addAll(cards)
        animatedPiles.add(_truePile.toList())
        appendHistory()
    }

    /**
     *  Removes [Cards][Card] from [truePile], starting from [Card] that was pressed to the end of
     *  [truePile]. Lastly, flips the last [Card] of [truePile] if any.
     *
     *  @param tappedIndex The [Card] to start removal process from.
     *  @return Not used in this version.
     */
    override fun remove(tappedIndex: Int): Card {
        _truePile.run {
            subList(tappedIndex, size).clear()
            // flip last card up
            if (isNotEmpty() && !last().faceUp) this[size - 1] = last().copy(faceUp = true)
        }
        animatedPiles.add(_truePile.toList())
        appendHistory()
        return Card(0, Suits.SPADES, false)
    }

    /**
     *  Resets [animatedPiles] and [historyList]. Replaces [truePile] and [displayPile] with a new
     *  list of face up [Cards][Card].
     *
     *  @param cards The new list of [Cards][Card].
     */
    override fun reset(cards: List<Card>) {
        resetLists()
        _truePile.addAll(cards)
        _displayPile.addAll(cards)
        currentStep = _truePile.toList()
    }

    /**
     *  Returns [truePile] to its previous state by accessing [historyList].
     */
    override fun undo() {
        _truePile.clear()
        val history = retrieveHistory()
        _truePile.addAll(history)
        animatedPiles.add(_truePile.toList())
        currentStep = _truePile.toList()
    }

    /**
     *  Checks if last [Card] of [truePile] will need a flip animation.
     *
     *  @param cardIndex The [Card] to flip, if needed, will be one less than this value.
     *  @return [TableauCardFlipInfo] which contains information needed to animated [Card] flip.
     */
    fun getTableauCardFlipInfo(cardIndex: Int): TableauCardFlipInfo? {
        val lastTableauCard: Card
        try {
            lastTableauCard = _truePile[cardIndex - 1]
        } catch (e: IndexOutOfBoundsException) {
            return null
        }
        if (lastTableauCard.faceUp) return null

        return TableauCardFlipInfo(
            flipCard = _truePile[cardIndex - 1],
            flipCardInfo = FlipCardInfo.FaceUp.SinglePile,
            remainingPile = _truePile.toList().subList(0, cardIndex - 1)
        )
    }

    /**
     *  Used to determine if game could be auto completed by having all face up cards
     */
    fun faceDownExists(): Boolean = _truePile.faceDownExists()

    /**
     *  Used to determine if pile contains more than 1 [Suits] type.
     */
    fun isMultiSuit(): Boolean = _truePile.isMultiSuit()

    /**
     *  It is possible for pile to be same suit, but out of order. This checks if pile is not in
     *  order descending, this way autocomplete will not be stuck in an infinite loop.
     */
    fun notInOrder(): Boolean = _truePile.notInOrder()

    /**
     *  It is possible for pile to be different suits, but out of order or not alternating colors.
     *  This checks if [truePile] is not in order descending and not alternating color, this way
     *  autocomplete will not be stuck in an infinite loop.
     */
    fun notInOrderOrAltColor(): Boolean = _truePile.notInOrderOrAltColor()
}