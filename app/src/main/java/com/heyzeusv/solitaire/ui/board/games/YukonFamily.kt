package com.heyzeusv.solitaire.ui.board.games

import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.MaxScore
import com.heyzeusv.solitaire.util.Redeals

/**
 *  Games that belong to Yukon family.
 */
sealed class YukonFamily : Games() {
    override val familyId: Int = R.string.games_family_yukon

    override val drawAmount: DrawAmount = DrawAmount.Zero
    override val redeals: Redeals = Redeals.None
    override val maxScore: MaxScore = MaxScore.ONE_DECK
}

class Yukon : YukonFamily() {
    override val nameId: Int = R.string.games_yukon
    override val previewId: Int = R.drawable.preview_yukon
}

class Alaska : YukonFamily() {
    override val nameId: Int = R.string.games_alaska
    override val previewId: Int = R.drawable.preview_yukon
}

class Russian : YukonFamily() {
    override val nameId: Int = R.string.games_russian
    override val previewId: Int = R.drawable.preview_yukon
}

class AustralianPatience : YukonFamily() {
    override val nameId: Int = R.string.games_australian_patience
    override val previewId: Int = R.drawable.preview_australian_patience

    override val drawAmount: DrawAmount = DrawAmount.One
}

class Canberra : YukonFamily() {
    override val nameId: Int = R.string.games_canberra
    override val previewId: Int = R.drawable.preview_australian_patience

    override val drawAmount: DrawAmount = DrawAmount.One
    override val redeals: Redeals = Redeals.Once
}