package com.heyzeusv.solitaire.ui.board

import androidx.annotation.StringRes
import com.heyzeusv.solitaire.R


/**
 *  TODO Use this method
 */
sealed class Outclass() {
    sealed class Family1() : Outclass() {
        class Member1() : Family1() {
            override val test: Int
                get() = R.string.games_alaska
        }

        class Member2() : Family1() {
            override val test: Int
                get() = R.string.games_alaska
        }

        override val family: Int
            get() = R.string.games_family_yukon
    }
    sealed class Family2() : Outclass() {
        class Member3() : Family2()
        class Member4() : Family2()
        override val test: Int
            get() = R.string.games_alaska
        override val family: Int
            get() = R.string.games_family_yukon
    }

    @get:StringRes
    abstract val test: Int
    @get:StringRes
    abstract val family: Int

    val test4 = this::class.sealedSubclasses
    val t3 = test4.flatMap { it.sealedSubclasses }
}