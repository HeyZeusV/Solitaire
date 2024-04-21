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

class GolfRushTest : BehaviorSpec({
    var tableau: Tableau
    var foundation: Foundation
    Given("Golf Rush game") {
        val golf = GolfRush
        When("Initialized") {
            Then("baseDeck should contain 52 cards by suit") {
                golf.baseDeck shouldBe tc.deck
            }
        }
        When("canAddToTableau is called") {
            And("Tableau is not empty") {
                tableau = Tableau(TableauZero, listOf(tc.card6SFU))
                var result1 = golf.canAddToTableau(tableau, tc.sameSuitInOrder)
                var result2 = golf.canAddToTableau(tableau, tc.sameSuitInOrderAsc)
                var result3 = golf.canAddToTableau(tableau, tc.sameSuitNotInOrder)
                var result4 = golf.canAddToTableau(tableau, tc.altColorInOrder)
                var result5 = golf.canAddToTableau(tableau, tc.altColorNotInOrder)
                var result6 = golf.canAddToTableau(tableau, tc.multiSuitInOrder)
                var result7 = golf.canAddToTableau(tableau, tc.multiSuitNotInOrder)
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
                    result1 = golf.canAddToTableau(tableau, tc.sameSuitInOrder)
                    result2 = golf.canAddToTableau(tableau, tc.sameSuitInOrderAsc)
                    result3 = golf.canAddToTableau(tableau, tc.sameSuitNotInOrder)
                    result4 = golf.canAddToTableau(tableau, tc.altColorInOrder)
                    result5 = golf.canAddToTableau(tableau, tc.altColorNotInOrder)
                    result6 = golf.canAddToTableau(tableau, tc.multiSuitInOrder)
                    result7 = golf.canAddToTableau(tableau, tc.multiSuitNotInOrder)
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
                val result = golf.canAddToFoundation(foundation, tc.card7CFU)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            foundation = Foundation(CLUBS, FoundationClubsOne, listOf(tc.card6CFU))
            And("cardToAdd is the correct next value ascending") {
                val result = golf.canAddToFoundation(foundation, tc.card7SFU)
                Then("Result should be true") {
                    result shouldBe true
                }
            }
            And("cardToAdd is the correct next value descending") {
                val result = golf.canAddToFoundation(foundation, tc.card5DFU)
                Then("Result should be true") {
                    result shouldBe true
                }
            }
        }
        When("resetTableau is called") {
            val tableauList = List(10) { Tableau(TableauZero, tc.clubs) }
            golf.resetTableau(tableauList, Stock(tc.deck))
            Then("Piles should be") {
                for (i in 0 until 7) {
                    tableauList[i] pilesNumFaceDownCardsShouldBe 0
                    tableauList[i] pilesNumFaceUpCardsShouldBe i + 1
                }
                for (i in 7 until 10) tableauList[i] pilesShouldBe tc.clubs
            }
        }
        When("resetFoundation is called") {
            val foundationList = List(8) { Foundation(CLUBS, FoundationClubsOne, tc.clubs) }
            golf.resetFoundation(foundationList, Stock(tc.deck))
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
                val result = golf.gameWon(foundationList)
                Then("Result should be true") {
                    result shouldBe true
                }
            }
            And("Foundation 3 is invalid") {
                val foundationList =
                    listOf(validFoundation, inValidFoundation, inValidFoundation, inValidFoundation)
                val result = golf.gameWon(foundationList)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
        }
    }
})