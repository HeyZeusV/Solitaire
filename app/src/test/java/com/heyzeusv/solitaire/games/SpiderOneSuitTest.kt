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

class SpiderOneSuitTest : BehaviorSpec({
    var tableau: Tableau
    var foundation: Foundation
    Given("Spider game") {
        val spider = SpiderOneSuit
        When("Initialized") {
            Then("baseDeck should contain 104 cards by suit") {
                spider.baseDeck shouldBe tc.twoDeckOneSuit
            }
        }
        When("canAddToTableau is called") {
            And("cardsToAdd is empty") {
                tableau = Tableau(TableauZero)
                val result = spider.canAddToTableau(tableau, emptyList())
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("Tableau is not empty") {
                tableau = Tableau(TableauZero, listOf(tc.card6SFU))
                And("The first card of cardsToAdd is the same suit as last card of Tableau") {
                    And("Descending") {
                        val result = spider.canAddToTableau(tableau, tc.sameSuitInOrder)
                        Then("Result should be true") {
                            result shouldBe true
                        }
                    }
                    And("Not in order") {
                        val result = spider.canAddToTableau(tableau, tc.sameSuitNotInOrder)
                        Then("Result should be false") {
                            result shouldBe false
                        }
                    }
                }
                And("The first card of cardsToAdd is not the same suit as last card of Tableau") {
                    val result = spider.canAddToTableau(tableau, listOf(tc.card7HFU))
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
            }
            And("Tableau is empty") {
                tableau = Tableau(TableauZero)
                And("cardToAdd starts with a King") {
                    And("In order and alternating color") {
                        val result = spider.canAddToTableau(
                            tableau,
                            listOf(tc.card13HFU, tc.card12CFU, tc.card11DFU)
                        )
                        Then("Result should be false") {
                            result shouldBe false
                        }
                    }
                    And("In order but not alternating color") {
                        val result = spider.canAddToTableau(
                            tableau,
                            listOf(tc.card13HFU, tc.card12DFU, tc.card11CFU)
                        )
                        Then("Result should be false") {
                            result shouldBe false
                        }
                    }
                    And("Alternating color but not in order") {
                        val result = spider.canAddToTableau(
                            tableau,
                            listOf(tc.card13HFU, tc.card10CFU, tc.card8DFU)
                        )
                        Then("Result should be false") {
                            result shouldBe false
                        }
                    }
                    And("In order and single suit") {
                        val result = spider.canAddToTableau(
                            tableau,
                            listOf(tc.card13HFU, tc.card12HFU, tc.card11HFU)
                        )
                        Then("Result should be true") {
                            result shouldBe true
                        }
                    }
                }
                And("cardToAdd does not start with a King") {
                    And("In order and alternating color") {
                        val result = spider.canAddToTableau(
                            tableau,
                            listOf(tc.card5HFU, tc.card4CFU, tc.card3DFU)
                        )
                        Then("Result should be false") {
                            result shouldBe false
                        }
                    }
                    And("In order but not alternating color") {
                        val result = spider.canAddToTableau(
                            tableau,
                            listOf(tc.card5HFU, tc.card4DFU, tc.card3CFU)
                        )
                        Then("Result should be false") {
                            result shouldBe false
                        }
                    }
                    And("Alternating color but not in order") {
                        val result = spider.canAddToTableau(
                            tableau,
                            listOf(tc.card7HFU, tc.card2CFU, tc.card10DFU)
                        )
                        Then("Result should be false") {
                            result shouldBe false
                        }
                    }
                    And("In order and single suit") {
                        val result = spider.canAddToTableau(
                            tableau,
                            listOf(tc.card5HFU, tc.card4HFU, tc.card3HFU)
                        )
                        Then("Result should be true") {
                            result shouldBe true
                        }
                    }
                }
            }
        }
        When("canAddToFoundation is called") {
            foundation = Foundation(CLUBS, FoundationClubsOne, listOf(tc.card6CFU))
            And("cardToAdd is the correct Suit") {
                And("The correct next value") {
                    val result = spider.canAddToFoundation(foundation, tc.card7CFU)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
                And("Not the correct next value") {
                    val result = spider.canAddToFoundation(foundation, tc.card8CFU)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
            }
            And("cardToAdd is not the correct Suit") {
                And("The correct next value") {
                    val result = spider.canAddToFoundation(foundation, tc.card7HFU)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
                And("Not the correct next value") {
                    val result = spider.canAddToFoundation(foundation, tc.card8HFU)
                    Then("Result should be false") {
                        result shouldBe false
                    }
                }
            }
        }
        When("resetTableau is called") {
            val tableauList = List(10) { Tableau(TableauZero, tc.clubs) }
            spider.resetTableau(tableauList, Stock(tc.twoDeck))
            Then("Piles should be") {
                for (i in 0 until 4) {
                    tableauList[i] pilesNumFaceDownCardsShouldBe 5
                    tableauList[i] pilesNumFaceUpCardsShouldBe 1
                }
                for (i in 4 until 10) {
                    tableauList[i] pilesNumFaceDownCardsShouldBe 4
                    tableauList[i] pilesNumFaceUpCardsShouldBe 1
                }
            }
        }
        When("resetFoundation is called") {
            val foundationList = List(8) { Foundation(CLUBS, FoundationClubsOne, tc.clubs)}
            spider.resetFoundation(foundationList, Stock())
            Then("Piles should be") {
                for (i in 0 until 8) foundationList[i] pilesShouldBe emptyList()
            }
        }
        When("gameWon is called") {
            val validFoundation = Foundation(CLUBS, FoundationClubsOne, tc.clubs)
            val inValidFoundation = Foundation(CLUBS, FoundationClubsOne)
            And("Not all 4 counted piles are valid") {
                val foundationList = MutableList(10) { validFoundation }
                foundationList[3] = inValidFoundation
                val result = spider.gameWon(foundationList)
                Then("Result should be false") {
                    result shouldBe false
                }
            }
            And("All 8 counted piles are valid") {
                val validList = List(8) { validFoundation }
                val result = spider.gameWon(validList)
                Then("Result should be true") {
                    result shouldBe true
                }
            }
        }
    }
})