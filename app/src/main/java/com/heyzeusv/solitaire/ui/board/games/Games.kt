package com.heyzeusv.solitaire.ui.board.games

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.heyzeusv.solitaire.data.pile.Stock
import com.heyzeusv.solitaire.data.pile.Foundation
import com.heyzeusv.solitaire.data.pile.Waste
import com.heyzeusv.solitaire.ui.toolbar.menu.GamesMenu
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.MaxScore
import com.heyzeusv.solitaire.util.Redeals

/**
 *  Subclasses of this will not be games exactly, but instead the families the game belongs to in
 *  order to have an extra layer of organization.
 */
sealed class Games : GameInfo, GameRules {
    private val familySubclasses = Games::class.sealedSubclasses
    val gameSubclasses = familySubclasses.flatMap { it.sealedSubclasses }
}

/**
 *  Info that each game requires. Each game has its own unique [nameId] and belongs to a family
 *  which is retrieved using [familyId]. Each game on [GamesMenu] has a small preview image,
 *  [previewId], to give users an idea of the type of game it is.
 */
interface GameInfo {
    @get:StringRes val nameId: Int
    @get:StringRes val familyId: Int
    @get:DrawableRes val previewId: Int
}

/**
 *  Rules that are shared between each game, but with different values. [drawAmount] is the amount
 *  of Cards drawn per click of [Stock]. [redeals] is the number of times [Stock] can be refilled
 *  from [Waste]. [maxScore] refers to the max score users can get from just placing Cards in
 *  [Foundation].
 */
interface GameRules {
    val drawAmount: DrawAmount
    val redeals: Redeals
    val maxScore: MaxScore
}