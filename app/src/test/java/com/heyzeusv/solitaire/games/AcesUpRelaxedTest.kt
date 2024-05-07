package com.heyzeusv.solitaire.games

import com.heyzeusv.solitaire.board.piles.Foundation
import com.heyzeusv.solitaire.board.piles.Stock
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.util.GamePiles.FoundationClubsOne
import com.heyzeusv.solitaire.util.GamePiles.TableauZero
import com.heyzeusv.solitaire.util.Suits.CLUBS
import com.heyzeusv.solitaire.util.pilesNumFaceDownCardsShouldBe
import com.heyzeusv.solitaire.util.pilesNumFaceUpCardsShouldBe
import com.heyzeusv.solitaire.util.pilesShouldBe
import com.heyzeusv.solitaire.util.tc
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class AcesUpRelaxedTest : BehaviorSpec({
    var tableau: Tableau
    Given("Aces Up game") {
        val acesUp = AcesUpRelaxed
        When("Initialized") {
            Then("baseDeck should contain 52 cards by suit") {
                acesUp.baseDeck shouldBe tc.deck
            }
        }
        When("canAddToTableau is called") {
            tableau = Tableau(TableauZero, listOf(tc.card6SFU))
            var result1 = acesUp.canAddToTableau(tableau, tc.sameSuitInOrder)
            var result2 = acesUp.canAddToTableau(tableau, tc.sameSuitInOrderAsc)
            var result3 = acesUp.canAddToTableau(tableau, tc.sameSuitNotInOrder)
            var result4 = acesUp.canAddToTableau(tableau, tc.altColorInOrder)
            var result5 = acesUp.canAddToTableau(tableau, tc.altColorNotInOrder)
            var result6 = acesUp.canAddToTableau(tableau, tc.multiSuitInOrder)
            var result7 = acesUp.canAddToTableau(tableau, listOf(tc.card1CFU))
            var result8 = acesUp.canAddToTableau(tableau, listOf(tc.card6CFU))
            And("Tableau is not empty") {
                Then("All results should be false") {
                    result1 shouldBe false
                    result2 shouldBe false
                    result3 shouldBe false
                    result4 shouldBe false
                    result5 shouldBe false
                    result6 shouldBe false
                    result7 shouldBe false
                    result8 shouldBe false
                }
            }
            And("Tableau is empty") {
                tableau = Tableau(TableauZero)
                result1 = acesUp.canAddToTableau(tableau, tc.sameSuitInOrder)
                result2 = acesUp.canAddToTableau(tableau, tc.sameSuitInOrderAsc)
                result3 = acesUp.canAddToTableau(tableau, tc.sameSuitNotInOrder)
                result4 = acesUp.canAddToTableau(tableau, tc.altColorInOrder)
                result5 = acesUp.canAddToTableau(tableau, tc.altColorNotInOrder)
                result6 = acesUp.canAddToTableau(tableau, tc.multiSuitInOrder)
                result7 = acesUp.canAddToTableau(tableau, listOf(tc.card1CFU))
                result8 = acesUp.canAddToTableau(tableau, listOf(tc.card6CFU))
                Then("All results should be false") {
                    result1 shouldBe false
                    result2 shouldBe false
                    result3 shouldBe false
                    result4 shouldBe false
                    result5 shouldBe false
                    result6 shouldBe false
                    result7 shouldBe true
                    result8 shouldBe true
                }
            }
        }
        When("canAddToFoundation is called") {
            And("All top cards of Tableau are different suit and higher value") {
                val tableauList = listOf(
                    Tableau(TableauZero, listOf(tc.card8HFU)),
                    Tableau(TableauZero, listOf(tc.card9CFU)),
                    Tableau(TableauZero, listOf(tc.card10DFU)),
                    Tableau(TableauZero, listOf(tc.card11SFU))
                )
                val result = acesUp.canAddToFoundation(tableauList, tc.card8HFU)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("All top cards of Tableau are different suit and lower value") {
                val tableauList = listOf(
                    Tableau(TableauZero, listOf(tc.card8HFU)),
                    Tableau(TableauZero, listOf(tc.card7CFU)),
                    Tableau(TableauZero, listOf(tc.card6DFU)),
                    Tableau(TableauZero, listOf(tc.card5SFU))
                )
                val result = acesUp.canAddToFoundation(tableauList, tc.card8HFU)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("All top cards of Tableau are same suit and higher value") {
                val tableauList = listOf(
                    Tableau(TableauZero, listOf(tc.card8HFU)),
                    Tableau(TableauZero, listOf(tc.card10HFU)),
                    Tableau(TableauZero, listOf(tc.card11HFU)),
                    Tableau(TableauZero, listOf(tc.card12HFU))
                )
                val result = acesUp.canAddToFoundation(tableauList, tc.card8HFU)
                Then("Result should be true") {
                    result shouldBe true
                }
            }
            And("All top cards of Tableau are same suit and lower value") {
                val tableauList = listOf(
                    Tableau(TableauZero, listOf(tc.card8HFU)),
                    Tableau(TableauZero, listOf(tc.card7HFU)),
                    Tableau(TableauZero, listOf(tc.card6HFU)),
                    Tableau(TableauZero, listOf(tc.card5HFU))
                )
                val result = acesUp.canAddToFoundation(tableauList, tc.card8HFU)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Card beneath top card is same suit and higher value") {
                val tableauList = listOf(
                    Tableau(TableauZero, listOf(tc.card9HFU, tc.card8HFU)),
                    Tableau(TableauZero, listOf(tc.card7CFU)),
                    Tableau(TableauZero, listOf(tc.card6DFU)),
                    Tableau(TableauZero, listOf(tc.card5SFU))
                )
                val result = acesUp.canAddToFoundation(tableauList, tc.card8HFU)
                Then("Result should be false") {
                    result shouldBe true
                }
            }
            And("Card beneath top card is same suit and lower value") {
                val tableauList = listOf(
                    Tableau(TableauZero, listOf(tc.card7HFU, tc.card8HFU)),
                    Tableau(TableauZero, listOf(tc.card7CFU)),
                    Tableau(TableauZero, listOf(tc.card6DFU)),
                    Tableau(TableauZero, listOf(tc.card5SFU))
                )
                val result = acesUp.canAddToFoundation(tableauList, tc.card8HFU)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Card beneath top card is different suit and higher value") {
                val tableauList = listOf(
                    Tableau(TableauZero, listOf(tc.card9CFU, tc.card8HFU)),
                    Tableau(TableauZero, listOf(tc.card7CFU)),
                    Tableau(TableauZero, listOf(tc.card6DFU)),
                    Tableau(TableauZero, listOf(tc.card5SFU))
                )
                val result = acesUp.canAddToFoundation(tableauList, tc.card8HFU)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Card beneath top card is different suit and lower value") {
                val tableauList = listOf(
                    Tableau(TableauZero, listOf(tc.card7CFU, tc.card8HFU)),
                    Tableau(TableauZero, listOf(tc.card7CFU)),
                    Tableau(TableauZero, listOf(tc.card6DFU)),
                    Tableau(TableauZero, listOf(tc.card5SFU))
                )
                val result = acesUp.canAddToFoundation(tableauList, tc.card8HFU)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Try to move an Ace to Foundation") {
                val tableauList = listOf(
                    Tableau(TableauZero, listOf(tc.card1HFU)),
                    Tableau(TableauZero, listOf(tc.card7HFU)),
                    Tableau(TableauZero, listOf(tc.card6HFU)),
                    Tableau(TableauZero, listOf(tc.card5HFU))
                )
                val result = acesUp.canAddToFoundation(tableauList, tc.card1HFU)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
        }
        When("resetTableau is called") {
            val tableauList = List(10) { Tableau(TableauZero, tc.clubs) }
            acesUp.resetTableau(tableauList, Stock(tc.deck))
            Then("Piles should be") {
                for (i in 0 until 4) {
                    tableauList[i] pilesNumFaceDownCardsShouldBe 0
                    tableauList[i] pilesNumFaceUpCardsShouldBe 1
                }
                for (i in 4 until 10) tableauList[i] pilesShouldBe tc.clubs
            }
        }
        When("resetFoundation is called") {
            val foundationList = List(8) { Foundation(CLUBS, FoundationClubsOne, tc.clubs) }
            acesUp.resetFoundation(foundationList, Stock(tc.deck))
            Then("Piles should be") {
                foundationList[0] pilesShouldBe emptyList()
                for (i in 1 until 8) foundationList[i] pilesShouldBe tc.clubs
            }
        }
        When("gameWon is called") {
            val validFoundation = Foundation(CLUBS, FoundationClubsOne, tc.deckNoAces)
            val invalidFoundation = Foundation(CLUBS, FoundationClubsOne)
            And("Foundation 0 is valid") {
                val foundationList =
                    listOf(validFoundation, invalidFoundation, invalidFoundation, validFoundation)
                val result = acesUp.gameWon(foundationList)
                Then("Result should be true") {
                    result shouldBe true
                }
            }
            And("Foundation 0 is invalid") {
                val foundationList =
                    listOf(invalidFoundation, invalidFoundation, invalidFoundation, validFoundation)
                val result = acesUp.gameWon(foundationList)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
        }
    }
})