package com.heyzeusv.solitaire.util

import androidx.compose.animation.core.AnimationSpec
import com.heyzeusv.solitaire.data.pile.Tableau
import kotlinx.coroutines.delay

/**
 *  Enum class containing durations of Card animations. [fullAniSpec] is the full duration of most
 *   animations available and is used by [AnimationSpec]. [fullDelay] is [fullAniSpec] converted to
 *   [Long] in order to be used by [delay]. [beforeActionDelay] and [afterActionDelay] are used by
 *   [delay] in order to prevent Cards from flashing in and out before and after animations.
 *   [tableauCardFlipAniSpec] is the duration of the animation that occurs when a [Tableau] Card is
 *   flipped with [tableauCardFlipDelayAniSpec] being the delay before it occurs. Both are used by
 *   [AnimationSpec]
 */
enum class AnimationDurations(
    val fullAniSpec: Int,
    val beforeActionDelay: Long,
    val afterActionDelay: Long,
    val tableauCardFlipAniSpec: Int,
    val tableauCardFlipDelayAniSpec: Int
) {
    TwoHundredFifty(
        fullAniSpec = 250,
        beforeActionDelay = 15,
        afterActionDelay = 240,
        tableauCardFlipAniSpec = 200,
        tableauCardFlipDelayAniSpec = 50
    );

    val fullDelay: Long = fullAniSpec.toLong()
    val noAnimation: Int = 0
}