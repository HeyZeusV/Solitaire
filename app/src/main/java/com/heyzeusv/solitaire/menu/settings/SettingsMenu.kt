package com.heyzeusv.solitaire.menu.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.menu.MenuScreen
import com.heyzeusv.solitaire.menu.MenuViewModel
import com.heyzeusv.solitaire.board.animation.AnimationDurations
import com.heyzeusv.solitaire.util.AccountTabs
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.theme.Purple40
import com.heyzeusv.solitaire.util.theme.Purple80

/**
 *  Composable that displays Menu which allows user to change various Settings.
 */
@Composable
fun SettingsMenu(menuVM: MenuViewModel) {
    val settings by menuVM.settings.collectAsState()
    val selectedAnimationDurations = AnimationDurations from settings.animationDurations

    SettingsMenu(
        selectedAnimationDurations = selectedAnimationDurations,
        updateAnimationDurations = menuVM::updateAnimationDurations
    ) {
        menuVM.updateDisplayMenuButtonsAndMenuState(MenuState.ButtonsFromScreen)
    }
}

/**
 *  Composable that displays Menu which allows user to change various Settings.
 *  [selectedAnimationDurations] and [updateAnimationDurations] are used to display/update
 *  Animation Duration Setting. [onBackPress] is launched when user tries to close [SettingsMenu]
 *  using either top left arrow icon or back button on phone.
 */
@Composable
fun SettingsMenu(
    selectedAnimationDurations: AnimationDurations,
    updateAnimationDurations: (AnimationDurations) -> Unit,
    onBackPress: () -> Unit
) {
    MenuScreen(
        menu = MenuState.Settings,
        modifier = Modifier.testTag("Settings Menu"),
        onBackPress = onBackPress
    ) {
        AccountSetting()
        AnimationDurationSetting(selectedAnimationDurations) { updateAnimationDurations(it) }
    }
}

@Composable
fun AccountSetting() {
    var selectedTab by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.sColumnSpacedBy))) {
        Text(
            text = stringResource(R.string.settings_account),
            style = MaterialTheme.typography.headlineSmall.copy(
                textDecoration = TextDecoration.Underline
            )
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                TabRow(selectedTabIndex = selectedTab) {
                    AccountTabs.entries.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text(stringResource(tab.nameId))
                        }
                    }
                }
                Column(
                    modifier = Modifier.height(168.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (selectedTab == AccountTabs.Create.ordinal) {
                        TextField(
                            value = "",
                            onValueChange = { },
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .fillMaxWidth(),
                            placeholder = { Text(stringResource(R.string.account_username)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = stringResource(id = R.string.account_username)
                                )
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }
                    TextField(
                        value = "",
                        onValueChange = { },
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.account_email)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = stringResource(id = R.string.account_username)
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    TextField(
                        value = "",
                        onValueChange = { },
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.account_password)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = stringResource(id = R.string.account_username)
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 *  Composable that display Animation Duration Setting. [selectedAnimationDurations] is the
 *  currently selected [AnimationDurations], while [updateAnimationDurations] is used to update
 *  [AnimationDurations].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationDurationSetting(
    selectedAnimationDurations: AnimationDurations,
    updateAnimationDurations: (AnimationDurations) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.sColumnSpacedBy))) {
        Text(
            text = stringResource(R.string.settings_animation_duration),
            style = MaterialTheme.typography.headlineSmall.copy(
                textDecoration = TextDecoration.Underline
            )
        )
        SingleChoiceSegmentedButtonRow {
            AnimationDurations.entries.forEachIndexed { index, ad ->
                SegmentedButton(
                    selected = selectedAnimationDurations == ad,
                    onClick = { updateAnimationDurations(ad) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = AnimationDurations.entries.size
                    ),
                    colors = SegmentedButtonDefaults.colors().copy(
                        activeContainerColor = Purple40,
                        activeContentColor = Color.Black,
                        activeBorderColor = Purple80
                    ),
                    icon = { }
                ) {
                    Icon(
                        painter = painterResource(ad.iconId),
                        contentDescription = ad.name
                    )
                }
            }
        }
        Text(
            text = stringResource(selectedAnimationDurations.settingDisplayId),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = TextUnit(
                    20f,
                    TextUnitType.Sp
                )
            )
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
fun SettingsMenuPreview() {
    PreviewUtil().apply {
        Preview {
            SettingsMenu(
                selectedAnimationDurations = AnimationDurations.None,
                updateAnimationDurations = { }
            ) { }
        }
    }
}

@Preview
@Composable
fun AnimationDurationSettingPreview() {
    PreviewUtil().apply {
        Preview {
            AnimationDurationSetting(
                selectedAnimationDurations = AnimationDurations.Fastest
            ) { }
        }
    }
}