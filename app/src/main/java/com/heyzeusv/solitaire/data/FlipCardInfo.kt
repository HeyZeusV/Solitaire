package com.heyzeusv.solitaire.data

sealed class FlipCardInfo {
    class FaceDown : FlipCardInfo() {
        override val endRotationY = 180f
        override fun flipCondition(flipRotation: Float): Boolean = flipRotation < 90f
    }
    class FaceUp : FlipCardInfo() {
        override val endRotationY = -180f
        override fun flipCondition(flipRotation: Float): Boolean = flipRotation > -90f
    }
    data object NoFlip : FlipCardInfo()
    val startRotationY: Float = 0f
    open val endRotationY: Float = 0f
    open fun flipCondition(flipRotation: Float): Boolean = false
}