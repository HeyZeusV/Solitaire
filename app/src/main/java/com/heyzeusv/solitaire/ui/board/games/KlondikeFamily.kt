package com.heyzeusv.solitaire.ui.board.games

import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.MaxScore
import com.heyzeusv.solitaire.util.Redeals

/**
 *  Games that belong to Klondike family.
 */
sealed class KlondikeFamily : Games() {
    override val familyId: Int = R.string.games_family_klondike

    override val maxScore: MaxScore = MaxScore.ONE_DECK
}

class KlondikeTurnOne : KlondikeFamily() {
    override val nameId: Int = R.string.games_klondike_turn_one
    override val previewId: Int = R.drawable.preview_klondike_turn_one

    override val drawAmount: DrawAmount = DrawAmount.One
    override val redeals: Redeals = Redeals.Unlimited
}

class KlondikeTurnThree : KlondikeFamily() {
    override val nameId: Int = R.string.games_klondike_turn_three
    override val previewId: Int = R.drawable.preview_klondike_turn_three

    override val drawAmount: DrawAmount = DrawAmount.Three
    override val redeals: Redeals = Redeals.Unlimited
}

class ClassicWestcliff : KlondikeFamily() {
    override val nameId: Int = R.string.games_classic_westcliff
    override val previewId: Int = R.drawable.preview_classic_westcliff

    override val drawAmount: DrawAmount = DrawAmount.One
    override val redeals: Redeals = Redeals.None
    override val maxScore: MaxScore = MaxScore.ONE_DECK_NO_ACES
}

class Easthaven : KlondikeFamily() {
    override val nameId: Int = R.string.games_easthaven
    override val previewId: Int = R.drawable.preview_easthaven

    override val drawAmount: DrawAmount = DrawAmount.Seven
    override val redeals: Redeals = Redeals.None
}