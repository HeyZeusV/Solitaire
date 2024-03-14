package com.heyzeusv.solitaire.data

sealed class FlipCard {
    data class FaceUp(val flipRotation: Float = 0f) : FlipCard() {
        override val flipCondition = flipRotation > -90f
        override val startRotationY = 0f
        override val endRotationY = -180f
    }
    data class FaceDown(val flipRotation: Float = 0f) : FlipCard() {
        override val flipCondition = flipRotation < 90f
        override val startRotationY = 0f
        override val endRotationY = 180f
    }
    abstract val flipCondition: Boolean
    abstract val startRotationY: Float
    abstract val endRotationY: Float
}