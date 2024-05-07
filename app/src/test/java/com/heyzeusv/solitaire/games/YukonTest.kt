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

class YukonTest : BehaviorSpec({
    var tableau: Tableau
    var foundation: Foundation
    Given("Yukon game") {
        val yukon = Yukon
        When("Initialized") {
            Then("baseDeck should contain 52 cards by suit") {
                yukon.baseDeck shouldBe tc.deck
            }
        }
        When("canAddToTableau is called") {
            And("cardsToAdd is empty") {
                tableau = Tableau(TableauZero)
                val result = yukon.canAddToTableau(tableau, emptyList())
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Tableau contains card trying to be added") {
                tableau = Tableau(TableauZero, tc.sameSuitInOrder)
                val result = yukon.canAddToTableau(tableau, listOf(tc.card5SFU))
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Tableau is not empty") {
                tableau = Tableau(TableauZero, listOf(tc.card6SFU))
                And("The first card of cardsToAdd is the same suit color as last card of Tableau") {
                    val result = yukon.canAddToTableau(tableau, listOf(tc.card5CFU))
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
                And("The first card of cardsToAdd is not the same suit color as last card of Tableau") {
                    And("In order") {
                        val result = yukon.canAddToTableau(tableau, tc.altColorInOrder)
                        Then("Result should be true") {
                            result shouldBe true
                        }
                    }
                    And("Not in order") {
                        val result = yukon.canAddToTableau(tableau, tc.altColorNotInOrder)
                        Then("Result should be false") {
                            result shouldBe false
                        }
                    }
                }
            }
            And("Tableau is empty") {
                tableau = Tableau(TableauZero)
                And("cardToAdd starts with a King") {
                    val result = yukon.canAddToTableau(tableau, listOf(tc.card13HFU))
                    Then("Result should be true") {
                        result shouldBe true
                    }
                }
                And("cardToAdd does not start with a King") {
                    val result = yukon.canAddToTableau(tableau, listOf(tc.card10HFU))
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
            }
        }
        When("canAddToFoundation is called") {
            foundation = Foundation(CLUBS, FoundationClubsOne, listOf(tc.card6CFU))
            And("cardToAdd is the correct Suit") {
                And("The correct next value") {
                    val result = yukon.canAddToFoundation(foundation, tc.card7CFU)
                    Then("Result should be true") {
                        result shouldBe true
                    }
                }
                And("Not the correct next value") {
                    val result = yukon.canAddToFoundation(foundation, tc.card8CFU)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
            }
            And("cardToAdd is not the correct Suit") {
                And("The correct next value") {
                    val result = yukon.canAddToFoundation(foundation, tc.card7HFU)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
                And("Not the correct next value") {
                    val result = yukon.canAddToFoundation(foundation, tc.card8HFU)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
            }
        }
        When("autocompleteTableauCheck is called") {
            And("Tableau contains face down cards") {
                tableau = Tableau(TableauZero, listOf(tc.card8H, tc.card7H))
                val result = yukon.autocompleteTableauCheck(List(7) { tableau })
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Tableau contains cards not in order") {
                tableau = Tableau(TableauZero, listOf(tc.card8HFU, tc.card9HFU))
                val result = yukon.autocompleteTableauCheck(List(7) { tableau })
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Tableau contains all face up cards and in order") {
                tableau = Tableau(TableauZero, listOf(tc.card8HFU, tc.card7HFU))
                val result = yukon.autocompleteTableauCheck(List(7) { tableau })
                Then("Result should be true") {
                    result shouldBe true
                }
            }
        }
        When("resetTableau is called") {
            val tableauList = List(10) { Tableau(TableauZero, tc.clubs) }
            yukon.resetTableau(tableauList, Stock(tc.deck))
            Then("Piles should be") {
                tableauList[0] pilesNumFaceDownCardsShouldBe 0
                tableauList[0] pilesNumFaceUpCardsShouldBe 1
                for (i in 1 until 7) {
                    tableauList[i] pilesNumFaceDownCardsShouldBe i
                    tableauList[i] pilesNumFaceUpCardsShouldBe 5
                }
                for (i in 7 until 10) tableauList[i] pilesShouldBe tc.clubs
            }
        }
        When("resetFoundation is called") {
            val foundationList = List(8) { Foundation(CLUBS, FoundationClubsOne, tc.clubs)}
            yukon.resetFoundation(foundationList, Stock())
            Then("Piles should be") {
                for (i in 0 until 4) foundationList[i] pilesShouldBe emptyList()
                for (i in 4 until 8) foundationList[i] pilesShouldBe tc.clubs
            }
        }
        When("gameWon is called") {
            val validFoundation = Foundation(CLUBS, FoundationClubsOne, tc.clubs)
            val inValidFoundation = Foundation(CLUBS, FoundationClubsOne)
            And("Not all 4 counted piles are valid") {
                val foundationList = listOf(validFoundation, validFoundation, validFoundation, inValidFoundation)
                val result = yukon.gameWon(foundationList)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("All 4 counted piles are valid") {
                val validList = List(4) { validFoundation }
                val invalidList = List(4) { inValidFoundation }
                val result = yukon.gameWon(validList + invalidList)
                Then("Result should be true") {
                    result shouldBe true
                }
            }
        }
    }
})