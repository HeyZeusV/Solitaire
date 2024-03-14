package com.heyzeusv.solitaire.data

sealed class FlipCard {
    class FaceDown : FlipCard() {
        override val endRotationY = 180f
        override fun flipCondition(flipRotation: Float): Boolean = flipRotation < 90f
    }
    class FaceUp : FlipCard() {
        override val endRotationY = -180f
        override fun flipCondition(flipRotation: Float): Boolean = flipRotation > -90f
    }
    data object NoFlip : FlipCard()
    open val startRotationY: Float = 0f
    open val endRotationY: Float = 0f
    open fun flipCondition(flipRotation: Float): Boolean = false
}