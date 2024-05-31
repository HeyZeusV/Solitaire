package com.heyzeusv.solitaire.board.piles

import com.heyzeusv.solitaire.util.tc
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class StockLogicTest : BehaviorSpec({
    lateinit var stock: Stock
    beforeContainer {
        stock = Stock()
    }
    Given("Initial pile") {
        val initialPile = listOf(tc.card10D, tc.card10S, tc.card1H, tc.card4C)
        When("Stock is initialized") {
            stock = Stock(initialPile)
            Then("truePile and displayPile are equal to initialPile") {
                stock.truePile shouldBe initialPile
                stock.displayPile shouldBe initialPile
            }
        }
    }
    Given("List of cards") {
        val cards = listOf(tc.card1CFU, tc.card2DFU, tc.card3HFU, tc.card4SFU)
        When("Calling add") {
            stock.addAll(cards)
            Then("All cards should be added to truePile face down") {
                stock.truePile shouldBe cards.map { it.copy(faceUp = false) }
                stock.displayPile shouldBe emptyList()
            }
        }
        When("Adding all cards and calling remove") {
            stock.addAll(cards)
            val removedCard = stock.remove()
            Then("First card should be removed and returned") {
                stock.truePile shouldBe listOf(tc.card2D, tc.card3H, tc.card4S)
                stock.displayPile shouldBe listOf(tc.card2D, tc.card3H, tc.card4S)
                removedCard shouldBe tc.card1C
            }
        }
        When("Calling reset with cards") {
            stock.reset(cards)
            Then("All cards should be added as is") {
                stock.truePile shouldBe cards
                stock.displayPile shouldBe cards
            }
        }
        When("Adding all cards and calling undo") {
            stock.addAll(cards)
            stock.undo()
            Then("Piles should be empty") {
                stock.truePile shouldBe emptyList()
                stock.displayPile shouldBe emptyList()
            }
        }
        When("Adding all cards twice and calling undo once") {
            stock.addAll(cards)
            stock.addAll(cards)
            stock.undo()
            Then("truePile should contain 4 face down cards") {
                stock.truePile shouldBe listOf(tc.card1C, tc.card2D, tc.card3H, tc.card4S)
                stock.displayPile shouldBe emptyList()
            }
        }
    }
    Given("Stock with cards") {
        val cards = listOf(tc.card1CFU, tc.card2DFU, tc.card3HFU, tc.card4SFU)
        beforeContainer {
            stock.reset(cards)
        }
        When("Calling removeMany with amount of 3 (less than cards amount") {
            val cardsReturned = stock.removeMany(3)
            Then("Card list of size 3 should be returned and truePile changed") {
                stock.truePile shouldBe listOf(tc.card4SFU)
                cardsReturned shouldBe listOf(tc.card1CFU, tc.card2DFU, tc.card3HFU)
            }
        }
        When("Calling removeMany with amount of 10 (more than cards amount") {
            val cardsReturned = stock.removeMany(10)
            Then("Cards list of size 4 should be returned and truePile changed") {
                stock.truePile shouldBe emptyList()
                cardsReturned shouldBe cards
            }
        }
    }
    Given("Stock with no cards") {
        When("Calling reset") {
            stock.reset()
            Then("No cards should exist in Stock") {
                stock.truePile shouldBe emptyList()
                stock.displayPile shouldBe emptyList()
            }
        }
        When("Calling undo") {
            stock.undo()
            Then("No cards should exist in Stock") {
                stock.truePile shouldBe emptyList()
                stock.displayPile shouldBe emptyList()
            }
        }
    }
})