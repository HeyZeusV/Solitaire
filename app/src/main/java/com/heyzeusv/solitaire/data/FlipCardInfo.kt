package com.heyzeusv.solitaire.data

/**
 *  Sealed class containing animation values for Card flips.
 */
sealed class FlipCardInfo {
    sealed class FaceDown : FlipCardInfo() {
        override val endRotationY = 180f
        override fun flipCondition(flipRotation: Float): Boolean = flipRotation < 90f

        data object SinglePile : FaceDown()
        data object MultiPile : FaceDown()
    }
    sealed class FaceUp : FlipCardInfo() {
        override val endRotationY = -180f
        override fun flipCondition(flipRotation: Float): Boolean = flipRotation > -90f

        data object SinglePile : FaceUp()
        data object MultiPile : FaceUp()
    }
    data object NoFlip : FlipCardInfo()

    // all flip animations have same start rotation value
    val startRotationY: Float = 0f
    // different end rotation values, so replaced in inner sealed classes.
    open val endRotationY: Float = 0f

    /**
     *  Replaced in inner sealed classes. Determines when [Card] has flipped enough to show other
     *  side of [Card] depending on [flipRotation] value.
     */
    open fun flipCondition(flipRotation: Float): Boolean = false

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