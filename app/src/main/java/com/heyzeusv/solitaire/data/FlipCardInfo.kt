package com.heyzeusv.solitaire.data

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
    val startRotationY: Float = 0f
    open val endRotationY: Float = 0f
    open fun flipCondition(flipRotation: Float): Boolean = false

    fun getUndoFlipCardInfo(): FlipCardInfo = when (this) {
        is FaceDown.SinglePile -> FaceUp.SinglePile
        is FaceDown.MultiPile -> FaceUp.MultiPile
        is FaceUp.SinglePile -> FaceDown.SinglePile
        is FaceUp.MultiPile -> FaceDown.MultiPile
        NoFlip -> NoFlip
    }
}