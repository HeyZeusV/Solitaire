package com.heyzeusv.solitaire.ui

/**
 *  Found here [https://al-e-shevelev.medium.com/how-to-prevent-multiple-clicks-in-android-jetpack-compose-8e62224c9c5e].
 *  Was having crashes that were very random and impossible to reproduce.
 *  kotlinx.coroutines.CompletionHandlerException: Exception in resume onCancellation handler for
 *  CancellableContinuation... Caused by: java.lang.IllegalStateException: This mutex is not locked...
 *  Others were having issues on Multiplatform Git Repo, but no solution. I just tried this one on a
 *  whim, which is suppose to prevent multiple clicks, and so far so good!
 */
internal interface MultipleEventsCutter {
    fun processEvent(event: () -> Unit)

    companion object
}

internal fun MultipleEventsCutter.Companion.get(): MultipleEventsCutter =
    MultipleEventsCutterImpl()

private class MultipleEventsCutterImpl : MultipleEventsCutter {
    private val now: Long
        get() = System.currentTimeMillis()

    private var lastEventTimeMs: Long = 0

    override fun processEvent(event: () -> Unit) {
        if (now - lastEventTimeMs >= 300L) {
            event.invoke()
        }
        lastEventTimeMs = now
    }
}