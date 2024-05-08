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

class FortyAndEightTest : BehaviorSpec({
    var tableau: Tableau
    var foundation: Foundation
    Given("Forty and Eight game") {
        val forty = FortyAndEight
        When("Initialized") {
            Then("baseDeck should contain 104 cards by suit") {
                forty.baseDeck shouldBe tc.twoDeck
            }
        }
        When("canAddToTableau is called") {
            And("cardsToAdd is empty") {
                tableau = Tableau(TableauZero)
                val result = forty.canAddToTableau(tableau, emptyList())
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Tableau is not empty") {
                tableau = Tableau(TableauZero, listOf(tc.card6SFU))
                And("cardsToAdd is larger than one and is the same suit as last card of Tableau") {
                    val result = forty.canAddToTableau(tableau, tc.sameSuitInOrder)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
                And("cardsToAdd is larger than one and is the not suit as last card of Tableau") {
                    val result = forty.canAddToTableau(tableau, tc.multiSuitInOrder)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
                And("cardsToAdd is size 1 and is the same suit as last card of Tableau") {
                    val result = forty.canAddToTableau(tableau, listOf(tc.card5SFU))
                    Then("Result should be true") {
                        result shouldBe true
                    }
                }
                And("cardsToAdd is size 1 and is the not suit as last card of Tableau") {
                    val result = forty.canAddToTableau(tableau, listOf(tc.card5DFU))
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
            }
            And("Tableau is empty") {
                tableau = Tableau(TableauZero)
                And("cardsToAdd is larger than one") {
                    val result = forty.canAddToTableau(tableau, tc.sameSuitInOrder)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
                And("cardsToAdd is size 1 and is King") {
                    val result = forty.canAddToTableau(tableau, listOf(tc.card13SFU))
                    Then("Result should be true") {
                        result shouldBe true
                    }
                }
                And("cardsToAdd is size 1 and is not King") {
                    val result = forty.canAddToTableau(tableau, listOf(tc.card5SFU))
                    Then("Result should be true") {
                        result shouldBe true
                    }
                }
            }
        }
        When("canAddToFoundation is called") {
            foundation = Foundation(CLUBS, FoundationClubsOne, listOf(tc.card6CFU))
            And("cardToAdd is the correct Suit") {
                And("The correct next value") {
                    val result = forty.canAddToFoundation(foundation, tc.card7CFU)
                    Then("Result should be true") {
                        result shouldBe true
                    }
                }
                And("Not the correct next value") {
                    val result = forty.canAddToFoundation(foundation, tc.card8CFU)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
            }
            And("cardToAdd is not the correct Suit") {
                And("The correct next value") {
                    val result = forty.canAddToFoundation(foundation, tc.card7HFU)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
                And("Not the correct next value") {
                    val result = forty.canAddToFoundation(foundation, tc.card8HFU)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
            }
        }
        When("resetTableau is called") {
            val tableauList = List(10) { Tableau(TableauZero, tc.clubs) }
            forty.resetTableau(tableauList, Stock(tc.twoDeck))
            Then("Piles should be") {
                for (i in 0 until 8) {
                    tableauList[i] pilesNumFaceDownCardsShouldBe 0
                    tableauList[i] pilesNumFaceUpCardsShouldBe 5
                }
                for (i in 8 until 10) {
                    tableauList[i] pilesNumFaceDownCardsShouldBe 0
                    tableauList[i] pilesNumFaceUpCardsShouldBe 13
                }
            }
        }
        When("resetFoundation is called") {
            val foundationList = List(8) { Foundation(CLUBS, FoundationClubsOne, tc.clubs)}
            forty.resetFoundation(foundationList, Stock())
            Then("Piles should be") {
                for (i in 0 until 8) foundationList[i] pilesShouldBe emptyList()
            }
        }
        When("gameWon is called") {
            val validFoundation = Foundation(CLUBS, FoundationClubsOne, tc.clubs)
            val inValidFoundation = Foundation(CLUBS, FoundationClubsOne)
            And("Not all 8 counted piles are valid") {
                val foundationList = MutableList(10) { validFoundation }
                foundationList[3] = inValidFoundation
                val result = forty.gameWon(foundationList)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("All 8 counted piles are valid") {
                val validList = List(8) { validFoundation }
                val result = forty.gameWon(validList)
                Then("Result should be true") {
                    result shouldBe true
                }
            }
        }
    }
})