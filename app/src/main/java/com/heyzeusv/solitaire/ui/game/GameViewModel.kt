package com.heyzeusv.solitaire.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.solitaire.data.AnimateInfo
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.FlipCardInfo
import com.heyzeusv.solitaire.data.TableauCardFlipInfo
import com.heyzeusv.solitaire.data.LayoutInfo
import com.heyzeusv.solitaire.data.pile.Foundation
import com.heyzeusv.solitaire.data.PileHistory
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.pile.Pile
import com.heyzeusv.solitaire.data.pile.Stock
import com.heyzeusv.solitaire.data.pile.Waste
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.MoveResult
import com.heyzeusv.solitaire.util.MoveResult.*
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.Suits
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 *  Data manager for game.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
abstract class GameViewModel (
    private val ss: ShuffleSeed,
    val layoutInfo: LayoutInfo
) : ViewModel() {

    // holds all 52 playing Cards
    protected open var baseDeck = MutableList(52) { Card(it % 13, getSuit(it)) }
    private var shuffledDeck = emptyList<Card>()

    protected open val baseRedealAmount: Int = 1000
    protected open var redealLeft: Int = 1000

    protected val _stock = Stock()
    val stock: Stock get() = _stock

    private val _waste = Waste()
    val waste: Waste get() = _waste

    private val _stockWasteEmpty = MutableStateFlow(false)
    val stockWasteEmpty: StateFlow<Boolean> get() = _stockWasteEmpty

    protected val _foundation = Suits.entries.map { Foundation(it) }.toMutableList()
    val foundation: List<Foundation> get() = _foundation

    protected abstract val _tableau: MutableList<Tableau>
    val tableau: List<Tableau> get() = _tableau

    private val _historyList = mutableListOf<AnimateInfo>()
    val historyList: List<AnimateInfo> get() = _historyList
    private lateinit var currentStep: PileHistory

    private val _undoEnabled = MutableStateFlow(false)
    val undoEnabled: StateFlow<Boolean> get() = _undoEnabled
    fun updateUndoEnabled(newValue: Boolean) { _undoEnabled.value = newValue }

    private val _undoAnimation = MutableStateFlow(false)
    val undoAnimation: StateFlow<Boolean> get() = _undoAnimation
    fun updateUndoAnimation(newValue: Boolean) { _undoAnimation.value = newValue }

    private val _autoCompleteActive = MutableStateFlow(false)
    val autoCompleteActive: StateFlow<Boolean> get() = _autoCompleteActive

    private var _autoCompleteCorrection: Int = 0
    val autoCompleteCorrection: Int get() = _autoCompleteCorrection

    private val _gameWon = MutableStateFlow(false)
    val gameWon: StateFlow<Boolean> get() = _gameWon

    protected val _animateInfo = MutableStateFlow<AnimateInfo?>(null)
    val animateInfo: StateFlow<AnimateInfo?> get() = _animateInfo
    fun updateAnimateInfo(newValue: AnimateInfo?) { _animateInfo.value = newValue }

    /**
     *  Goes through all the card piles in the game and resets them for either the same game or a
     *  new game depending on [resetOption].
     */
    protected open fun reset(resetOption: ResetOptions) {
        redealLeft = baseRedealAmount
        when (resetOption) {
            ResetOptions.RESTART -> _stock.reset(shuffledDeck)
            ResetOptions.NEW -> {
                shuffledDeck = baseDeck.shuffled(ss.shuffleSeed)
                _stock.reset(shuffledDeck)
            }
        }
        // empty foundations
        _foundation.forEach { it.reset() }
        // clear the waste pile
        _waste.reset()
        _historyList.clear()
        _undoEnabled.value = false
        _gameWon.value = false
        _autoCompleteActive.value = false
        _animateInfo.value = null
        _autoCompleteCorrection = 0
        _stockWasteEmpty.value = true
    }

    /**
     *  Initial Tableau state might be different from game to game.
     */
    protected abstract fun resetTableau()

    /**
     *  Helper function to call both reset functions at the same time.
     */
    fun resetAll(resetOption: ResetOptions) {
        reset(resetOption)
        resetTableau()
        _stock.recordHistory()
    }

    /**
     *  Runs when user taps on Stock pile. Either draws Card(s) from Stock if any or resets Stock by
     *  adding Cards back from Waste. [drawAmount] will be used for testing and has default parameter
     *  that will be updated depending on game selected.
     */
    open fun onStockClick(drawAmount: Int): MoveResult {
        // add card to waste if stock is not empty and flip it face up
        if (_stock.truePile.isNotEmpty()) {
            val cards = _stock.getCards(drawAmount)
            val aniInfo = AnimateInfo(
                start = GamePiles.Stock,
                end = GamePiles.Waste,
                animatedCards = cards,
                flipAnimatedCards = FlipCardInfo.FaceUp()
            )
            _stock.removeMany(drawAmount)
            _waste.add(cards)
            aniInfo.actionBeforeAnimation = { _stock.updateDisplayPile() }
            aniInfo.actionAfterAnimation = {
                _waste.updateDisplayPile()
                appendHistory(aniInfo.getUndoAnimateInfo())
            }
            _animateInfo.value = aniInfo
            _stockWasteEmpty.value = if (redealLeft == 0) {
                true
            } else {
                _waste.truePile.size <= 1 && _stock.truePile.isEmpty()
            }
            return Move
        } else if (_waste.truePile.size > 1 && redealLeft != 0) {
            val cards = _waste.truePile.toList()
            val aniInfo = AnimateInfo(
                start = GamePiles.Waste,
                end = GamePiles.Stock,
                animatedCards = listOf(cards.last()),
                flipAnimatedCards = FlipCardInfo.FaceDown()
            )
            _waste.removeAll()
            _stock.add(cards)
            aniInfo.actionBeforeAnimation = { _waste.updateDisplayPile() }
            aniInfo.actionAfterAnimation = {
                _stock.updateDisplayPile()
                appendHistory(aniInfo.getUndoAnimateInfo())
            }
            _animateInfo.value = aniInfo
            redealLeft--
            return Move
        }
        return Illegal
    }

    /**
     *  Runs when user taps on Waste pile. Checks to see if top [Card] can be moved to any other
     *  pile except [Stock]. If so, it is removed from [Waste].
     */
     fun onWasteClick(): MoveResult {
        _waste.let {
            if (it.truePile.isNotEmpty()) {
                val result = checkLegalMove(
                    start = GamePiles.Waste,
                    cards = listOf(it.truePile.last()),
                    ifLegal = { it.remove() },
                    actionBeforeAnimation = { it.updateDisplayPile() }
                )
                // if any move is possible then remove card from waste
                if (result != Illegal) {
                    _stockWasteEmpty.value = if (redealLeft == 0) {
                        true
                    } else {
                        _waste.truePile.size <= 1 && _stock.truePile.isEmpty()
                    }
                    return result
                }
            }
        }
        return Illegal
    }

    /**
     *  Runs when user taps on Foundation with given [fIndex]. Checks to see if top [Card] can be
     *  moved to any [Tableau] pile. If so, it is removed from [_foundation] with given [fIndex].
     */
    fun onFoundationClick(fIndex: Int): MoveResult {
        val foundation = _foundation[fIndex]
        if (foundation.truePile.isNotEmpty()) {
            val result = checkLegalMove(
                start = foundation.suit.gamePile,
                cards = listOf(foundation.truePile.last()),
                ifLegal = { foundation.remove() },
                actionBeforeAnimation = { foundation.updateDisplayPile() }
            )
            // if any move is possible then remove card from foundation
            if (result != Illegal) {
                // legalMove() doesn't detect removal from Foundation which always results in losing
                // score.
                return MoveMinusScore
            }
        }
        return Illegal
    }

    /**
     *  Runs when user taps on Tableau pile with given [tableauIndex] and given [cardIndex]. Checks
     *  to see if tapped sublist of [Card]s can be moved to another [Tableau] pile or a [Foundation].
     */
    fun onTableauClick(tableauIndex: Int, cardIndex: Int): MoveResult {
        val tableauPile = _tableau[tableauIndex]
        val tPile = tableauPile.truePile.toList()
        val fixedCardIndex = cardIndex.coerceAtMost(tPile.size - 1)
        if (tPile.isNotEmpty() && tPile[fixedCardIndex].faceUp) {
            val tableauCardFlipInfo = tableauPile.getTableauCardFlipInfo(fixedCardIndex)
            return checkLegalMove(
                start = tableauPile.gamePile,
                cards = tPile.subList(fixedCardIndex, tPile.size),
                startIndex = fixedCardIndex,
                tableauCardFlipInfo = tableauCardFlipInfo,
                ifLegal = { tableauPile.remove(fixedCardIndex) },
                actionBeforeAnimation = { tableauPile.updateDisplayPile() }
            )
        }
        return Illegal
    }

    /**
     *  After meeting certain tableau conditions, depending on game, complete the game for the user.
     */
    protected abstract fun autoCompleteTableauCheck(): Boolean

    /**
     *  After meeting certain conditions, depending on game, complete the game for the user.
     */
    private fun autoComplete() {
        if (_autoCompleteActive.value) return
        if (_stock.truePile.isEmpty() && _waste.truePile.isEmpty()) {
            if (!autoCompleteTableauCheck()) return
            viewModelScope.launch {
                _autoCompleteActive.value = true
                _autoCompleteCorrection = 0
                while (!gameWon()) {
                    _tableau.forEachIndexed { i, tableau ->
                        if (tableau.truePile.isEmpty()) return@forEachIndexed
                        onTableauClick(i, tableau.truePile.size - 1)
                        delay(310)
                    }
                }
            }
        }
    }

    /**
     *  Should be called after successful [onWasteClick] or [onTableauClick] since game can only end
     *  after one of those clicks and if each foundation pile has exactly 13 Cards.
     */
    private fun gameWon(): Boolean {
        foundation.forEach { if (it.displayPile.size != 13) return false }
        _autoCompleteActive.value = false
        _gameWon.value = true
        return true
    }

    /**
     *  Checks if move is possible by attempting to add [cards] to piles. Returns true if added.
     */
    private fun checkLegalMove(
        start: GamePiles,
        cards: List<Card>,
        startIndex: Int = 0,
        tableauCardFlipInfo: TableauCardFlipInfo? = null,
        ifLegal: () -> Unit,
        actionBeforeAnimation: () -> Unit
    ): MoveResult {
        if (cards.size == 1) {
            _foundation.forEach {
                if (it.canAdd(cards)) {
                    val aniInfo = AnimateInfo(
                        start = start,
                        end = it.suit.gamePile,
                        animatedCards = cards,
                        startTableauIndex = startIndex,
                        tableauCardFlipInfo = tableauCardFlipInfo
                    )
                    ifLegal()
                    it.add(cards)
                    aniInfo.actionBeforeAnimation = actionBeforeAnimation
                    aniInfo.actionAfterAnimation = {
                        it.updateDisplayPile()
                        appendHistory(aniInfo.getUndoAnimateInfo())
                        autoComplete()
                    }
                    _animateInfo.value = aniInfo
                    _autoCompleteCorrection++
                    return MoveScore
                }
            }
        }
        // try to add to non-empty tableau first
        _tableau.forEach {
            val endIndex = it.truePile.size
            if (it.truePile.isNotEmpty() && it.canAdd(cards)) {
                val aniInfo = AnimateInfo(
                    start = start,
                    end = it.gamePile,
                    animatedCards = cards,
                    startTableauIndex = startIndex,
                    endTableauIndex = endIndex,
                    tableauCardFlipInfo = tableauCardFlipInfo
                )
                ifLegal()
                it.add(cards)
                aniInfo.actionBeforeAnimation = actionBeforeAnimation
                aniInfo.actionAfterAnimation = {
                    it.updateDisplayPile()
                    appendHistory(aniInfo.getUndoAnimateInfo())
                    autoComplete()
                }
                _animateInfo.value = aniInfo
                return Move
            }
        }
        _tableau.forEach {
            if (it.truePile.isEmpty() && it.canAdd(cards)) {
                val aniInfo = AnimateInfo(
                    start = start,
                    end = it.gamePile,
                    animatedCards = cards,
                    startTableauIndex = startIndex,
                    tableauCardFlipInfo = tableauCardFlipInfo
                )
                ifLegal()
                it.add(cards)
                aniInfo.actionBeforeAnimation = actionBeforeAnimation
                aniInfo.actionAfterAnimation = {
                    it.updateDisplayPile()
                    appendHistory(aniInfo.getUndoAnimateInfo())
                    autoComplete()
                }
                _animateInfo.value = aniInfo
                return Move
            }
        }
        return Illegal
    }

    /**
     *  Adds [currentStep] to our [_historyList] list before overwriting [currentStep] using
     *  . This should be call after every legal move.
     */
    protected fun appendHistory(undoAnimateInfo: AnimateInfo) {
        // limit number of undo steps to 15
        if (_historyList.size == 15) _historyList.removeFirst()
        _historyList.add(undoAnimateInfo)
        _undoEnabled.value = true
    }

    /**
     *  We pop the last [PileHistory] value in [_historyList] and use it to restore the game
     *  state to it.
     */
    fun undo() {
        if (_historyList.isNotEmpty()) {
            val step = _historyList.removeLast()
            val startPile = undoAction(step.start)
            val endPile = undoAction(step.end)
            startPile.undo()
            endPile.undo()
            step.actionBeforeAnimation = {
                startPile.updateDisplayPile()
            }
            step.actionAfterAnimation = {
                endPile.updateDisplayPile()
                _undoEnabled.value = _historyList.isNotEmpty()
            }
            _animateInfo.value = step
        }
    }

    private fun undoAction(gamePile: GamePiles): Pile {
        when (gamePile) {
            GamePiles.Stock -> {
                _stockWasteEmpty.value = if (redealLeft == 0) {
                    true
                } else {
                    _waste.truePile.size <= 1 && _stock.truePile.isEmpty()
                }
                return _stock
            }
            GamePiles.Waste -> {
                _stockWasteEmpty.value = if (redealLeft == 0) {
                    true
                } else {
                    _waste.truePile.size <= 1 && _stock.truePile.isEmpty()
                }
                return _waste
            }
            GamePiles.ClubsFoundation -> return _foundation[0]
            GamePiles.DiamondsFoundation -> return _foundation[1]
            GamePiles.HeartsFoundation -> return _foundation[2]
            GamePiles.SpadesFoundation -> return _foundation[3]
            GamePiles.TableauZero -> return _tableau[0]
            GamePiles.TableauOne -> return _tableau[1]
            GamePiles.TableauTwo -> return _tableau[2]
            GamePiles.TableauThree -> return _tableau[3]
            GamePiles.TableauFour -> return _tableau[4]
            GamePiles.TableauFive -> return _tableau[5]
            GamePiles.TableauSix -> return _tableau[6]
        }
    }

    /**
     *  Used during creation of deck to assign suit to each card.
     *  Cards  0-12 -> Clubs
     *  Cards 13-25 -> Diamonds
     *  Cards 26-38 -> Hearts
     *  Cards 39-51 -> Spades
     */
    protected open fun getSuit(i: Int) = when (i / 13) {
        0 -> Suits.CLUBS
        1 -> Suits.DIAMONDS
        2 -> Suits.HEARTS
        else -> Suits.SPADES
    }

    protected fun initializeTableau(
        type: KClass<out Tableau>,
        initialPiles: List<List<Card>> = List(7) { emptyList() }
    ): MutableList<Tableau> {
        return mutableListOf(
            type.primaryConstructor!!.call(GamePiles.TableauZero, initialPiles[0]),
            type.primaryConstructor!!.call(GamePiles.TableauOne, initialPiles[1]),
            type.primaryConstructor!!.call(GamePiles.TableauTwo, initialPiles[2]),
            type.primaryConstructor!!.call(GamePiles.TableauThree, initialPiles[3]),
            type.primaryConstructor!!.call(GamePiles.TableauFour, initialPiles[4]),
            type.primaryConstructor!!.call(GamePiles.TableauFive, initialPiles[5]),
            type.primaryConstructor!!.call(GamePiles.TableauSix, initialPiles[6])
        )
    }
}