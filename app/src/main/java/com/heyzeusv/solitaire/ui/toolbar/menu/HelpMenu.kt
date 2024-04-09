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
import com.heyzeusv.solitaire.util.theme.FoundationHelp
import com.heyzeusv.solitaire.util.theme.StockHelp
import com.heyzeusv.solitaire.util.theme.TableauHelp
import com.heyzeusv.solitaire.util.theme.WasteHelp

/**
 *  Composable that displays Help Menu Screen where users can get info of currently [selectedGame].
 *  [onBackPress] navigates user away from [HelpMenu].
 */
@Composable
fun HelpMenu(
    selectedGame: Games,
    onBackPress: () -> Unit
) {
    MenuScreen(
        menu = MenuState.Help,
        modifier = Modifier.testTag("Help Menu"),
        onBackPress = onBackPress
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.hsColumnSpacedBy))
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(dimensionResource(R.dimen.hsColumnSpacedBy)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(selectedGame.nameId),
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Image(
                        painter = painterResource(selectedGame.helpId),
                        contentDescription = stringResource(selectedGame.nameId),
                        modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
            Text(
                text = "Stock",
                color = StockHelp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Waste",
                color = WasteHelp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Foundation",
                color = FoundationHelp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Tableau",
                color = TableauHelp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}