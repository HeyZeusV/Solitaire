package com.heyzeusv.solitaire.util

import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.piles.Pile
import io.kotest.matchers.ints.shouldBeExactly
import kotlinx.coroutines.flow.StateFlow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

infix fun StateFlow<Any?>.valueShouldBe(expected: Any?) {
    this.value shouldBe expected
}

infix fun StateFlow<Any?>.valueShouldNotBe(expected: Any?) {
    this.value shouldNotBe expected
}

infix fun Pile.pilesShouldBe(expected: List<Card>) {
    this.truePile shouldBe expected
    this.displayPile shouldBe expected
}

infix fun Pile.pileSizesShouldBe(expected: Int) {
    this.truePile.size shouldBeExactly expected
    this.displayPile.size shouldBeExactly expected
}

infix fun Pile.pilesShouldNotBe(expected: List<Card>) {
    this.truePile shouldNotBe expected
    this.displayPile shouldNotBe expected
}