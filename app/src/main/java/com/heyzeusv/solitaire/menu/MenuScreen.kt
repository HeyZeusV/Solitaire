package com.heyzeusv.solitaire.menu

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.PreviewUtil

/**
 *  Composable that possible [MenuState] use as a base. Provided [menu] is used to fill top
 *  [MenuHeaderBar] title and [onBackPress] handles both ways to close [MenuScreen], arrow and phone
 *  back Button. [extraAction] is displayed opposite of [onBackPress] on [MenuHeaderBar]. [content]
 *  fills the rest of the screen.
 */
@Composable
fun MenuScreen(
    menu: MenuState,
    modifier: Modifier,
    onBackPress: () -> Unit,
    extraAction: @Composable (Modifier) -> Unit = { },
    content: @Composable () -> Unit
) {
    BackHandler { onBackPress() }
    Card(
        modifier = modifier.fillMaxSize(),
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = dimensionResource(R.dimen.msPaddingAll)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.msColumnSpacedBy))
        ) {
            MenuHeaderBar(menu, onBackPress) { modifier -> extraAction(modifier) }
            content()
        }
    }
}

/**
 *  Composable used by [MenuState] screens that display given [menu] name as a title and a back
 *  arrow button that runs [onBackPress] when clicked. [extraAction] is displayed opposite of
 *  [onBackPress] on [MenuHeaderBar].
 */
@Composable
fun MenuHeaderBar(
    menu: MenuState,
    onBackPress: () -> Unit = { },
    extraAction: @Composable (Modifier) -> Unit = { }
) {
    val menuName = stringResource(menu.nameId)
    Box(modifier = Modifier.fillMaxWidth()) {
        Icon(
            painter = painterResource(R.drawable.button_menu_back),
            contentDescription = stringResource(R.string.menu_cdesc_close, menuName),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(top = dimensionResource(R.dimen.mhbIconPaddingTop))
                .size(dimensionResource(R.dimen.mhbIconSize))
                .clickable { onBackPress() }
        )
        Text(
            text = menuName,
            modifier = Modifier.align(Alignment.Center),
            textDecoration = TextDecoration.Underline,
            style = MaterialTheme.typography.displayMedium
        )
        extraAction(Modifier.align(Alignment.CenterEnd))
    }
}

@Preview
@Composable
fun MenuScreenPreview() {
    PreviewUtil().apply {
        Preview {
            MenuScreen(
                menu = MenuState.Games,
                modifier = Modifier,
                onBackPress = { }
            ) { }
        }
    }
}

@Preview
@Composable
fun MenuHeaderBarPreview() {
    PreviewUtil().apply {
        Preview {
            Card {
                Column {
                    MenuHeaderBar(menu = MenuState.Games)
                    MenuHeaderBar(menu = MenuState.Stats)
                    MenuHeaderBar(menu = MenuState.About)
                }
            }
        }
    }
}