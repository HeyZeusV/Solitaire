package com.heyzeusv.solitaire.board

import com.heyzeusv.solitaire.board.animation.AnimateInfo
import com.heyzeusv.solitaire.board.animation.FlipCardInfo
import com.heyzeusv.solitaire.board.layouts.Width1080
import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.piles.ShuffleSeed
import com.heyzeusv.solitaire.games.AustralianPatience
import com.heyzeusv.solitaire.games.Easthaven
import com.heyzeusv.solitaire.games.Golf
import com.heyzeusv.solitaire.games.KlondikeTurnOne
import com.heyzeusv.solitaire.games.Spider
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.ViewModelBehaviorSpec
import com.heyzeusv.solitaire.util.pileSizesShouldBe
import com.heyzeusv.solitaire.util.pilesShouldBe
import com.heyzeusv.solitaire.util.pilesShouldNotBe
import com.heyzeusv.solitaire.util.tc
import com.heyzeusv.solitaire.util.valueShouldBe
import io.kotest.matchers.shouldBe
import java.util.Random

class GameViewModelTest : ViewModelBehaviorSpec({
    lateinit var vm: GameViewModel

    /**
     *  Helper function to call [onPileClick] and call actionBefore/AfterAnimation.
     */
    suspend fun onPileClick(onPileClick: () -> Unit) {
        onPileClick()
        vm.animateInfo.value?.actionBeforeAnimation?.invoke()
        vm.animateInfo.value?.actionAfterAnimation?.invoke()
    }

    beforeContainer {
        vm = GameViewModel(ShuffleSeed(Random(10L)), Width1080(0))
    }
    Given("The ViewModel") {
        When("Calling resetAll using NEW") {
            vm.resetAll(ResetOptions.NEW)
            Then("The state should be") {
                vm.stock pilesShouldNotBe emptyList()
                vm.waste pilesShouldBe emptyList()
                vm.historyList shouldBe emptyList()
                vm.undoEnabled valueShouldBe  false
                vm.undoAnimation valueShouldBe false
                vm.gameWon valueShouldBe false
                vm.autoCompleteActive valueShouldBe false
                vm.animateInfo valueShouldBe null
                vm.stockWasteEmpty valueShouldBe true
                for (i in 0..3) vm.foundation[i] pilesShouldBe emptyList()
                for (i in 0..6) vm.tableau[i] pilesShouldNotBe emptyList()
            }
        }
        When("Calling resetAll using RESTART after calling resetAll using NEW") {
            vm.resetAll(ResetOptions.NEW)
            val newStock = vm.stock.truePile.toList()
            val newTableau = mutableListOf<List<Card>>()
            for (i in 0..6) newTableau.add(vm.tableau[i].truePile.toList())
            vm.resetAll(ResetOptions.RESTART)
            Then("The state should be") {
                vm.stock pilesShouldBe newStock
                vm.waste pilesShouldBe emptyList()
                for (i in 0..3) vm.foundation[i] pilesShouldBe emptyList()
                for (i in 0..6) vm.tableau[i] pilesShouldBe newTableau[i]
            }
        }
        When("Calling resetAll using NEW after calling resetAll using NEW") {
            vm.resetAll(ResetOptions.NEW)
            val newStock = vm.stock.truePile.toList()
            val newTableau = mutableListOf<List<Card>>()
            for (i in 0..6) newTableau.add(vm.tableau[i].truePile.toList())
            vm.resetAll(ResetOptions.NEW)
            Then("The state should be") {
                vm.stock pilesShouldNotBe newStock
                vm.waste pilesShouldBe emptyList()
                for (i in 0..3) vm.foundation[i] pilesShouldBe emptyList()
                for (i in 0..6) vm.tableau[i] pilesShouldNotBe newTableau[i]
            }
        }
    }
    Given("Klondike Turn One as selectedGame") {
        beforeContainer {
            vm.updateSelectedGame(KlondikeTurnOne)
        }
        When("Calling onStockClick on empty Stock with redeals") {
            repeat(24) { onPileClick { vm.onStockClick() } }
            vm.updateAnimateInfo(null)
            onPileClick { vm.onStockClick() }
            val expectedAnimateInfo = AnimateInfo(
                start = GamePiles.Waste,
                end = GamePiles.Stock,
                animatedCards = tc.oneDeckUp.takeLast(1),
                flipCardInfo = FlipCardInfo.FaceDown.SinglePile
            )
            Then("AnimateInfo should be") {
                vm.animateInfo valueShouldBe expectedAnimateInfo
                vm.stockWasteEmpty valueShouldBe false
                vm.sbLogic.moves valueShouldBe 25
                vm.stock pilesShouldBe tc.oneDeckDown.subList(28, 52)
                vm.waste pilesShouldBe emptyList()
            }
        }
    }
    Given("Australian Patience as selectedGame") {
        beforeContainer {
            vm.updateSelectedGame(AustralianPatience)
        }
        When("Calling onStockClick on non-empty Stock") {
            onPileClick { vm.onStockClick() }
            val expectedAnimateInfo = AnimateInfo(
                start = GamePiles.Stock,
                end = GamePiles.Waste,
                animatedCards = listOf(tc.oneDeckDown[28]),
                flipCardInfo = FlipCardInfo.FaceUp.SinglePile
            )
            Then("AnimateInfo should be") {
                vm.animateInfo valueShouldBe expectedAnimateInfo
                vm.stockWasteEmpty valueShouldBe true
                vm.sbLogic.moves valueShouldBe 1
                vm.stock pilesShouldBe tc.oneDeckDown.subList(29, 52)
                vm.waste pilesShouldBe listOf(tc.oneDeckUp[28])
            }
        }
        When("Calling onStockClick on empty Stock with no redeals") {
            repeat(24) { onPileClick { vm.onStockClick() } }
            vm.updateAnimateInfo(null)
            onPileClick { vm.onStockClick() }
            Then("AnimateInfo should be") {
                vm.animateInfo valueShouldBe null
                vm.stockWasteEmpty valueShouldBe true
                vm.sbLogic.moves valueShouldBe 24
                vm.stock pilesShouldBe emptyList()
                vm.waste pilesShouldBe tc.oneDeckUp.subList(28, 52)
            }
        }
    }
    Given("Easthaven (7 pile game) as selectedGame") {
        beforeContainer {
            vm.updateSelectedGame(Easthaven)
        }
        When("Calling onStockClick on non-empty Stock") {
            repeat(5) { onPileClick { vm.onStockClick() } }
            val expectedAnimateInfo = AnimateInfo(
                start = GamePiles.Stock,
                end = GamePiles.TableauAll,
                animatedCards = tc.oneDeckDown.subList(49, 52),
                endTableauIndices = List(7) { 7 },
                flipCardInfo = FlipCardInfo.FaceUp.MultiPile
            )
            Then("AnimateInfo should be") {
                vm.animateInfo valueShouldBe expectedAnimateInfo
                vm.stockWasteEmpty valueShouldBe true
                vm.sbLogic.moves valueShouldBe 5
                vm.stock pilesShouldBe emptyList()
                for (i in 0..2) vm.tableau[i] pileSizesShouldBe 8
                for (i in 3..6) vm.tableau[i] pileSizesShouldBe 7
            }
        }
    }
    Given("Spider (10 pile game) as selectedGame") {
        beforeContainer {
            vm.updateSelectedGame(Spider)
        }
        When("Calling onStockClick on non-empty Stock") {
            repeat(5) { onPileClick { vm.onStockClick() } }
            val expectedAnimateInfo = AnimateInfo(
                start = GamePiles.Stock,
                end = GamePiles.TableauAll,
                animatedCards = tc.twoDeckDown.subList(94, 104),
                endTableauIndices = listOf(10, 10, 10, 10, 9, 9, 9, 9, 9, 9),
                flipCardInfo = FlipCardInfo.FaceUp.MultiPile
            )
            Then("AnimateInfo should be") {
                vm.animateInfo valueShouldBe expectedAnimateInfo
                vm.stockWasteEmpty valueShouldBe true
                vm.sbLogic.moves valueShouldBe 5
                vm.stock pilesShouldBe emptyList()
                for (i in 0..3) vm.tableau[i] pileSizesShouldBe 11
                for (i in 4..9) vm.tableau[i] pileSizesShouldBe 10
            }
        }
    }
    Given("Golf (7 pile game) as selectedGame") {
        beforeContainer {
            vm.updateSelectedGame(Golf)
        }
        When("Calling onStockClick on non-empty Stock") {
            onPileClick { vm.onStockClick() }
            val expectedAnimateInfo = AnimateInfo(
                start = GamePiles.Stock,
                end = GamePiles.FoundationSpadesOne,
                animatedCards = listOf(tc.oneDeckDown[36]),
                flipCardInfo = FlipCardInfo.FaceUp.SinglePile
            )
            Then("AnimateInfo should be") {
                vm.animateInfo valueShouldBe expectedAnimateInfo
                vm.stockWasteEmpty valueShouldBe true
                vm.sbLogic.moves valueShouldBe 1
                vm.stock pilesShouldBe tc.oneDeckDown.subList(37, 52)
                vm.foundation[3] pilesShouldBe listOf(tc.oneDeckUp[0], tc.oneDeckUp[36])
            }
        }
    }
})
