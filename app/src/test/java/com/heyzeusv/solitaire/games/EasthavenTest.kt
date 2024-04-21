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

class EasthavenTest : BehaviorSpec({
    var tableau: Tableau
    var foundation: Foundation
    Given("Easthaven game") {
        val eh = Easthaven
        When("Initialized") {
            Then("baseDeck should contain 52 cards by suit") {
                eh.baseDeck shouldBe tc.deck
            }
        }
        When("canAddToTableau is called") {
            And("cardsToAdd is empty") {
                tableau = Tableau(TableauZero)
                val result = eh.canAddToTableau(tableau, emptyList())
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Tableau contains card trying to be added") {
                tableau = Tableau(TableauZero, tc.sameSuitInOrder)
                val result = eh.canAddToTableau(tableau, listOf(tc.card5SFU))
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Tableau is not empty") {
                tableau = Tableau(TableauZero, listOf(tc.card6SFU))
                And("The first card of cardsToAdd is the same suit color as last card of Tableau") {
                    val result = eh.canAddToTableau(tableau, listOf(tc.card5CFU))
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
                And("The first card of cardsToAdd is not the same suit color as last card of Tableau") {
                    And("All cardsToAdd in order and alternating colors") {
                        val result = eh.canAddToTableau(tableau, tc.altColorInOrder)
                        Then("Result should be true") {
                            result shouldBe true
                        }
                    }
                    And("None of cardsToAdd in order") {
                        val result = eh.canAddToTableau(tableau, tc.altColorNotInOrder)
                        Then("Result should be false") {
                            result shouldBe false
                        }
                    }
                    And("Only first card in cardsToAdd in order") {
                        val result = eh.canAddToTableau(
                            tableau,
                            listOf(tc.card5DFU, tc.card6CFU) + tc.altColorNotInOrder
                        )
                        Then("Result should be false") {
                            result shouldBe false
                        }
                    }
                }
            }
            And("Tableau is empty") {
                tableau = Tableau(TableauZero)
                And("cardToAdd starts with a King") {
                    And("In order and alternating color") {
                        val result = eh.canAddToTableau(
                            tableau,
                            listOf(tc.card13HFU, tc.card12CFU, tc.card11DFU)
                        )
                        Then("Result should be true") {
                            result shouldBe true
                        }
                    }
                    And("In order but not alternating color") {
                        val result = eh.canAddToTableau(
                            tableau,
                            listOf(tc.card13HFU, tc.card12DFU, tc.card11CFU)
                        )
                        Then("Result should be false") {
                            result shouldBe false
                        }
                    }
                    And("Alternating color but not in order") {
                        val result = eh.canAddToTableau(
                            tableau,
                            listOf(tc.card13HFU, tc.card10CFU, tc.card8DFU)
                        )
                        Then("Result should be false") {
                            result shouldBe false
                        }
                    }
                }
                And("cardToAdd does not start with a King") {
                    And("In order and alternating color") {
                        val result = eh.canAddToTableau(
                            tableau,
                            listOf(tc.card5HFU, tc.card4CFU, tc.card3DFU)
                        )
                        Then("Result should be true") {
                            result shouldBe true
                        }
                    }
                    And("In order but not alternating color") {
                        val result = eh.canAddToTableau(
                            tableau,
                            listOf(tc.card5HFU, tc.card4DFU, tc.card3CFU)
                        )
                        Then("Result should be false") {
                            result shouldBe false
                        }
                    }
                    And("Alternating color but not in order") {
                        val result = eh.canAddToTableau(
                            tableau,
                            listOf(tc.card7HFU, tc.card2CFU, tc.card10DFU)
                        )
                        Then("Result should be false") {
                            result shouldBe false
                        }
                    }
                }
            }
        }
        When("canAddToFoundation is called") {
            foundation = Foundation(CLUBS, FoundationClubsOne, listOf(tc.card6CFU))
            And("cardToAdd is the correct Suit") {
                And("The correct next value") {
                    val result = eh.canAddToFoundation(foundation, tc.card7CFU)
                    Then("Result should be true") {
                        result shouldBe true
                    }
                }
                And("Not the correct next value") {
                    val result = eh.canAddToFoundation(foundation, tc.card8CFU)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
            }
            And("cardToAdd is not the correct Suit") {
                And("The correct next value") {
                    val result = eh.canAddToFoundation(foundation, tc.card7HFU)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
                And("Not the correct next value") {
                    val result = eh.canAddToFoundation(foundation, tc.card8HFU)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
            }
        }
        When("autocompleteTableauCheck is called") {
            And("Tableau contains face down cards") {
                tableau = Tableau(TableauZero, listOf(tc.card8H, tc.card7S))
                val result = eh.autocompleteTableauCheck(List(7) { tableau })
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Tableau contains cards not in order") {
                tableau = Tableau(TableauZero, listOf(tc.card8HFU, tc.card5SFU))
                val result = eh.autocompleteTableauCheck(List(7) { tableau })
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Tableau contains cards not in alternating color") {
                tableau = Tableau(TableauZero, listOf(tc.card8HFU, tc.card7DFU))
                val result = eh.autocompleteTableauCheck(List(7) { tableau })
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Tableau contains all face up cards, in order, and alternating color") {
                tableau = Tableau(TableauZero, listOf(tc.card8HFU, tc.card7CFU))
                val result = eh.autocompleteTableauCheck(List(7) { tableau })
                Then("Result should be true") {
                    result shouldBe true
                }
            }
        }
        When("resetTableau is called") {
            val tableauList = List(10) { Tableau(TableauZero, tc.clubs) }
            eh.resetTableau(tableauList, Stock(tc.deck))
            Then("Piles should be") {
                for (i in 0 until 7) {
                    tableauList[i] pilesNumFaceDownCardsShouldBe 2
                    tableauList[i] pilesNumFaceUpCardsShouldBe 1
                }
                for (i in 7 until 10) tableauList[i] pilesShouldBe tc.clubs
            }
        }
        When("resetFoundation is called") {
            val foundationList = List(8) { Foundation(CLUBS, FoundationClubsOne, tc.clubs)}
            eh.resetFoundation(foundationList, Stock())
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
                val result = eh.gameWon(foundationList)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("All 4 counted piles are valid") {
                val validList = List(4) { validFoundation }
                val invalidList = List(4) { inValidFoundation }
                val result = eh.gameWon(validList + invalidList)
                Then("Result should be true") {
                    result shouldBe true
                }
            }
        }
    }
})