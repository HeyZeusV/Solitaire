package com.heyzeusv.solitaire.board.piles

import com.heyzeusv.solitaire.util.tc
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class WasteLogicTest : BehaviorSpec({
    lateinit var waste: Waste
    val cards = listOf(tc.card1C, tc.card2D, tc.card3H, tc.card4S)
    beforeContainer {
        waste = Waste()
    }
    Given("Initial pile") {
        val initialPile = listOf(tc.card10D, tc.card10S, tc.card1H, tc.card4C)
        When("Waste is initialized") {
            waste = Waste(initialPile)
            Then("truePile and displayPile are equal to initialPile") {
                waste.truePile shouldBe initialPile
                waste.displayPile shouldBe initialPile
            }
        }
    }
    Given("Waste with cards") {
        beforeContainer {
            waste.addAll(cards)
        }
        When("Calling remove") {
            val returnedCard = waste.remove()
            Then("Last card of truePile should be removed and returned") {
                returnedCard shouldBe tc.card4SFU
                waste.truePile shouldBe listOf(tc.card1CFU, tc.card2DFU, tc.card3HFU)
                waste.displayPile shouldBe emptyList()
            }
        }
        When("Calling removeAll") {
            waste.removeAll()
            Then("truePile should be empty") {
                waste.truePile shouldBe emptyList()
                waste.displayPile shouldBe emptyList()
            }
        }
        When("Calling undo") {
            waste.undo()
            Then("truePile should be empty") {
                waste.truePile shouldBe emptyList()
                waste.displayPile shouldBe emptyList()
            }
        }
        When("Adding cards and calling undo") {
            waste.addAll(cards)
            waste.undo()
            Then("truePile should contain cards face up") {
                waste.truePile shouldBe cards.map { it.copy(faceUp = true) }
                waste.displayPile shouldBe emptyList()
            }
        }
        When("Calling updateDisplayPile") {
            waste.updateDisplayPile()
            Then("truePile and displayPile should be equal") {
                waste.truePile.toList() shouldBe waste.displayPile.toList()
            }
        }
    }
    Given("Waste with no cards") {
        When("Calling add") {
            waste.addAll(cards)
            Then("All cards should be added to truePile face up") {
                waste.truePile shouldBe cards.map { it.copy(faceUp = true) }
                waste.displayPile shouldBe emptyList()
            }
        }
        When("Calling reset with no cards") {
            waste.reset()
            Then("No cards should exist in Waste") {
                waste.truePile shouldBe emptyList()
                waste.displayPile shouldBe emptyList()
            }
        }
        When("Calling reset with cards") {
            waste.reset(cards)
            Then("No cards should exist in Waste") {
                waste.truePile shouldBe emptyList()
                waste.displayPile shouldBe emptyList()
            }
        }
        When("Calling undo") {
            waste.undo()
            Then("No cards should exist in Waste") {
                waste.truePile shouldBe emptyList()
                waste.displayPile shouldBe emptyList()
            }
        }
    }
})