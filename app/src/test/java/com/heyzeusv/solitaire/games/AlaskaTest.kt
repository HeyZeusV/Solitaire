package com.heyzeusv.solitaire.games

import com.heyzeusv.solitaire.board.piles.Foundation
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.util.GamePiles.FoundationClubsOne
import com.heyzeusv.solitaire.util.GamePiles.TableauZero
import com.heyzeusv.solitaire.util.Suits.CLUBS
import com.heyzeusv.solitaire.util.tc
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class AlaskaTest : BehaviorSpec({
    var tableau: Tableau
    var foundation: Foundation
    Given("Alaska game") {
        val alaska = Alaska
        When("Initialized") {
            Then("baseDeck should contain 52 cards by suit") {
                alaska.baseDeck shouldBe tc.deck
            }
        }
        When("canAddToTableau is called") {
            And("cardsToAdd is empty") {
                tableau = Tableau(TableauZero)
                val result = alaska.canAddToTableau(tableau, emptyList())
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Tableau contains card trying to be added") {
                tableau = Tableau(TableauZero, tc.sameSuitInOrder)
                val result = alaska.canAddToTableau(tableau, listOf(tc.card5SFU))
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Tableau is not empty") {
                tableau = Tableau(TableauZero, listOf(tc.card6SFU))
                And("The first card of cardsToAdd is the same suit as last card of Tableau") {
                    And("Descending") {
                        val result = alaska.canAddToTableau(tableau, tc.multiSuitInOrder)
                        Then("Result should be true") {
                            result shouldBe true
                        }
                    }
                    And("Ascending") {
                        val result = alaska.canAddToTableau(tableau, tc.sameSuitInOrderAsc)
                        Then("Result should be true") {
                            result shouldBe true
                        }
                    }
                    And("Not in order") {
                        val result = alaska.canAddToTableau(tableau, tc.sameSuitNotInOrder)
                        Then("Result should be false") {
                            result shouldBe false
                        }
                    }
                }
                And("The first card of cardsToAdd is not the same suit as last card of Tableau") {
                    val result = alaska.canAddToTableau(tableau, listOf(tc.card7HFU))
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
            }
            And("Tableau is empty") {
                tableau = Tableau(TableauZero)
                And("cardToAdd starts with a King") {
                    val result = alaska.canAddToTableau(tableau, listOf(tc.card13HFU))
                    Then("Result should be true") {
                        result shouldBe true
                    }
                }
                And("cardToAdd does not start with a King") {
                    val result = alaska.canAddToTableau(tableau, listOf(tc.card10HFU))
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
            }
        }
        When("canAddToFoundation is called") {
            foundation = Foundation(CLUBS, FoundationClubsOne)
            And("cardToAdd is the correct next value") {

            }
        }
    }
})