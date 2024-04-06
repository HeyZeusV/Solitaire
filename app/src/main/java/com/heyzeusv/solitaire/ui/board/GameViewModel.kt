package com.heyzeusv.solitaire.ui.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.solitaire.data.AnimateInfo
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.FlipCardInfo
import com.heyzeusv.solitaire.data.TableauCardFlipInfo
import com.heyzeusv.solitaire.data.LayoutInfo
import com.heyzeusv.solitaire.data.pile.Foundation
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.pile.Pile
import com.heyzeusv.solitaire.data.pile.Stock
import com.heyzeusv.solitaire.data.pile.Waste
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.ui.board.games.Easthaven
import com.heyzeusv.solitaire.ui.board.games.Games
import com.heyzeusv.solitaire.ui.board.games.KlondikeTurnOne
import com.heyzeusv.solitaire.util.AnimationDurations
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.MoveResult
import com.heyzeusv.solitaire.util.MoveResult.*
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.Suits
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/**
 *  Data manager for game.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
@HiltViewModel
class GameViewModel @Inject constructor(
    private val ss: ShuffleSeed,
    val layoutInfo: LayoutInfo
) : ViewModel() {

    // ensures only one actionBefore/AfterAnimation occurs at a time.
    private val mutex = Mutex()

    var selectedGame: Games = KlondikeTurnOne
        private set
    fun updateSelectedGame(newGame: Games) {
        selectedGame = newGame
        resetAll(ResetOptions.NEW)
    }

    private var shuffledDeck = emptyList<Card>()

    private var redealLeft: Int = 1000

    private val _stock = Stock()
    val stock: Stock get() = _stock

    private val _waste = Waste()
    val waste: Waste get() = _waste

    private val _stockWasteEmpty = MutableStateFlow(false)
    val stockWasteEmpty: StateFlow<Boolean> get() = _stockWasteEmpty

    private val _foundation = Suits.entries.map { Foundation(it) }.toMutableList()
    val foundation: List<Foundation> get() = _foundation

    private val _tableau: MutableList<Tableau> = initializeTableau()
    val tableau: List<Tableau> get() = _tableau

    private val _historyList = mutableListOf<AnimateInfo>()
    private val historyList: List<AnimateInfo> get() = _historyList

    // determines if Undo Button is available
    private val _undoEnabled = MutableStateFlow(false)
    val undoEnabled: StateFlow<Boolean> get() = _undoEnabled
    fun updateUndoEnabled(newValue: Boolean) { _undoEnabled.value = newValue }

    // disables any Board clicks during undo animations
    private val _undoAnimation = MutableStateFlow(false)
    val undoAnimation: StateFlow<Boolean> get() = _undoAnimation
    fun updateUndoAnimation(newValue: Boolean) { _undoAnimation.value = newValue }

    private val _autoCompleteActive = MutableStateFlow(false)
    val autoCompleteActive: StateFlow<Boolean> get() = _autoCompleteActive

    private var _autoCompleteCorrection: Int = 0
    val autoCompleteCorrection: Int get() = _autoCompleteCorrection

    private val _gameWon = MutableStateFlow(false)
    val gameWon: StateFlow<Boolean> get() = _gameWon

    private val _animateInfo = MutableStateFlow<AnimateInfo?>(null)
    val animateInfo: StateFlow<AnimateInfo?> get() = _animateInfo
    fun updateAnimateInfo(newValue: AnimateInfo?) { _animateInfo.value = newValue }

    private var autoCompleteDelay: Long = AnimationDurations.Fast.autoCompleteDelay
    fun updateAutoCompleteDelay(newValue: Long) { autoCompleteDelay = newValue }

    /**
     *  Goes through all the card piles in the game and resets them for either the same game or a
     *  new game depending on [resetOption].
     */
    private fun reset(resetOption: ResetOptions) {
        redealLeft = selectedGame.redeals.amount
        when (resetOption) {
            ResetOptions.RESTART -> _stock.reset(shuffledDeck)
            ResetOptions.NEW -> {
                shuffledDeck = selectedGame.baseDeck.shuffled(ss.shuffleSeed)
                _stock.reset(shuffledDeck)
            }
        }
        // clear the waste pile
        _waste.reset()
        _historyList.clear()
        _undoEnabled.value = false
        _undoAnimation.value = false
        _gameWon.value = false
        _autoCompleteActive.value = false
        _animateInfo.value = null
        _autoCompleteCorrection = 0
        _stockWasteEmpty.value = true
    }

