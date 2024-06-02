package com.heyzeusv.solitaire.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.heyzeusv.solitaire.board.animation.AnimationDurations
import com.heyzeusv.solitaire.games.FortyAndEight
import com.heyzeusv.solitaire.games.FortyThieves
import com.heyzeusv.solitaire.games.Games

/**
 *  Displays the correct [Games] [Board] that depends on currently selected game.
 *
 *  @param animationDurations The durations for each available animation.
 *  @param modifier Modifiers to be applied to the layout.
 *  @param gameVM Contains and handles game data.
 */
@Composable
fun Board(
    modifier: Modifier = Modifier,
    animationDurations: AnimationDurations,
    gameVM: GameViewModel = hiltViewModel(),
) {
    val stockWasteEmpty by gameVM.stockWasteEmpty.collectAsStateWithLifecycle()
    val animateInfo by gameVM.animateInfo.collectAsStateWithLifecycle()
    val spiderAnimateInfo by gameVM.spiderAnimateInfo.collectAsStateWithLifecycle()
    val undoAnimation by gameVM.isUndoAnimation.collectAsStateWithLifecycle()
    val selectedGame by gameVM.selectedGame.collectAsStateWithLifecycle()

    when (selectedGame) {
        is Games.AcesUpVariants -> {
            AcesUpBoard(
                modifier = modifier,
                layout = gameVM.screenLayouts.sevenWideFourTableauLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                updateAnimateInfo = gameVM::updateAnimateInfo,
                updateIsUndoEnabled = gameVM::updateIsUndoEnabled,
                isUndoAnimation = undoAnimation,
                updateIsUndoAnimation = gameVM::updateIsUndoAnimation,
                stock = gameVM.stock,
                onStockClick = gameVM::onStockClick,
                foundation = gameVM.foundation[3],
                tableauList = gameVM.tableau,
                onTableauClick = gameVM::onTableauClick,
            )
        }
        is Games.GolfFamily -> {
            GolfBoard(
                modifier = modifier,
                layout = gameVM.screenLayouts.sevenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                updateAnimateInfo = gameVM::updateAnimateInfo,
                updateIsUndoEnabled = gameVM::updateIsUndoEnabled,
                isUndoAnimation = undoAnimation,
                updateIsUndoAnimation = gameVM::updateIsUndoAnimation,
                stock = gameVM.stock,
                onStockClick = gameVM::onStockClick,
                foundation = gameVM.foundation[3],
                tableauList = gameVM.tableau,
                onTableauClick = gameVM::onTableauClick,
            )
        }
        is Games.SpiderFamily, is FortyThieves -> {
            TenWideBoard(
                modifier = modifier,
                layout = gameVM.screenLayouts.tenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                updateAnimateInfo = gameVM::updateAnimateInfo,
                spiderAnimateInfo = spiderAnimateInfo,
                updateSpiderAnimateInfo = gameVM::updateSpiderAnimateInfo,
                updateUndoEnabled = gameVM::updateIsUndoEnabled,
                undoAnimation = undoAnimation,
                updateUndoAnimation = gameVM::updateIsUndoAnimation,
                drawAmount = selectedGame.drawAmount,
                stock = gameVM.stock,
                onStockClick = gameVM::onStockClick,
                waste = gameVM.waste,
                stockWasteEmpty = { stockWasteEmpty },
                onWasteClick = gameVM::onWasteClick,
                foundationList = gameVM.foundation,
                tableauList = gameVM.tableau,
                onTableauClick = gameVM::onTableauClick,
            )
        }
        is FortyAndEight -> {
            TenWideEightTableauBoard(
                modifier = modifier,
                layout = gameVM.screenLayouts.tenWideEightTableauLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                updateAnimateInfo = gameVM::updateAnimateInfo,
                updateUndoEnabled = gameVM::updateIsUndoEnabled,
                undoAnimation = undoAnimation,
                updateUndoAnimation = gameVM::updateIsUndoAnimation,
                drawAmount = selectedGame.drawAmount,
                stock = gameVM.stock,
                onStockClick = gameVM::onStockClick,
                waste = gameVM.waste,
                stockWasteEmpty = { stockWasteEmpty },
                onWasteClick = gameVM::onWasteClick,
                foundationList = gameVM.foundation,
                tableauList = gameVM.tableau,
                onTableauClick = gameVM::onTableauClick,
            )
        }
        else -> {
            StandardBoard(
                modifier = modifier,
                layout = gameVM.screenLayouts.sevenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                updateAnimateInfo = gameVM::updateAnimateInfo,
                updateUndoEnabled = gameVM::updateIsUndoEnabled,
                undoAnimation = undoAnimation,
                updateUndoAnimation = gameVM::updateIsUndoAnimation,
                drawAmount = selectedGame.drawAmount,
                stock = gameVM.stock,
                onStockClick = gameVM::onStockClick,
                waste = gameVM.waste,
                stockWasteEmpty = { stockWasteEmpty },
                onWasteClick = gameVM::onWasteClick,
                foundationList = gameVM.foundation,
                onFoundationClick = gameVM::onFoundationClick,
                tableauList = gameVM.tableau,
                onTableauClick = gameVM::onTableauClick,
            )
        }
    }
}