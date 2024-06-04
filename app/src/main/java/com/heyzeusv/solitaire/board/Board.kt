package com.heyzeusv.solitaire.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.heyzeusv.solitaire.board.layouts.ScreenLayouts
import com.heyzeusv.solitaire.games.FortyAndEight
import com.heyzeusv.solitaire.games.FortyThieves
import com.heyzeusv.solitaire.games.Games

/**
 *  Displays the correct [Games] [Board] that depends on currently selected game.
 *
 *  @param modifier Modifiers to be applied to the layout.
 *  @param screenLayouts Provides pile positions and Card sizes.
 *  @param gameVM Contains and handles game data.
 */
@Composable
fun Board(
    modifier: Modifier = Modifier,
    screenLayouts: ScreenLayouts,
    gameVM: GameViewModel = hiltViewModel(),
) {
    val settings by gameVM.settingsFlow.collectAsStateWithLifecycle()
    val stockWasteEmpty by gameVM.stockWasteEmpty.collectAsStateWithLifecycle()
    val animateInfo by gameVM.animateInfo.collectAsStateWithLifecycle()
    val spiderAnimateInfo by gameVM.spiderAnimateInfo.collectAsStateWithLifecycle()
    val undoAnimation by gameVM.isUndoAnimation.collectAsStateWithLifecycle()
    val selectedGame by gameVM.selectedGame.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = settings.selectedGame) {
        gameVM.updateSelectedGame(Games.getGameClass(settings.selectedGame))
    }

    when (selectedGame) {
        is Games.AcesUpVariants -> {
            AcesUpBoard(
                modifier = modifier,
                layout = screenLayouts.sevenWideFourTableauLayout,
                animationDurations = gameVM.animationDurations,
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
                layout = screenLayouts.sevenWideLayout,
                animationDurations = gameVM.animationDurations,
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
                layout = screenLayouts.tenWideLayout,
                animationDurations = gameVM.animationDurations,
                animateInfo = animateInfo,
                updateAnimateInfo = gameVM::updateAnimateInfo,
                spiderAnimateInfo = spiderAnimateInfo,
                updateSpiderAnimateInfo = gameVM::updateSpiderAnimateInfo,
                updateIsUndoEnabled = gameVM::updateIsUndoEnabled,
                isUndoAnimation = undoAnimation,
                updateIsUndoAnimation = gameVM::updateIsUndoAnimation,
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
            FortyAndEightBoard(
                modifier = modifier,
                layout = screenLayouts.tenWideEightTableauLayout,
                animationDurations = gameVM.animationDurations,
                animateInfo = animateInfo,
                updateAnimateInfo = gameVM::updateAnimateInfo,
                updateIsUndoEnabled = gameVM::updateIsUndoEnabled,
                isUndoAnimation = undoAnimation,
                updateIsUndoAnimation = gameVM::updateIsUndoAnimation,
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
                layout = screenLayouts.sevenWideLayout,
                animationDurations = gameVM.animationDurations,
                animateInfo = animateInfo,
                updateAnimateInfo = gameVM::updateAnimateInfo,
                updateIsUndoEnabled = gameVM::updateIsUndoEnabled,
                isUndoAnimation = undoAnimation,
                updateIsUndoAnimation = gameVM::updateIsUndoAnimation,
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