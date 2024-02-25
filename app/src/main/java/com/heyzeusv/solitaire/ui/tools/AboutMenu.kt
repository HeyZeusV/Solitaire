package com.heyzeusv.solitaire.ui.tools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.theme.Pink80

/**
 *  Composable that displays About Menu Screen where users can see info about app.
 */
@Composable
fun AboutMenu(
    updateMenuState: (MenuState) -> Unit
) {
    BackHandler { updateMenuState(MenuState.BUTTONS) }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .testTag("About Menu"),
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.menu_header_credits),
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.headlineMedium
            )
            LinkifyText(
                text = stringResource(R.string.menu_content_credits),
                linkColor = Pink80
            )
        }
    }
}

@Preview
@Composable
fun AboutMenuPreview() {
    SolitairePreview {
        AboutMenu(updateMenuState = { })
    }
}