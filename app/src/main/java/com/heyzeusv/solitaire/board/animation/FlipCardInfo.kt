package com.heyzeusv.solitaire.board.animation

import com.heyzeusv.solitaire.board.piles.CardLogic

/**
 *  Sealed classes containing animation values for Card flips.
 */
sealed class FlipCardInfo {
    sealed class FaceDown : FlipCardInfo() {
        override val endRotationY = 180f
        override fun flipCondition(rotationY: Float): Boolean = rotationY < 90f

        data object SinglePile : FaceDown()
        data object MultiPile : FaceDown()
    }
    sealed class FaceUp : FlipCardInfo() {
        override val endRotationY = -180f
        override fun flipCondition(rotationY: Float): Boolean = rotationY > -90f

        data object SinglePile : FaceUp()
        data object MultiPile : FaceUp()
    }
    data object NoFlip : FlipCardInfo()

    // all flip animations have same start rotation value
    val startRotationY: Float = 0f
    // different end rotation values, so replaced in inner sealed classes.
    open val endRotationY: Float = 0f

    /**
     *  Determines when [CardLogic] has flipped enough to show other side of [CardLogic].
     *
     *  @param rotationY Determines [Card's][CardLogic] current Y rotation value.
     */
    open fun flipCondition(rotationY: Float): Boolean = false

    /**
     *  Returns the opposite value of this, which is used by Undo.
     */
    fun getUndoFlipCardInfo(): FlipCardInfo = when (this) {
        is FaceDown.SinglePile -> FaceUp.SinglePile
        is FaceDown.MultiPile -> FaceUp.MultiPile
        is FaceUp.SinglePile -> FaceDown.SinglePile
        is FaceUp.MultiPile -> FaceDown.MultiPile
        NoFlip -> NoFlip
    }
}