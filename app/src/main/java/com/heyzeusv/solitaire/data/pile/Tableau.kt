package com.heyzeusv.solitaire.data.pile

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.util.Suits

/**
 *  Sealed class containing all possible options of [Tableau] piles. Each game has its own set of
 *  rules, primarily referring to the amount of [Card]s that start face up on reset and the
 *  condition to add a new pile to the end of [_pile].
 */
sealed class Tableau(initialPile: List<Card>) : Pile(initialPile) {
    /**
     *  KlondikeTableauTest contains test for most functions while each individual *TableauTest test
     *  reset() and add() only.
     */
    class KlondikeTableau(initialPile: List<Card> = emptyList()): Tableau(initialPile) {
        override val resetFaceUpAmount: Int = 1

        override fun addCondition(cFirst: Card, pLast: Card): Boolean {
            return cFirst.suit.color != pLast.suit.color && cFirst.value == pLast.value - 1
        }
    }
    class YukonTableau(initialPile: List<Card> = emptyList()): Tableau(initialPile) {
        override val resetFaceUpAmount: Int = 5

        override fun addCondition(cFirst: Card, pLast: Card): Boolean {
            return cFirst.suit.color != pLast.suit.color && cFirst.value == pLast.value - 1
        }
    }
    /**
     *  AustralianPatienceTableauTest also tests for isMultiSuit() and notInOrder().
     *
     *  Also used by Canberra
     */
    class AustralianPatienceTableau(initialPile: List<Card> = emptyList()): Tableau(initialPile) {
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
     *  Each game has their own version to adding new [Card]s to [_pile]. This is used in [add] to
     *  determine if cads should be added.
     */
    abstract fun addCondition(cFirst: Card, pLast: Card): Boolean

    /**
     *  Attempts to add given [cards] to [_pile] depending on [addCondition].
     */
    override fun add(cards: List<Card>): Boolean {
        if (cards.isEmpty()) return false

        val cFirst = cards.first()
        // can't add a card to its own pile
        _pile.run {
            if (contains(cFirst)) return false
            if (isNotEmpty()) {
                val pLast = last()
                // add cards if value of last card of pile is 1 more than first card of new cards
                // and if they are different colors
                if (addCondition(cFirst, pLast)) {
                    addAll(cards)
                    return true
                }
            // add cards if pile is empty and first card of given cards is the highest value (King)
            } else if (cFirst.value == 12) {
                addAll(cards)
                return true
            }
            return false
        }
    }

    /**
     *  Removes all cards from [_pile] starting from [tappedIndex] to the end of [_pile] and flips
     *  the last card if any.
     */
    override fun remove(tappedIndex: Int): Card {
        _pile.run {
            subList(tappedIndex, size).clear()
            // flip last card up
            if (isNotEmpty() && !last().faceUp) this[size - 1] = last().copy(faceUp = true)
        }
        // return value isn't used
        return Card(0, Suits.SPADES, false)
    }

    /**
     *  Reset [_pile] to initial game state.
     */
    override fun reset(cards: List<Card>) {
        _pile.run {
            clear()
            addAll(cards)
            for (i in cards.size.downTo(cards.size - resetFaceUpAmount + 1)) {
                try {
                    this[i - 1] = this[i - 1].copy(faceUp = true)
                } catch (e: IndexOutOfBoundsException) {
                    break
                }
            }
        }
    }

    override fun undo(cards: List<Card>) {
       _pile.run {
            clear()
            if (cards.isEmpty()) return
            addAll(cards)
        }
    }

    /**
     *  Used to determine if game could be auto completed by having all face up cards
     */
    fun faceDownExists(): Boolean = _pile.any { !it.faceUp }

    /**
     *  Used to determine if pile contains more than 1 [Suits] type.
     */
    fun isMultiSuit(): Boolean = _pile.map { it.suit }.distinct().size > 1

    /**
     *  It is possible for pile to be same suit, but out of order. This checks if pile is not in
     *  order, this way autocomplete will not be stuck in an infinite loop.
     */
    fun notInOrder(): Boolean {
        val it = _pile.iterator()
        if (!it.hasNext()) return false
        var current = it.next()
        while (true) {
            if (!it.hasNext()) return false
            val next = it.next()
            if (current.value < next.value) return true
            current = next
        }
    }
}