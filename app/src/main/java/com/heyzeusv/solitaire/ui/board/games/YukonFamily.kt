package com.heyzeusv.solitaire.ui.board.games

import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.pile.Stock
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.Redeals

/**
 *  Games that belong to Yukon family.
 */
sealed class YukonFamily : Games() {
    override val familyId: Int = R.string.games_family_yukon

    override val drawAmount: DrawAmount = DrawAmount.Zero
    override val redeals: Redeals = Redeals.None

    override fun resetTableau(tableauList: List<Tableau>, stock: Stock) {
        tableauList.forEachIndexed { index, tableau ->
            if (index != 0) {
                val cards = List(index + 5) { stock.remove() }
                tableau.reset(cards)
            } else {
                tableau.reset(listOf(stock.remove()))
            }
        }
    }
}

class Yukon : YukonFamily() {
    override val nameId: Int = R.string.games_yukon
    override val previewId: Int = R.drawable.preview_yukon

    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean {
        tableauList.forEach { if (it.faceDownExists() || it.notInOrder()) return false }
        return true
    }
}

class Alaska : YukonFamily() {
    override val nameId: Int = R.string.games_alaska
    override val previewId: Int = R.drawable.preview_yukon

    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean {
        tableauList.forEach {
            if (it.faceDownExists() || it.isMultiSuit() || it.notInOrder()) return false
        }
        return true
    }
}

class Russian : YukonFamily() {
    override val nameId: Int = R.string.games_russian
    override val previewId: Int = R.drawable.preview_yukon

    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean {
        tableauList.forEach {
            if (it.faceDownExists() || it.isMultiSuit() || it.notInOrder()) return false
        }
        return true
    }
}

class AustralianPatience : YukonFamily() {
    override val nameId: Int = R.string.games_australian_patience
    override val previewId: Int = R.drawable.preview_australian_patience

    override val drawAmount: DrawAmount = DrawAmount.One

    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean {
        tableauList.forEach { if (it.isMultiSuit() || it.notInOrder()) return false }
        return true
    }

    override fun resetTableau(tableauList: List<Tableau>, stock: Stock) {
        tableauList.forEach { tableau ->
            val cards = List(4) { stock.remove() }
            tableau.reset(cards)
        }
    }
}

class Canberra : YukonFamily() {
    override val nameId: Int = R.string.games_canberra
    override val previewId: Int = R.drawable.preview_australian_patience

    override val drawAmount: DrawAmount = DrawAmount.One
    override val redeals: Redeals = Redeals.Once

    override fun autocompleteTableauCheck(tableauList: List<Tableau>): Boolean {
        tableauList.forEach { if (it.isMultiSuit() || it.notInOrder()) return false }
        return true
    }

    override fun resetTableau(tableauList: List<Tableau>, stock: Stock) {
        tableauList.forEach { tableau ->
            val cards = List(4) { stock.remove() }
            tableau.reset(cards)
        }
    }
}