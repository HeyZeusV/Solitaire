package com.heyzeusv.solitaire.ui.toolbar.menu

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.solitaire.util.AnimationDurations
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.PreviewDevices
import com.heyzeusv.solitaire.util.PreviewUtil

@Composable
fun SettingsMenu(
    onBackPressed: () -> Unit
) {
    MenuScreen(
        menu = MenuState.Settings,
        modifier = Modifier.testTag("Settings Menu"),
        onBackPress = onBackPressed
    ) {
        AnimationDurationSetting()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationDurationSetting() {
    var selected by remember { mutableStateOf(AnimationDurations.Fastest) }
    SingleChoiceSegmentedButtonRow {
        AnimationDurations.entries.forEachIndexed { index, ad ->
            SegmentedButton(
                selected = selected == ad,
                onClick = { selected = ad },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = AnimationDurations.entries.size
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
}

@Preview
@Composable
fun SettingsMenuPreview() {
    PreviewUtil().apply {
        Preview {
            SettingsMenu { }
        }
    }
}

@PreviewDevices
@Composable
fun AnimationDurationSettingPreview() {
    PreviewUtil().apply {
        Preview {
            AnimationDurationSetting()
        }
    }
}