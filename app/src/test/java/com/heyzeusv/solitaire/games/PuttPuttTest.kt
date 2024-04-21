package com.heyzeusv.solitaire.games

import com.heyzeusv.solitaire.board.piles.Foundation
import com.heyzeusv.solitaire.board.piles.Stock
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.util.GamePiles.FoundationClubsOne
import com.heyzeusv.solitaire.util.GamePiles.TableauZero
import com.heyzeusv.solitaire.util.Suits.CLUBS
import com.heyzeusv.solitaire.util.pileSizesShouldBe
import com.heyzeusv.solitaire.util.pilesNumFaceDownCardsShouldBe
import com.heyzeusv.solitaire.util.pilesNumFaceUpCardsShouldBe
import com.heyzeusv.solitaire.util.pilesShouldBe
import com.heyzeusv.solitaire.util.pilesShouldNotBe
import com.heyzeusv.solitaire.util.tc
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class PuttPuttTest : BehaviorSpec({
    var tableau: Tableau
    var foundation: Foundation
    Given("Putt Putt game") {
        val puttPutt = PuttPutt
        When("Initialized") {
            Then("baseDeck should contain 52 cards by suit") {
                puttPutt.baseDeck shouldBe tc.deck
            }
        }
        When("canAddToTableau is called") {
            And("Tableau is not empty") {
                tableau = Tableau(TableauZero, listOf(tc.card6SFU))
                var result1 = puttPutt.canAddToTableau(tableau, tc.sameSuitInOrder)
                var result2 = puttPutt.canAddToTableau(tableau, tc.sameSuitInOrderAsc)
                var result3 = puttPutt.canAddToTableau(tableau, tc.sameSuitNotInOrder)
                var result4 = puttPutt.canAddToTableau(tableau, tc.altColorInOrder)
                var result5 = puttPutt.canAddToTableau(tableau, tc.altColorNotInOrder)
                var result6 = puttPutt.canAddToTableau(tableau, tc.multiSuitInOrder)
                var result7 = puttPutt.canAddToTableau(tableau, tc.multiSuitNotInOrder)
                Then("All results should be false") {
                    result1 shouldBe false
                    result2 shouldBe false
                    result3 shouldBe false
                    result4 shouldBe false
                    result5 shouldBe false
                    result6 shouldBe false
                    result7 shouldBe false
                }
                And("Tableau is empty") {
                    tableau = Tableau(TableauZero)
                    result1 = puttPutt.canAddToTableau(tableau, tc.sameSuitInOrder)
                    result2 = puttPutt.canAddToTableau(tableau, tc.sameSuitInOrderAsc)
                    result3 = puttPutt.canAddToTableau(tableau, tc.sameSuitNotInOrder)
                    result4 = puttPutt.canAddToTableau(tableau, tc.altColorInOrder)
                    result5 = puttPutt.canAddToTableau(tableau, tc.altColorNotInOrder)
                    result6 = puttPutt.canAddToTableau(tableau, tc.multiSuitInOrder)
                    result7 = puttPutt.canAddToTableau(tableau, tc.multiSuitNotInOrder)
                    Then("All results should be false") {
                        result1 shouldBe false
                        result2 shouldBe false
                        result3 shouldBe false
                        result4 shouldBe false
                        result5 shouldBe false
                        result6 shouldBe false
                        result7 shouldBe false
                    }
                }
            }
        }
        When("canAddToFoundation is called") {
            And("Foundation is empty") {
                foundation = Foundation(CLUBS, FoundationClubsOne)
                val result = puttPutt.canAddToFoundation(foundation, tc.card7CFU)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            foundation = Foundation(CLUBS, FoundationClubsOne, listOf(tc.card6CFU))
            And("cardToAdd is the correct next value ascending") {
                val result = puttPutt.canAddToFoundation(foundation, tc.card7SFU)
                Then("Result should be true") {
                    result shouldBe true
                }
            }
            And("cardToAdd is the correct next value descending") {
                val result = puttPutt.canAddToFoundation(foundation, tc.card5DFU)
                Then("Result should be true") {
                    result shouldBe true
                }
            }
            foundation = Foundation(CLUBS, FoundationClubsOne, listOf(tc.card1CFU))
            And("cardToAdd is King to wrap from Ace") {
                val result = puttPutt.canAddToFoundation(foundation, tc.card13SFU)
                Then("Result should be true") {
                    result shouldBe true
                }
            }
            foundation = Foundation(CLUBS, FoundationClubsOne, listOf(tc.card13CFU))
            And("cardToAdd is Ace to wrap from King") {
                val result = puttPutt.canAddToFoundation(foundation, tc.card1DFU)
                Then("Result should be true") {
                    result shouldBe true
                }
            }
        }
        When("resetTableau is called") {
            val tableauList = List(10) { Tableau(TableauZero, tc.clubs) }
            puttPutt.resetTableau(tableauList, Stock(tc.deck))
            Then("Piles should be") {
                for (i in 0 until 7) {
                    tableauList[i] pilesNumFaceDownCardsShouldBe 0
                    tableauList[i] pilesNumFaceUpCardsShouldBe 5
                }
                for (i in 7 until 10) tableauList[i] pilesShouldBe tc.clubs
            }
        }
        When("resetFoundation is called") {
            val foundationList = List(8) { Foundation(CLUBS, FoundationClubsOne, tc.clubs) }
            puttPutt.resetFoundation(foundationList, Stock(tc.deck))
            Then("Piles should be") {
                for (i in 0 until 3) foundationList[i] pilesShouldBe tc.clubs
                foundationList[3] pilesShouldNotBe emptyList()
                foundationList[3] pileSizesShouldBe 1
                for (i in 4 until 8) foundationList[i] pilesShouldBe tc.clubs
            }
        }
        When("gameWon is called") {
            val validFoundation = Foundation(CLUBS, FoundationClubsOne, tc.deck)
            val inValidFoundation = Foundation(CLUBS, FoundationClubsOne)
            And("Foundation 3 is valid") {
                val foundationList =
                    listOf(validFoundation, inValidFoundation, inValidFoundation, validFoundation)
                val result = puttPutt.gameWon(foundationList)
                Then("Result should be true") {
                    result shouldBe true
                }
            }
            And("Foundation 3 is invalid") {
                val foundationList =
                    listOf(validFoundation, inValidFoundation, inValidFoundation, inValidFoundation)
                val result = puttPutt.gameWon(foundationList)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
        }
    }
})