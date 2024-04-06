package com.heyzeusv.solitaire.data.pile

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.FlipCardInfo
import com.heyzeusv.solitaire.data.TableauCardFlipInfo
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.Suits

/**
 *  Sealed class containing all possible options of [Tableau] piles. Each game has its own set of
 *  rules, primarily referring to the amount of [Card]s that start face up on reset and the
 *  condition to add a new pile to the end of [truePile].
 */
class Tableau(val gamePile: GamePiles, initialPile: List<Card>) : Pile(initialPile) {
    /**
     *  Adds given [cards] to [truePile].
     */
    override fun add(cards: List<Card>) {
        _truePile.addAll(cards)
        animatedPiles.add(_truePile.toList())
        appendHistory(_truePile.toList())
    }

    /**
     *  Removes all cards from [truePile] starting from [tappedIndex] to the end of [truePile] and
     *  flips the last card if any.
     */
    override fun remove(tappedIndex: Int): Card {
        _truePile.run {
            subList(tappedIndex, size).clear()
            // flip last card up
            if (isNotEmpty() && !last().faceUp) this[size - 1] = last().copy(faceUp = true)
        }
        animatedPiles.add(_truePile.toList())
        appendHistory(_truePile.toList())
        // return value isn't used
        return Card(0, Suits.SPADES, false)
    }

    /**
     *  Reset [truePile] to initial game state using given [cards].
     */
    override fun reset(cards: List<Card>) {
        animatedPiles.clear()
        resetHistory()
        _truePile.run {
            clear()
            addAll(cards)
            _displayPile.clear()
            _displayPile.addAll(_truePile.toList())
            currentStep = this.toList()
        }
    }

    /**
     *  Used to return [truePile] to a previous state.
     */
    override fun undo() {
        _truePile.clear()
        val history = retrieveHistory()
        _truePile.addAll(history)
        animatedPiles.add(_truePile.toList())
        currentStep = _truePile.toList()
    }

    /**
     *  Checks if last card of [Tableau] will need a flip animation. If so, returns
     *  [TableauCardFlipInfo] using given [cardIndex].
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
    fun faceDownExists(): Boolean = _truePile.any { !it.faceUp }

    /**
     *  Used to determine if pile contains more than 1 [Suits] type.
     */
    fun isMultiSuit(): Boolean = _truePile.map { it.suit }.distinct().size > 1

    /**
     *  It is possible for pile to be same suit, but out of order. This checks if pile is not in
     *  order descending, this way autocomplete will not be stuck in an infinite loop.
     */
    fun notInOrder(): Boolean {
        val it = _truePile.iterator()
        if (!it.hasNext()) return false
        var current = it.next()
        while (true) {
            if (!it.hasNext()) return false
            val next = it.next()
            if (current.value - 1 != next.value) return true
            current = next
        }
    }

    /**
     *  It is possible for pile to be different suits, but out of order or not alternating colors.
     *  This checks if given [cards] is not in order descending and not alternating color, this way
     *  autocomplete will not be stuck in an infinite loop.
     */
    fun notInOrderOrAltColor(cards: List<Card> = truePile.toList()): Boolean {
        val it = cards.iterator()
        if (!it.hasNext()) return false
        var current = it.next()
        while (true) {
            if (!it.hasNext()) return false
            val next = it.next()
            if (current.value - 1 != next.value || current.suit.color == next.suit.color) return true
            current = next
        }
    }
}