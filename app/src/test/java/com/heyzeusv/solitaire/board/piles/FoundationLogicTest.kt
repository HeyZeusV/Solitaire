package com.heyzeusv.solitaire.board.piles

import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.util.tc
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class FoundationLogicTest : BehaviorSpec({
    lateinit var foundation: Foundation
    beforeContainer {
        foundation = Foundation(Suits.CLUBS, GamePiles.FoundationClubsOne)
    }
    Given("Initial pile") {
        val initialPile = listOf(tc.card10D, tc.card10S, tc.card1H, tc.card4C)
        When("Foundation is initialized") {
            foundation = Foundation(Suits.CLUBS, GamePiles.FoundationClubsOne, initialPile)
            Then("truePile and displayPile are equal to initialPile") {
                foundation.truePile shouldBe initialPile
                foundation.displayPile shouldBe initialPile
            }
        }
    }
    Given("List of cards") {
        val cards = listOf(tc.card1C, tc.card2D, tc.card3H, tc.card4S)
        When("Calling add") {
            foundation.add(cards)
            Then("Only first card should be added to truePile face up") {
                foundation.truePile shouldBe listOf(tc.card1CFU)
                foundation.displayPile shouldBe emptyList()
            }
        }
        When("Calling addAll") {
            foundation.addAll(cards)
            Then("All cards should be added to truePile as is") {
                foundation.truePile shouldBe cards
                foundation.displayPile shouldBe emptyList()
            }
            Then("Call updateDisplayPile; displayPile should be the same as truePile") {
                foundation.updateDisplayPile()
                foundation.displayPile.toList() shouldBe foundation.truePile.toList()
            }
        }
        When("Adding all cards and calling remove") {
            foundation.addAll(cards)
            foundation.remove()
            Then("Last card should be removed") {
                foundation.truePile shouldBe listOf(tc.card1C, tc.card2D, tc.card3H)
                foundation.displayPile shouldBe emptyList()
            }
        }
        When("Calling reset with cards") {
            foundation.reset(cards)
            Then("All cards should be added face up") {
                foundation.truePile shouldBe cards.map { it.copy(faceUp = true) }
                foundation.displayPile shouldBe cards.map { it.copy(faceUp = true) }
            }
        }
        When("Adding all cards and calling undo") {
            foundation.addAll(cards)
            foundation.undo()
            Then("Piles should be empty") {
                foundation.truePile shouldBe emptyList()
                foundation.displayPile shouldBe emptyList()
            }
        }
    }
    Given("Foundation with no cards") {
        When("Calling reset") {
            foundation.reset()
            Then("No cards should exist in Foundation") {
                foundation.truePile shouldBe emptyList()
                foundation.displayPile shouldBe emptyList()
            }
        }
        When("Calling undo") {
            foundation.undo()
            Then("No cards should exist in Foundation") {
                foundation.truePile shouldBe emptyList()
                foundation.displayPile shouldBe emptyList()
            }
        }
    }
})