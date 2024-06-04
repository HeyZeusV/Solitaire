package com.heyzeusv.solitaire.board.animation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.heyzeusv.solitaire.AnimationDurationsSetting
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.menu.settings.SettingsMenu

/**
 *  Users have 5 options to select from that determine how long an animation lasts on a valid move.
 *  None(0ms), Fastest(100ms), Fast(250ms), Slow(500ms), Slowest(1000ms)
 *
 *  @property duration How long the animation lasts from start to finish.
 *  @property beforeActionDelay Delay length before running [AnimateInfo.actionBeforeAnimation].
 *  @property afterActionDelay Delay length before running [AnimateInfo.actionAfterAnimation].
 *  @property tableauCardFlipDuration How long the animation lasts for [TableauCardFlipInfo].
 *  @property tableauCardFlipDelay Delay length before flipping [Tableau] [Card].
 *  @property autocompleteDelay Delay length before attempting to run the next autocomplete step.
 *  @property iconId Icon resource id that is display on [SettingsMenu] to represent entry.
 *  @property displayId String resource id that is displayed on [SettingsMenu] to represent
 *  selected entry.
 *  @property setting Is the enum value that entry is represented by in Proto DataStore.
 */
enum class AnimationDurations(
    val duration: Int,
    val beforeActionDelay: Long = 15,
    val afterActionDelay: Long,
    val tableauCardFlipDuration: Int,
    val tableauCardFlipDelay: Int = 50,
    val autocompleteDelay: Long = (duration + 50).toLong(),
    @DrawableRes val iconId: Int,
    @StringRes val displayId: Int,
    val setting: AnimationDurationsSetting
) {
    None(
        duration = 0,
        beforeActionDelay = 0,
        afterActionDelay = 0,
        tableauCardFlipDuration = 0,
        tableauCardFlipDelay = 0,
        iconId = R.drawable.button_animation_none,
        displayId = R.string.animation_duration_none,
        setting = AnimationDurationsSetting.NONE
    ),
    Slowest(
        duration = 1000,
        afterActionDelay = 950,
        tableauCardFlipDuration = 950,
        iconId = R.drawable.button_animation_slowest,
        displayId = R.string.animation_duration_slowest,
        setting = AnimationDurationsSetting.SLOWEST
    ),
    Slow(
        duration = 500,
        afterActionDelay = 450,
        tableauCardFlipDuration = 450,
        iconId = R.drawable.button_animation_slow,
        displayId = R.string.animation_duration_slow,
        setting = AnimationDurationsSetting.SLOW
    ),
    Fast(
        duration = 250,
        afterActionDelay = 200,
        tableauCardFlipDuration = 200,
        iconId = R.drawable.button_animation_fast,
        displayId = R.string.animation_duration_fast,
        setting = AnimationDurationsSetting.FAST
    ),
    Fastest(
        duration = 100,
        afterActionDelay = 50,
        tableauCardFlipDuration = 50,
        iconId = R.drawable.button_animation_fastest,
        displayId = R.string.animation_duration_fastest,
        setting = AnimationDurationsSetting.FASTEST
    );

    val fullDelay: Long = duration.toLong()
    val noAnimation: Int = 0

    companion object {
        /**
         *  Returns [AnimationDurations] that corresponds to [AnimationDurationsSetting].
         *
         *  @param setting Value from Proto DataStore [AnimationDurationsSetting] enum class.
         */
        infix fun from(setting: AnimationDurationsSetting): AnimationDurations =
            entries.firstOrNull { it.setting == setting } ?: None
    }
}