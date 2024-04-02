package com.heyzeusv.solitaire.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.AnimationSpec
import com.heyzeusv.solitaire.AnimationDurationsSetting
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
    val beforeActionDelay: Long = 15,
    val afterActionDelay: Long,
    val tableauCardFlipAniSpec: Int,
    val tableauCardFlipDelayAniSpec: Int = 50,
    @DrawableRes val iconId: Int,
    @StringRes val settingDisplayId: Int,
    val ads: AnimationDurationsSetting
) {
    None(
        fullAniSpec = 0,
        beforeActionDelay = 0,
        afterActionDelay = 0,
        tableauCardFlipAniSpec = 0,
        tableauCardFlipDelayAniSpec = 0,
        iconId = R.drawable.button_animation_none,
        settingDisplayId = R.string.animation_duration_none,
        ads = AnimationDurationsSetting.NONE
    ),
    Slowest(
        fullAniSpec = 1000,
        afterActionDelay = 990,
        tableauCardFlipAniSpec = 950,
        iconId = R.drawable.button_animation_slowest,
        settingDisplayId = R.string.animation_duration_slowest,
        ads = AnimationDurationsSetting.SLOWEST
    ),
    Slow(
        fullAniSpec = 500,
        afterActionDelay = 490,
        tableauCardFlipAniSpec = 450,
        iconId = R.drawable.button_animation_slow,
        settingDisplayId = R.string.animation_duration_slow,
        ads = AnimationDurationsSetting.SLOW
    ),
    Fast(
        fullAniSpec = 250,
        afterActionDelay = 240,
        tableauCardFlipAniSpec = 200,
        iconId = R.drawable.button_animation_fast,
        settingDisplayId = R.string.animation_duration_fast,
        ads = AnimationDurationsSetting.FAST
    ),
    Fastest(
        fullAniSpec = 100,
        afterActionDelay = 90,
        tableauCardFlipAniSpec = 50,
        iconId = R.drawable.button_animation_fastest,
        settingDisplayId = R.string.animation_duration_fastest,
        ads = AnimationDurationsSetting.FASTEST
    );

    val fullDelay: Long = fullAniSpec.toLong()
    val noAnimation: Int = 0
    val autoCompleteDelay: Long = (fullAniSpec + 50).toLong()

    companion object {
        /**
         *  Returns [AnimationDurations] that corresponds to given [ads].
         */
        infix fun from(ads: AnimationDurationsSetting): AnimationDurations =
            entries.firstOrNull { it.ads == ads } ?: None
    }
}