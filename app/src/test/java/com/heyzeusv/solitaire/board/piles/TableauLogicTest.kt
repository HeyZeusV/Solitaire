package com.heyzeusv.solitaire.board.piles

import com.heyzeusv.solitaire.util.GamePiles.TableauNine
import com.heyzeusv.solitaire.util.tc
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class TableauLogicTest : BehaviorSpec({
    lateinit var tableau: Tableau
    val cards = listOf(tc.card1C, tc.card2D, tc.card3H, tc.card4S)
    beforeContainer {
        tableau = Tableau(TableauNine)
    }
    Given("Initial pile") {
        val initialPile = listOf(tc.card10D, tc.card10S, tc.card1H, tc.card4C)
        When("Tableau is initialized") {
            tableau = Tableau(TableauNine, initialPile)
            Then("truePile and displayPile are equal to initialPile") {
                tableau.truePile shouldBe initialPile
                tableau.displayPile shouldBe initialPile
            }
        }
    }
    Given("Tableau with cards") {
        beforeContainer {
            tableau.add(cards)
        }
        When("Calling add") {
            tableau.add(cards)
            Then("All cards should be added to truePile as is") {
                tableau.truePile shouldBe (cards + cards)
                tableau.displayPile shouldBe emptyList()
            }
        }
        When("Calling remove on last index") {
            tableau.remove(cards.size - 1)
            Then("Last card of truePile should be removed and new last card should be face up") {
                tableau.truePile shouldBe listOf(tc.card1C, tc.card2D, tc.card3HFU)
                tableau.displayPile shouldBe emptyList()
            }
        }
        When("Calling remove on second to last index") {
            tableau.remove(cards.size - 2)
            Then("Last 2 cards of truePile should be removed and new last card should be face up") {
                tableau.truePile shouldBe listOf(tc.card1C, tc.card2DFU)
                tableau.displayPile shouldBe emptyList()
            }
        }
        When("Calling remove on first index") {
            tableau.remove(0)
            Then("All cards of truePile should be removed") {
                tableau.truePile shouldBe emptyList()
                tableau.displayPile shouldBe emptyList()
            }
        }
        When("Calling undo") {
            tableau.undo()
            Then("truePile should be empty") {
                tableau.truePile shouldBe emptyList()
                tableau.displayPile shouldBe emptyList()
            }
        }
        When("Adding cards and calling undo") {
            tableau.add(cards)
            tableau.undo()
            Then("truePile should contain cards as is") {
                tableau.truePile shouldBe cards
                tableau.displayPile shouldBe emptyList()
            }
        }
        When("Calling updateDisplayPile") {
            tableau.updateDisplayPile()
            Then("truePile and displayPile should be equal") {
                tableau.truePile.toList() shouldBe tableau.displayPile.toList()
            }
        }
        When("Calling getTableauCardFlipInfo on last card") {
            val returnedInfo = tableau.getTableauCardFlipInfo(cards.size - 1)
            Then("Returned info should be valid") {
                returnedInfo shouldNotBe null
                returnedInfo!!.flipCard shouldBe tc.card3H
                returnedInfo.remainingPile shouldBe listOf(tc.card1C, tc.card2D)
            }
        }
        When("Calling getTableauCardFlipInfo on last card with second to last card face up") {
            val newTableau = Tableau(TableauNine, listOf(tc.card2D, tc.card3HFU, tc.card4S))
            val returnedInfo = newTableau.getTableauCardFlipInfo(2)
            Then("Returned info should be null") {
                returnedInfo shouldBe null
            }
        }
        When("Calling getTableauCardFlipInfo on out of bounds index") {
            val returnedInfo = tableau.getTableauCardFlipInfo(10)
            Then("Returned info should be null") {
                returnedInfo shouldBe null
            }
        }
    }
    Given("Tableau with no cards") {
        When("Calling add") {
            tableau.add(cards)
            Then("All cards should be added to truePile as is") {
                tableau.truePile shouldBe cards
                tableau.displayPile shouldBe emptyList()
            }
        }
        When("Calling reset with no cards") {
            tableau.reset()
            Then("No cards should exist in Tableau") {
                tableau.truePile shouldBe emptyList()
                tableau.displayPile shouldBe emptyList()
            }
        }
        When("Calling reset with cards") {
            tableau.reset(cards)
            Then("All cards should be added as is") {
                tableau.truePile shouldBe cards
                tableau.displayPile shouldBe cards
            }
        }
        When("Calling undo") {
            tableau.undo()
            Then("No cards should exist in Tableau") {
                tableau.truePile shouldBe emptyList()
                tableau.displayPile shouldBe emptyList()
            }
        }
    }
    Given("Custom Tableau piles") {
        When("Calling faceDownExists") {
            val trueTableau = Tableau(TableauNine, cards).faceDownExists()
            val falseTableau =
                Tableau(TableauNine, cards.map { it.copy(faceUp = true) }).faceDownExists()
            Then("Should be true") {
                trueTableau shouldBe true
            }
            Then("Should be false") {
                falseTableau shouldBe false
            }
        }
        When("Calling isMultiSuit") {
            val trueTableau = Tableau(TableauNine, cards).isMultiSuit()
            val falseTableau =
                Tableau(TableauNine, listOf(tc.card3H, tc.card4HFU, tc.card1H)).isMultiSuit()
            Then("Should be true") {
                trueTableau shouldBe true
            }
            Then("Should be false") {
                falseTableau shouldBe false
            }
        }
        When("Calling notInOrder") {
            val trueTableau =
                Tableau(TableauNine, listOf(tc.card3H, tc.card4HFU, tc.card1H)).notInOrder()
            val falseTableau = Tableau(TableauNine, cards.reversed()).notInOrder()
            Then("Should be true") {
                trueTableau shouldBe true
            }
            Then("Should be false") {
                falseTableau shouldBe false
            }
        }
        When("Calling notInOrderOrAltColor") {
            val trueTableau = Tableau(TableauNine, cards).notInOrderOrAltColor()
            val falseTableau = Tableau(TableauNine, listOf(tc.card7H, tc.card6CFU, tc.card5D))
                .notInOrderOrAltColor()
            Then("Should be true") {
                trueTableau shouldBe true
            }
            Then("Should be false") {
                falseTableau shouldBe false
            }
        }
    }
})