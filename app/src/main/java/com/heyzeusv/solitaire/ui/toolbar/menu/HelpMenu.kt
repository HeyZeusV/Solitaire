package com.heyzeusv.solitaire.ui.toolbar.menu

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
        Column {
            Text(
                text = "Stock",
                color = StockHelp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = "Waste",
                color = WasteHelp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = "Foundation",
                color = FoundationHelp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Tableau",
                color = TableauHelp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}