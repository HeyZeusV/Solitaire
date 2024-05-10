package com.heyzeusv.solitaire.menu.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    val uiState by menuVM.uiState.collectAsState()
    val selectedAnimationDurations = AnimationDurations from settings.animationDurations

    SettingsMenu(
        isConnected = menuVM::isConnected,
        uiState = uiState,
        updateUsername = menuVM::updateUsername,
        updateEmail = menuVM::updateEmail,
        updatePassword = menuVM::updatePassword,
        logInOnClick = menuVM::onLogInClick,
        selectedAnimationDurations = selectedAnimationDurations,
        updateAnimationDurations = menuVM::updateAnimationDurations
    ) {
        menuVM.updateDisplayMenuButtonsAndMenuState(MenuState.ButtonsFromScreen)
    }
}

/**
 *  Composable that displays Menu which allows user to change various Settings.
 *
 *  ~~ Account ~~
 *  [isConnected] determines if [AccountSetting] should be fully shown.
 *
 *  ~~ Animation Speed ~~
 *  [selectedAnimationDurations] and [updateAnimationDurations] are used to display/update
 *  Animation Duration Setting.
 *
 *  [onBackPress] is launched when user tries to close [SettingsMenu] using either top left arrow
 *  icon or back button on phone.
 */
@Composable
fun SettingsMenu(
    isConnected: () -> Boolean = { false },
    uiState: AccountUiState,
    updateUsername: (String) -> Unit = { },
    updateEmail: (String) -> Unit = { },
    updatePassword: (String) -> Unit = { },
    logInOnClick: () -> Unit = { },
    selectedAnimationDurations: AnimationDurations,
    updateAnimationDurations: (AnimationDurations) -> Unit = { },
    onBackPress: () -> Unit = { }
) {
    MenuScreen(
        menu = MenuState.Settings,
        modifier = Modifier.testTag("Settings Menu"),
        onBackPress = onBackPress
    ) {
        Setting(title = R.string.settings_account) {
            AccountSetting(
                isConnected = isConnected,
                uiState = uiState,
                updateUsername = updateUsername,
                updateEmail = updateEmail,
                updatePassword = updatePassword,
                logInOnClick = logInOnClick,
            )
        }
        Setting(title = R.string.settings_animation_duration) {
            AnimationDurationSetting(selectedAnimationDurations) { updateAnimationDurations(it) }
        }
    }
}

/**
 *  Allows user to either create a new account or sign into an existing one. [isConnected] is used
 *  to determine if connection error should be displayed.
 */
@Composable
fun AccountSetting(
    isConnected: () -> Boolean = { false },
    uiState: AccountUiState,
    updateUsername: (String) -> Unit = { },
    updateEmail: (String) -> Unit = { },
    updatePassword: (String) -> Unit = { },
    logInOnClick: () -> Unit = { },
    ) {
    var connected by remember { mutableStateOf(isConnected()) }
    var selectedTab by remember { mutableIntStateOf(0) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(272.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        if (connected) {
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
                    modifier = Modifier
                        .height(212.dp)
                        .padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (selectedTab == AccountTabs.SignUp.ordinal) {
                        AccountTextField(
                            value = uiState.username,
                            onValueChange = { updateUsername(it) },
                            placeholder = R.string.account_username
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = stringResource(id = R.string.account_username)
                            )
                        }
                    }
                    AccountTextField(
                        value = uiState.email,
                        onValueChange = { updateEmail(it) },
                        placeholder = R.string.account_email
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = stringResource(id = R.string.account_username)
                        )
                    }
                    PasswordTextField(
                        value = uiState.password,
                        onValueChange = { updatePassword(it) },
                        placeholder = R.string.account_password
                    )
                    if (selectedTab == AccountTabs.SignUp.ordinal) {
                        Button(
                            onClick = { /*TODO*/ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(text = stringResource(R.string.account_sign_up))
                        }
                    } else {
                        Button(
                            onClick = { logInOnClick() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(text = stringResource(R.string.account_log_in))
                        }
                        FilledTonalButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(text = stringResource(R.string.account_forgot_password))
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = stringResource(R.string.internet_error),
                    modifier = Modifier
                        .weight(.4f)
                        .fillMaxSize(),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = stringResource(R.string.internet_error),
                    modifier = Modifier.weight(.1f),
                    color = MaterialTheme.colorScheme.error
                )
                Button(
                    onClick = { connected = isConnected() },
                    modifier = Modifier.weight(.12f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text(text = stringResource(R.string.internet_error_button))
                }
            }
        }
    }
}

@Composable
fun AccountTextField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes placeholder: Int,
    leadingIcon: @Composable (() -> Unit)
) {
    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(placeholder))},
        leadingIcon = { leadingIcon() },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes placeholder: Int,
) {
    var isVisible by remember { mutableStateOf(false) }

    val icon = if (isVisible) painterResource(R.drawable.password_visibility_on)
    else painterResource(R.drawable.password_visibility_off)

    val visualTransformation =
        if (isVisible) VisualTransformation.None else PasswordVisualTransformation()

    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = stringResource(placeholder)) },
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock") },
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(
                    painter = icon,
                    contentDescription = stringResource(R.string.account_password_visibility)
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = visualTransformation,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
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
    updateAnimationDurations: (AnimationDurations) -> Unit = { }
) {
    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.sColumnSpacedBy))) {
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
    }
}

/**
 *  Template for any Setting. Provides [Text] to displays [title] at the top, [content] in the
 *  middle, and a [HorizontalDivider] at the bottom.
 */
@Composable
fun Setting(
    @StringRes title: Int,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.sColumnSpacedBy))) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.headlineSmall.copy(
                textDecoration = TextDecoration.Underline
            )
        )
        content()
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
                isConnected = { true },
                uiState = AccountUiState(),
                selectedAnimationDurations = AnimationDurations.None
            )
        }
    }
}

@Preview
@Composable
fun AccountSettingPreview() {
    PreviewUtil().apply {
        Preview {
            AccountSetting(
                isConnected = { true },
                uiState = AccountUiState()
            )
        }
    }
}

@Preview
@Composable
fun AccountSettingErrorPreview() {
    PreviewUtil().apply {
        Preview {
            AccountSetting(
                isConnected = { false },
                uiState = AccountUiState()
            )
        }
    }
}

@Preview
@Composable
fun AnimationDurationSettingPreview() {
    PreviewUtil().apply {
        Preview {
            AnimationDurationSetting(selectedAnimationDurations = AnimationDurations.Fastest)
        }
    }
}

