package com.heyzeusv.solitaire.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.AnimationSpec
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.ui.toolbar.menu.SettingsMenu
import kotlinx.coroutines.delay

/**
 *   Enum class containing durations of Card animations. [fullAniSpec] is the full duration of most
 *   animations available and is used by [AnimationSpec]. [fullDelay] is [fullAniSpec] converted to
 *   [Long] in order to be used by [delay]. [beforeActionDelay] and [afterActionDelay] are used by
 *   [delay] in order to prevent Cards from flashing in and out before and after animations.
 *   [tableauCardFlipAniSpec] is the duration of the animation that occurs when a [Tableau] Card is
 *   flipped with [tableauCardFlipDelayAniSpec] being the delay before it occurs. Both are used by
 *   [AnimationSpec]. [autoCompleteDelay] is delay used during autocomplete between each valid move.
 *   [iconId] and [settingDisplayId] is used in [SettingsMenu] to represent entries.
 */
enum class AnimationDurations(
    val fullAniSpec: Int,
    val beforeActionDelay: Long,
    val afterActionDelay: Long,
    val tableauCardFlipAniSpec: Int,
    val tableauCardFlipDelayAniSpec: Int,
    val autoCompleteDelay: Long,
    @DrawableRes val iconId: Int,
    @StringRes val settingDisplayId: Int
) {
    None(
        fullAniSpec = 0,
        beforeActionDelay = 0,
        afterActionDelay = 0,
        tableauCardFlipAniSpec = 0,
        tableauCardFlipDelayAniSpec = 0,
        autoCompleteDelay = 0,
        iconId = R.drawable.button_animation_none,
        settingDisplayId = R.string.animation_duration_none
    ),
    Slowest(
        fullAniSpec = 1000,
        beforeActionDelay = 15,
        afterActionDelay = 990,
        tableauCardFlipAniSpec = 950,
        tableauCardFlipDelayAniSpec = 50,
        autoCompleteDelay = 1050,
        iconId = R.drawable.button_animation_slowest,
        settingDisplayId = R.string.animation_duration_slowest
    ),
    Slow(
        fullAniSpec = 500,
        beforeActionDelay = 15,
        afterActionDelay = 490,
        tableauCardFlipAniSpec = 450,
        tableauCardFlipDelayAniSpec = 50,
        autoCompleteDelay = 550,
        iconId = R.drawable.button_animation_slow,
        settingDisplayId = R.string.animation_duration_slow
    ),
    Fast(
        fullAniSpec = 250,
        beforeActionDelay = 15,
        afterActionDelay = 240,
        tableauCardFlipAniSpec = 200,
        tableauCardFlipDelayAniSpec = 50,
        autoCompleteDelay = 300,
        iconId = R.drawable.button_animation_fast,
        settingDisplayId = R.string.animation_duration_fast
    ),
    Fastest(
        fullAniSpec = 100,
        beforeActionDelay = 15,
        afterActionDelay = 90,
        tableauCardFlipAniSpec = 50,
        tableauCardFlipDelayAniSpec = 50,
        autoCompleteDelay = 150,
        iconId = R.drawable.button_animation_fastest,
        settingDisplayId = R.string.animation_duration_fastest
    );

    val fullDelay: Long = fullAniSpec.toLong()
    val noAnimation: Int = 0
}