package com.heyzeusv.solitaire.data.pile

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.FlipCardInfo
import com.heyzeusv.solitaire.data.TableauCardFlipInfo
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.Suits

/**
 *  Sealed class containing all possible options of [Tableau] piles. Each game has its own set of
 *  rules, primarily referring to the amount of [Card]s that start face up on reset and the
 *  condition to add a new pile to the end of [_truePile].
 */
sealed class Tableau(val gamePile: GamePiles, initialPile: List<Card>) : Pile(initialPile) {
    /**
     *  KlondikeTableauTest contains test for most functions while each individual *TableauTest test
     *  reset() and add() only.
     */
    class KlondikeTableau(gamePile: GamePiles = GamePiles.Stock, initialPile: List<Card> = emptyList()): Tableau(gamePile, initialPile) {
        override val resetFaceUpAmount: Int = 1

        override fun addCondition(cFirst: Card, pLast: Card): Boolean {
            return cFirst.suit.color != pLast.suit.color && cFirst.value == pLast.value - 1
        }
    }
    class ClassicWestcliffTableau(gamePile: GamePiles = GamePiles.Stock, initialPile: List<Card> = emptyList()): Tableau(gamePile, initialPile) {
        override val resetFaceUpAmount: Int = 1
        override val anyCardEmptyPile: Boolean = true

        override fun addCondition(cFirst: Card, pLast: Card): Boolean {
            return cFirst.suit.color != pLast.suit.color && cFirst.value == pLast.value - 1
        }
    }
    class EasthavenTableau(gamePile: GamePiles = GamePiles.Stock, initialPile: List<Card> = emptyList()): Tableau(gamePile, initialPile) {
        override val resetFaceUpAmount: Int = 1
        override val anyCardEmptyPile: Boolean = true

        override fun addCondition(cFirst: Card, pLast: Card): Boolean {
            return cFirst.suit.color != pLast.suit.color && cFirst.value == pLast.value - 1
        }

        override fun addListCondition(cards: List<Card>): Boolean = notInOrderOrAltColor(cards)

        fun addFromStock(cards: List<Card>) { _truePile.addAll(cards.map { it.copy(faceUp = true) }) }
    }
    class YukonTableau(gamePile: GamePiles = GamePiles.Stock, initialPile: List<Card> = emptyList()): Tableau(gamePile, initialPile) {
        override val resetFaceUpAmount: Int = 5

        override fun addCondition(cFirst: Card, pLast: Card): Boolean {
            return cFirst.suit.color != pLast.suit.color && cFirst.value == pLast.value - 1
        }
    }
    class AlaskaTableau(gamePile: GamePiles = GamePiles.Stock, initialPile: List<Card> = emptyList()): Tableau(gamePile, initialPile) {
        override val resetFaceUpAmount: Int = 5

        override fun addCondition(cFirst: Card, pLast: Card): Boolean {
            return cFirst.suit == pLast.suit &&
                    (cFirst.value == pLast.value - 1 || cFirst.value == pLast.value + 1)
        }
    }
    class RussianTableau(gamePile: GamePiles = GamePiles.Stock, initialPile: List<Card> = emptyList()): Tableau(gamePile, initialPile) {
        override val resetFaceUpAmount: Int = 5

        override fun addCondition(cFirst: Card, pLast: Card): Boolean {
            return cFirst.suit == pLast.suit && cFirst.value == pLast.value - 1
        }
    }
    /**
     *  AustralianPatienceTableauTest also tests for isMultiSuit() and notInOrder().
     *
     *  Also used by Canberra
     */
    class AustralianPatienceTableau(gamePile: GamePiles = GamePiles.Stock, initialPile: List<Card> = emptyList()): Tableau(gamePile, initialPile) {
        override val resetFaceUpAmount: Int = 4

        override fun addCondition(cFirst: Card, pLast: Card): Boolean {
            return cFirst.suit == pLast.suit && cFirst.value == pLast.value - 1
        }
    }

    /**
     *  Amount of cards to be face up on reset.
     */
    abstract val resetFaceUpAmount: Int

    /**
     *  Determines if an empty Tableau pile can be started by any card.
     */
    protected open val anyCardEmptyPile: Boolean = false

    /**
     *  Each game has their own version to adding new [Card]s to [_truePile]. This is used in [add] to
     *  determine if cads should be added.
     */
    abstract fun addCondition(cFirst: Card, pLast: Card): Boolean

    /**
     *  Some games have additional check for given [cards] before adding them.
     */
    protected open fun addListCondition(cards: List<Card>): Boolean = false

    /**
     *  Attempts to add given [cards] to [_truePile] depending on [addCondition].
     */
    override fun add(cards: List<Card>) {
        _truePile.addAll(cards)
        animatedPiles.add(_truePile.toList())
        appendHistory(_truePile.toList())
    }

    fun canAdd(cards:List<Card>): Boolean {
        if (cards.isEmpty()) return false
        if (addListCondition(cards)) return false

        val cFirst = cards.first()
        // can't add a card to its own pile
        _truePile.run {
            if (contains(cFirst)) return false
            if (isNotEmpty()) {
                val pLast = last()
                if (addCondition(cFirst, pLast)) return true
                // add cards if pile is empty and first card of given cards is the highest value (King)
                // or if any card is allowed to start a new pile
            } else if ((cFirst.value == 12 || anyCardEmptyPile)) {
                return true
            }
            return false
        }
    }

    /**
     *  Removes all cards from [_truePile] starting from [tappedIndex] to the end of [_truePile] and
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
     *  Reset [_truePile] to initial game state.
     */
    override fun reset(cards: List<Card>) {
        animatedPiles.clear()
        resetHistory()
        _truePile.run {
            clear()
            addAll(cards)
            for (i in cards.size.downTo(cards.size - resetFaceUpAmount + 1)) {
                try {
                    this[i - 1] = this[i - 1].copy(faceUp = true)
                } catch (e: IndexOutOfBoundsException) {
                    break
                }
            }
            _displayPile.clear()
            _displayPile.addAll(_truePile.toList())
            currentStep = this.toList()
        }
    }

    override fun undo() {
        _truePile.clear()
        val history = retrieveHistory()
        _truePile.addAll(history)
        animatedPiles.add(_truePile.toList())
        currentStep = _truePile.toList()
    }

    fun getTableauCardFlipInfo(cardIndex: Int): TableauCardFlipInfo? {
        val lastTableauCard: Card
        try {
            lastTableauCard = _truePile[cardIndex - 1]
        } catch (e: IndexOutOfBoundsException) {
            return null
        }
        if (lastTableauCard.faceUp) return null

        return TableauCardFlipInfo(
            card = _truePile[cardIndex - 1],
            cardIndex = cardIndex - 1,
            flipCardInfo = FlipCardInfo.FaceUp(),
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
    fun notInOrderOrAltColor(cards: List<Card>): Boolean {
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