package com.heyzeusv.solitaire.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.games.Games
import com.heyzeusv.solitaire.games.KlondikeTurnOne
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.PileInfo
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.spacedBy
import com.heyzeusv.solitaire.util.theme.pileTitle

/**
 *  Displays rules for each pile of currently selected game.
 *
 *  @param selectedGame The currently selected game.
 *  @param onBackPress Action to take when back arrow or button is pressed. (Navigate away)
 */
@Composable
fun RulesMenu(
    selectedGame: Games,
    onBackPress: () -> Unit,
) {
    MenuScreen(
        menu = MenuState.Rules,
        modifier = Modifier.testTag("Rules Menu"),
        onBackPress = onBackPress,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement spacedBy R.dimen.rColumnSpacedBy,
        ) {
            GameTitleAndImage(selectedGame = selectedGame)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement spacedBy R.dimen.rColumnSpacedBy,
            ) {
                selectedGame.gamePileRules.pileRulesIds.forEachIndexed { index, id ->
                    id?.let {
                        GamePileInfo(
                            pileInfo = PileInfo.entries[index],
                            pileRules = stringResource(id),
                        )
                    }
                }
            }
        }
    }
}

/**
 *  Displays selected game's name and a helper image that differentiates between piles
 *
 *  @param selectedGame The currently selected game.
 */
@Composable
fun GameTitleAndImage(selectedGame: Games) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement spacedBy R.dimen.rColumnSpacedBy,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(selectedGame.nameId),
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.headlineLarge,
            )
            Image(
                painter = painterResource(selectedGame.gamePileRules.rulesId),
                contentDescription = stringResource(selectedGame.nameId),
                modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                contentScale = ContentScale.FillWidth,
            )
        }
    }
}

/**
 *  Displays the name of the pile in a unique color and the pile specific rules.
 *
 *  @param pileInfo The pile name res id and the color it should be displayed in.
 *  @param pileRules The pile rules.
 */
@Composable
fun GamePileInfo(
    pileInfo: PileInfo,
    pileRules: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement spacedBy R.dimen.rInfoSpacedBy,
    ) {
        Text(
            text = stringResource(pileInfo.nameId),
            style = pileTitle(color = pileInfo.color),
        )
        Text(text = pileRules)
    }
}

@Preview
@Composable
fun RulesMenuPreview() {
    PreviewUtil().apply {
        Preview {
            RulesMenu(selectedGame = KlondikeTurnOne) { }
        }
    }
}

@Preview
@Composable
fun GameTitleAndImagePreview() {
    PreviewUtil().apply {
        Preview {
            GameTitleAndImage(selectedGame = KlondikeTurnOne)
        }
    }
}

@Preview
@Composable
fun GamePileInfoPreview() {
    PreviewUtil().apply {
        Preview {
            Column {
                GamePileInfo(pileInfo = PileInfo.Stock, pileRules = "Testing")
                GamePileInfo(pileInfo = PileInfo.Waste, pileRules = "Testing")
                GamePileInfo(pileInfo = PileInfo.Foundation, pileRules = "Testing")
                GamePileInfo(pileInfo = PileInfo.Tableau, pileRules = "Testing")
            }
        }
    }
}