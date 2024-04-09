package com.heyzeusv.solitaire.ui.toolbar.menu

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.board.games.Games
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.PileInfo

/**
 *  Composable that displays Rules Menu Screen where users can get info of currently [selectedGame].
 *  [onBackPress] navigates user away from [RulesMenu].
 */
@Composable
fun RulesMenu(
    selectedGame: Games,
    onBackPress: () -> Unit
) {
    MenuScreen(
        menu = MenuState.Rules,
        modifier = Modifier.testTag("Rules Menu"),
        onBackPress = onBackPress
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.rColumnSpacedBy))
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(dimensionResource(R.dimen.rColumnSpacedBy)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(selectedGame.nameId),
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Image(
                        painter = painterResource(selectedGame.rulesId),
                        contentDescription = stringResource(selectedGame.nameId),
                        modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement =
                    Arrangement.spacedBy(dimensionResource(R.dimen.rColumnSpacedBy))
            ) {
                PileInfo(
                    pileName = PileInfo.Stock,
                    pileInfo = stringResource(R.string.klondike_turn_one_stock_rules)
                )
                PileInfo(
                    pileName = PileInfo.Waste,
                    pileInfo = stringResource(R.string.klondike_turn_one_waste_rules)
                )
                PileInfo(
                    pileName = PileInfo.Foundation,
                    pileInfo = stringResource(R.string.klondike_turn_one_foundation_rules)
                )
                PileInfo(
                    pileName = PileInfo.Tableau,
                    pileInfo = stringResource(R.string.klondike_turn_one_tableau_rules)
                )
            }
        }
    }
}

@Composable
fun PileInfo(
    pileName: PileInfo,
    pileInfo: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.rInfoSpacedBy))
    ) {
        Text(
            text = stringResource(pileName.nameId),
            color = pileName.color,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            style = MaterialTheme.typography.titleLarge
        )
        Text(text = pileInfo)
    }
}