//    /**
//     *  Initial Tableau state might be different from game to game.
//     */
//    protected abstract fun resetTableau()

    /**
     *  Helper function to call both reset functions at the same time.
     */
    fun resetAll(resetOption: ResetOptions) {
        reset(resetOption)
        selectedGame.resetFoundation(foundation)
        selectedGame.resetTableau(tableau, stock)
        _stock.recordHistory()
    }

    /**
     *  Checks [selectedGame] value to determine which onStockClick to run.
     */
    fun onStockClick(): MoveResult {
        return when (selectedGame) {
            is Easthaven -> onStockClickEasthaven()
            else -> onStockClickStandard()
        }
    }

    /**
     *  Runs when user taps on Stock pile. Either draws Card(s) from Stock if any or resets Stock by
     *  adding Cards back from Waste.
     */
    private fun onStockClickStandard(): MoveResult {
        // add card to waste if stock is not empty and flip it face up
        if (_stock.truePile.isNotEmpty()) {
            val cards = _stock.getCards(selectedGame.drawAmount.amount)
            val aniInfo = AnimateInfo(
                start = GamePiles.Stock,
                end = GamePiles.Waste,
                animatedCards = cards,
                flipCardInfo = FlipCardInfo.FaceUp.SinglePile,
            )
            aniInfo.actionBeforeAnimation = {
                mutex.withLock {
                    _stock.removeMany(selectedGame.drawAmount.amount)
                    _waste.add(cards)
                    _stock.updateDisplayPile()
                }
            }
            aniInfo.actionAfterAnimation = {
                mutex.withLock {
                    _waste.updateDisplayPile()
                    appendHistory(aniInfo.getUndoAnimateInfo())
                }
            }
            _animateInfo.value = aniInfo
            checkStockWasteEmpty()
            return Move
        } else if (_waste.truePile.size > 1 && redealLeft != 0) {
            val cards = _waste.truePile.toList()
            val aniInfo = AnimateInfo(
                start = GamePiles.Waste,
                end = GamePiles.Stock,
                animatedCards = listOf(cards.last()),
                flipCardInfo = FlipCardInfo.FaceDown.SinglePile,
            )
            aniInfo.actionBeforeAnimation = {
                mutex.withLock {
                    _waste.removeAll()
                    _stock.add(cards)
                    _waste.updateDisplayPile()
                }
            }
            aniInfo.actionAfterAnimation = {
                mutex.withLock {
                    _stock.updateDisplayPile()
                    appendHistory(aniInfo.getUndoAnimateInfo())
                }
            }
            _animateInfo.value = aniInfo
            redealLeft--
            return Move
        }
        return Illegal
    }

    /**
     *  Custom onStockClick for [Easthaven] due to [Card]s being move directly from [Stock] to
     *  [Tableau]. Each click on [Stock] attempts to move 1 [Card] to each [Tableau] pile. If there
     *  isn't enough for all 7 [Tableau] piles, it adds from left to right until it runs out.
     */
    private fun onStockClickEasthaven(): MoveResult {
        if (_stock.truePile.isNotEmpty()) {
            val stockCards = _stock.getCards(selectedGame.drawAmount.amount)
            val tableauIndices = mutableListOf<Int>()
            _tableau.forEach { tableau -> tableauIndices.add(tableau.truePile.size) }
            val aniInfo = AnimateInfo(
                start = GamePiles.Stock,
                end = GamePiles.TableauAll,
                endTableauIndices = tableauIndices,
                animatedCards = stockCards,
                flipCardInfo = FlipCardInfo.FaceUp.MultiPile
            )
            aniInfo.actionBeforeAnimation = {
                mutex.withLock {
                    _stock.removeMany(stockCards.size)
                    _tableau.forEachIndexed { index, tableau ->
                        val stockCard: List<Card> = try {
                            listOf(stockCards[index].copy(faceUp = true))
                        } catch (e: IndexOutOfBoundsException) {
                            emptyList()
                        }
                        tableau.add(stockCard)
                    }
                    _stock.updateDisplayPile()
                }
            }
            aniInfo.actionAfterAnimation = {
                mutex.withLock {
                    _tableau.forEach { it.updateDisplayPile() }
                    appendHistory(aniInfo.getUndoAnimateInfo())
                }
            }
            _animateInfo.value = aniInfo
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
                    cards = listOf(it.truePile.last()),
                    start = GamePiles.Waste,
                    ifLegal = { it.remove() }
                ) { it.updateDisplayPile() }
                // if any move is possible then remove card from waste
                if (result != Illegal) {
                    checkStockWasteEmpty()
                    return result
                }
            }
        }
        return Illegal
    }

    /**
     *  Runs when user taps on Foundation with given [fIndex]. Checks to see if top [Card] can be
     *  moved to any [Tableau] pile. If so, it is removed from [foundation] with given [fIndex].
     */
    fun onFoundationClick(fIndex: Int): MoveResult {
        val foundationPile = _foundation[fIndex]
        if (foundationPile.truePile.isNotEmpty()) {
            val result = checkLegalMove(
                cards = listOf(foundationPile.truePile.last()),
                start = foundationPile.suit.gamePile,
                ifLegal = { foundationPile.remove() }
            ) { foundationPile.updateDisplayPile() }
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
        val tableauTruePile = tableauPile.truePile.toList()
        val fixedCardIndex = cardIndex.coerceAtMost(tableauTruePile.size - 1)
        if (tableauTruePile.isNotEmpty() && tableauTruePile[fixedCardIndex].faceUp) {
            val tableauCardFlipInfo = tableauPile.getTableauCardFlipInfo(fixedCardIndex)
            return checkLegalMove(
                cards = tableauTruePile.subList(fixedCardIndex, tableauTruePile.size),
                start = tableauPile.gamePile,
                startIndex = fixedCardIndex,
                tableauCardFlipInfo = tableauCardFlipInfo,
                ifLegal = { tableauPile.remove(fixedCardIndex) }
            ) { tableauPile.updateDisplayPile() }
        }
        return Illegal
    }

//    /**
//     *  After meeting certain tableau conditions, depending on game, complete the game for the user.
//     */
//    protected abstract fun autoCompleteTableauCheck(): Boolean

    /**
     *  After meeting certain conditions, depending on game, complete the game for the user.
     */
    private fun autoComplete() {
        if (_autoCompleteActive.value) return
        if (_stock.truePile.isEmpty() && _waste.truePile.isEmpty()) {
            if (!selectedGame.autocompleteTableauCheck(tableau)) return
            viewModelScope.launch {
                _undoAnimation.value = true
                _autoCompleteActive.value = true
                _autoCompleteCorrection = 0
                while (!gameWon()) {
                    _tableau.forEachIndexed { i, tableau ->
                        if (tableau.truePile.isEmpty()) return@forEachIndexed
                        _foundation.forEach { foundation ->
                            if (foundation.canAdd(tableau.truePile.takeLast(1))) {
                                delay(autoCompleteDelay)
                                onTableauClick(i, tableau.truePile.size - 1)
                            }
                        }
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
        foundation.forEach { if (it.truePile.size != 13) return false }
        _autoCompleteActive.value = false
        _gameWon.value = true
        return true
    }

    /**
     *  Checks if move is possible by attempting to add [cards] to piles using canAdd(). If possible,
     *  uses rest of parameters to create [AnimateInfo].
     */
    private fun checkLegalMove(
        cards: List<Card>,
        start: GamePiles,
        startIndex: Int = 0,
        tableauCardFlipInfo: TableauCardFlipInfo? = null,
        ifLegal: () -> Unit,
        actionBeforeAnimation: () -> Unit
    ): MoveResult {
        // only one card can be added to Foundation at a time.
        if (cards.size == 1) {
            _foundation.forEach {
                if (it.canAdd(cards)) {
                    val aniInfo = AnimateInfo(
                        start = start,
                        end = it.suit.gamePile,
                        animatedCards = cards,
                        startTableauIndices = listOf(startIndex),
                        tableauCardFlipInfo = tableauCardFlipInfo
                    )
                    aniInfo.actionBeforeAnimation = {
                        mutex.withLock {
                            ifLegal()
                            it.add(cards)
                            actionBeforeAnimation()
                        }
                    }
                    aniInfo.actionAfterAnimation = {
                        mutex.withLock {
                            it.updateDisplayPile()
                            appendHistory(aniInfo.getUndoAnimateInfo())
                            autoComplete()
                        }
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
            if (it.truePile.isNotEmpty() && selectedGame.canAddToTableau(it, cards)) {
                val aniInfo = AnimateInfo(
                    start = start,
                    end = it.gamePile,
                    animatedCards = cards,
                    startTableauIndices = listOf(startIndex),
                    endTableauIndices = listOf(endIndex),
                    tableauCardFlipInfo = tableauCardFlipInfo
                )
                aniInfo.actionBeforeAnimation = {
                    mutex.withLock {
                        ifLegal()
                        it.add(cards)
                        actionBeforeAnimation()
                    }
                }
                aniInfo.actionAfterAnimation = {
                    mutex.withLock {
                        it.updateDisplayPile()
                        appendHistory(aniInfo.getUndoAnimateInfo())
                        autoComplete()
                    }
                }
                _animateInfo.value = aniInfo
                return Move
            }
        }
        _tableau.forEach {
            if (it.truePile.isEmpty() && selectedGame.canAddToTableau(it, cards)) {
                val aniInfo = AnimateInfo(
                    start = start,
                    end = it.gamePile,
                    animatedCards = cards,
                    startTableauIndices = listOf(startIndex),
                    tableauCardFlipInfo = tableauCardFlipInfo
                )
                aniInfo.actionBeforeAnimation = {
                    mutex.withLock {
                        ifLegal()
                        it.add(cards)
                        actionBeforeAnimation()
                    }
                }
                aniInfo.actionAfterAnimation = {
                    mutex.withLock {
                        it.updateDisplayPile()
                        appendHistory(aniInfo.getUndoAnimateInfo())
                        autoComplete()
                    }
                }
                _animateInfo.value = aniInfo
                return Move
            }
        }
        return Illegal
    }

    /**
     *  Adds [undoAnimateInfo] to [historyList].This should be called after every legal move.
     *  Limits [historyList] size to 15.
     */
    private fun appendHistory(undoAnimateInfo: AnimateInfo) {
        // limit number of undo steps to 15
        if (_historyList.size == 15) _historyList.removeFirst()
        _historyList.add(undoAnimateInfo)
        _undoEnabled.value = true
    }

    /**
     *  Pops the last [AnimateInfo] value in [historyList] and uses it to restore the affect piles
     *  to a previous state.
     */
    fun undo() {
        if (_historyList.isNotEmpty()) {
            val step = _historyList.removeLast()
            val startPiles = undoPiles(step.start)
            val endPiles = undoPiles(step.end)
            step.actionBeforeAnimation = {
                mutex.withLock {
                    startPiles.forEach { it.undo() }
                    endPiles.forEach { it.undo() }
                    startPiles.forEach { it.updateDisplayPile() }
                }
            }
            step.actionAfterAnimation = {
                mutex.withLock { endPiles.forEach { it.updateDisplayPile() } }
            }
            _animateInfo.value = step
        }
    }

    /**
     *  Returns list of [Pile]s depending on given [gamePile].
     */
    private fun undoPiles(gamePile: GamePiles): List<Pile> {
        when (gamePile) {
            GamePiles.Stock -> {
                checkStockWasteEmpty()
                return listOf(_stock)
            }
            GamePiles.Waste -> {
                checkStockWasteEmpty()
                return listOf(_waste)
            }
            GamePiles.ClubsFoundation -> return listOf(_foundation[0])
            GamePiles.DiamondsFoundation -> return listOf(_foundation[1])
            GamePiles.HeartsFoundation -> return listOf(_foundation[2])
            GamePiles.SpadesFoundation -> return listOf(_foundation[3])
            GamePiles.TableauZero -> return listOf(_tableau[0])
            GamePiles.TableauOne -> return listOf(_tableau[1])
            GamePiles.TableauTwo -> return listOf(_tableau[2])
            GamePiles.TableauThree -> return listOf(_tableau[3])
            GamePiles.TableauFour -> return listOf(_tableau[4])
            GamePiles.TableauFive -> return listOf(_tableau[5])
            GamePiles.TableauSix -> return listOf(_tableau[6])
            GamePiles.TableauAll -> return _tableau
        }
    }

//    /**
//     *  Used during creation of deck to assign suit to each card.
//     *  Cards  0-12 -> Clubs
//     *  Cards 13-25 -> Diamonds
//     *  Cards 26-38 -> Hearts
//     *  Cards 39-51 -> Spades
//     */
//    protected open fun getSuit(i: Int) = when (i / 13) {
//        0 -> Suits.CLUBS
//        1 -> Suits.DIAMONDS
//        2 -> Suits.HEARTS
//        else -> Suits.SPADES
//    }

    /**
     *  Initializes 7 [Tableau] with [initialPiles].
     */
    private fun initializeTableau(
        initialPiles: List<List<Card>> = List(7) { emptyList() }
    ): MutableList<Tableau> {
        return mutableListOf(
            Tableau(GamePiles.TableauZero, initialPiles[0]),
            Tableau(GamePiles.TableauOne, initialPiles[1]),
            Tableau(GamePiles.TableauTwo, initialPiles[2]),
            Tableau(GamePiles.TableauThree, initialPiles[3]),
            Tableau(GamePiles.TableauFour, initialPiles[4]),
            Tableau(GamePiles.TableauFive, initialPiles[5]),
            Tableau(GamePiles.TableauSix, initialPiles[6])
        )
    }

    /**
     *  Updates [stockWasteEmpty] depending on various checks.
     */
    private fun checkStockWasteEmpty() {
        _stockWasteEmpty.value = if (redealLeft == 0) {
            true
        } else {
            _waste.truePile.size <= 1 && _stock.truePile.isEmpty()
        }
    }
}