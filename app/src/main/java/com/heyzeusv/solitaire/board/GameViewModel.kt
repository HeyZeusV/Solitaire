package com.heyzeusv.solitaire.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.solitaire.board.animation.AnimateInfo
import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.animation.FlipCardInfo
import com.heyzeusv.solitaire.board.animation.TableauCardFlipInfo
import com.heyzeusv.solitaire.board.piles.Foundation
import com.heyzeusv.solitaire.board.piles.ShuffleSeed
import com.heyzeusv.solitaire.board.piles.Pile
import com.heyzeusv.solitaire.board.piles.Stock
import com.heyzeusv.solitaire.board.piles.Waste
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.board.layouts.ScreenLayouts
import com.heyzeusv.solitaire.games.Easthaven
import com.heyzeusv.solitaire.games.Games
import com.heyzeusv.solitaire.games.Golf
import com.heyzeusv.solitaire.games.KlondikeTurnOne
import com.heyzeusv.solitaire.scoreboard.ScoreboardLogic
import com.heyzeusv.solitaire.board.animation.AnimationDurations
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.MoveResult
import com.heyzeusv.solitaire.util.MoveResult.*
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.util.allFaceUp
import com.heyzeusv.solitaire.util.inOrder
import com.heyzeusv.solitaire.util.isNotMultiSuit
import com.heyzeusv.solitaire.util.numInOrder
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
    val screenLayouts: ScreenLayouts
) : ViewModel() {

    // ensures only one actionBefore/AfterAnimation occurs at a time.
    private val mutex = Mutex()
    private val spiderMutex = Mutex()

    val sbLogic: ScoreboardLogic = ScoreboardLogic(viewModelScope)

    private val _selectedGame = MutableStateFlow<Games>(KlondikeTurnOne)
    val selectedGame: StateFlow<Games> get() = _selectedGame
    fun updateSelectedGame(newGame: Games) {
        _selectedGame.value = newGame
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

    private val _foundation = initializeFoundation()
    val foundation: List<Foundation> get() = _foundation

    private val _tableau = initializeTableau()
    val tableau: List<Tableau> get() = _tableau

    private val _historyList = mutableListOf<AnimateInfo>()
    val historyList: List<AnimateInfo> get() = _historyList

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

    private val _gameWon = MutableStateFlow(false)
    val gameWon: StateFlow<Boolean> get() = _gameWon

    private val _animateInfo = MutableStateFlow<AnimateInfo?>(null)
    val animateInfo: StateFlow<AnimateInfo?> get() = _animateInfo
    fun updateAnimateInfo(newValue: AnimateInfo?) { _animateInfo.value = newValue }

    private val _spiderAnimateInfo = MutableStateFlow<AnimateInfo?>(null)
    val spiderAnimateInfo: StateFlow<AnimateInfo?> get() = _spiderAnimateInfo
    fun updateSpiderAnimateInfo(newValue: AnimateInfo?) { _spiderAnimateInfo.value = newValue }

    private var animationDurations: AnimationDurations = AnimationDurations.None
    fun updateAutoComplete(newValue: AnimationDurations) { animationDurations = newValue }

    /**
     *  Goes through all the card piles in the game and resets them for either the same game or a
     *  new game depending on [resetOption].
     */
    private fun reset(resetOption: ResetOptions) {
        sbLogic.reset(_selectedGame.value.startingScore)
        redealLeft = _selectedGame.value.redeals.amount
        when (resetOption) {
            ResetOptions.RESTART -> _stock.reset(shuffledDeck)
            ResetOptions.NEW -> {
                shuffledDeck = _selectedGame.value.baseDeck.shuffled(ss.shuffleSeed)
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
        _stockWasteEmpty.value = true
    }

    /**
     *  Helper function to call both reset functions at the same time.
     */
    fun resetAll(resetOption: ResetOptions) {
        reset(resetOption)
        _selectedGame.value.resetFoundation(foundation, stock)
        _selectedGame.value.resetTableau(tableau, stock)
        _stock.recordHistory()
    }

    /**
     *  Checks [selectedGame] value to determine which onStockClick to run.
     */
    fun onStockClick() {
        when (_selectedGame.value) {
            is Easthaven, is Games.SpiderFamily, is Games.AcesUpVariants -> onStockClickMultiPile()
            is Games.GolfFamily -> onStockClickGolf()
            else -> onStockClickStandard()
        }
    }

    /**
     *  Runs when user taps on Stock pile. Either draws Card(s) from Stock if any or resets Stock by
     *  adding Cards back from Waste.
     */
    private fun onStockClickStandard() {
        // add card to waste if stock is not empty and flip it face up
        if (_stock.truePile.isNotEmpty()) {
            val cards = _stock.getCards(_selectedGame.value.drawAmount.amount)
            val aniInfo = AnimateInfo(
                start = GamePiles.Stock,
                end = GamePiles.Waste,
                animatedCards = cards,
                flipCardInfo = FlipCardInfo.FaceUp.SinglePile,
            )
            aniInfo.actionBeforeAnimation = {
                mutex.withLock {
                    _stock.removeMany(_selectedGame.value.drawAmount.amount)
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
            sbLogic.handleMoveResult(Move)
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
            sbLogic.handleMoveResult(Move)
        }
    }

    /**
     *  Custom onStockClick for [Games] that require [Card]s to be move directly from [Stock] to
     *  [Tableau]. Each click on [Stock] attempts to move 1 [Card] to each [Tableau] pile. If there
     *  isn't enough for all [Tableau] piles, it adds from left to right until it runs out.
     */
    private fun onStockClickMultiPile() {
        if (_stock.truePile.isNotEmpty()) {
            val stockCards = _stock.getCards(_selectedGame.value.drawAmount.amount)
            val tableauIndices = mutableListOf<Int>()
            for (i in 0 until _selectedGame.value.numOfTableauPiles.amount) {
                tableauIndices.add(_tableau[i].truePile.size)
            }
            val aniInfo = AnimateInfo(
                start = GamePiles.Stock,
                end = GamePiles.TableauAll,
                animatedCards = stockCards,
                endTableauIndices = tableauIndices,
                flipCardInfo = FlipCardInfo.FaceUp.MultiPile
            )
            aniInfo.actionBeforeAnimation = {
                mutex.withLock {
                    _stock.removeMany(stockCards.size)
                    _tableau.forEachIndexed { index, tableau ->
                        if (index < _selectedGame.value.numOfTableauPiles.amount) {
                            val stockCard: List<Card> = try {
                                listOf(stockCards[index].copy(faceUp = true))
                            } catch (e: IndexOutOfBoundsException) {
                                emptyList()
                            }
                            tableau.add(stockCard)
                        }
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
            sbLogic.handleMoveResult(Move)
        }
    }

    /**
     *  Custom onStockClick for [Golf] due to [Card]s being move directly from [Stock] to specific
     *  [Foundation] pile. Each click on [Stock] attempts to move 1 [Card] to each [Tableau] pile.
     */
    private fun onStockClickGolf() {
        if (_stock.truePile.isNotEmpty()) {
            val cards = _stock.getCards(_selectedGame.value.drawAmount.amount)
            val aniInfo = AnimateInfo(
                start = GamePiles.Stock,
                end = GamePiles.FoundationSpadesOne,
                animatedCards = cards,
                flipCardInfo = FlipCardInfo.FaceUp.SinglePile
            )
            aniInfo.actionBeforeAnimation = {
                mutex.withLock {
                    _stock.removeMany(cards.size)
                    _foundation[3].add(cards)
                    _stock.updateDisplayPile()
                }
            }
            aniInfo.actionAfterAnimation = {
                mutex.withLock {
                    _foundation[3].updateDisplayPile()
                    appendHistory(aniInfo.getUndoAnimateInfo())
                    gameWon()
                }
            }
            _animateInfo.value = aniInfo
            sbLogic.handleMoveResult(MoveScore)
        }
    }

    /**
     *  Runs when user taps on Waste pile. Checks to see if top [Card] can be moved to any other
     *  pile except [Stock]. If so, it is removed from [Waste].
     */
    fun onWasteClick() {
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
                    sbLogic.handleMoveResult(result)
                }
            }
        }
    }

    /**
     *  Runs when user taps on Foundation with given [fIndex]. Checks to see if top [Card] can be
     *  moved to any [Tableau] pile. If so, it is removed from [foundation] with given [fIndex].
     */
    fun onFoundationClick(fIndex: Int) {
        val foundationPile = _foundation[fIndex]
        if (foundationPile.truePile.isNotEmpty()) {
            val result = checkLegalMove(
                cards = listOf(foundationPile.truePile.last()),
                start = foundationPile.gamePile,
                ifLegal = { foundationPile.remove() }
            ) { foundationPile.updateDisplayPile() }
            // legalMove() doesn't detect removal from Foundation which always results in losing
            // score.
            if (result != Illegal) { sbLogic.handleMoveResult(MoveMinusScore) }
        }
    }

    /**
     *  Runs when user taps on Tableau pile with given [tableauIndex] and given [cardIndex]. Checks
     *  to see if tapped sublist of [Card]s can be moved to another [Tableau] pile or a [Foundation].
     */
    fun onTableauClick(tableauIndex: Int, cardIndex: Int) {
        val tableauPile = _tableau[tableauIndex]
        val tableauTruePile = tableauPile.truePile.toList()
        val fixedCardIndex = cardIndex.coerceAtMost(tableauTruePile.size - 1)
        if (tableauTruePile.isNotEmpty() && tableauTruePile[fixedCardIndex].faceUp) {
            val tableauCardFlipInfo = tableauPile.getTableauCardFlipInfo(fixedCardIndex)
            val result = checkLegalMove(
                cards = tableauTruePile.subList(fixedCardIndex, tableauTruePile.size),
                start = tableauPile.gamePile,
                startIndex = fixedCardIndex,
                tableauCardFlipInfo = tableauCardFlipInfo,
                ifLegal = { tableauPile.remove(fixedCardIndex) }
            ) { tableauPile.updateDisplayPile() }
            sbLogic.handleMoveResult(result)
        }
    }

    /**
     *  After meeting certain conditions, depending on game, complete the game for the user.
     */
    private fun autoComplete() {
        if (_autoCompleteActive.value) return
        if (!_selectedGame.value.autocompleteAvailable) {
            if (!gameWon()) return
        }
        if (_stock.truePile.isEmpty() && _waste.truePile.isEmpty()) {
            if (!_selectedGame.value.autocompleteTableauCheck(tableau)) return
            viewModelScope.launch {
                _undoAnimation.value = true
                _autoCompleteActive.value = true
                while (!gameWon()) {
                    _tableau.forEach { tableau ->
                        if (tableau.truePile.isEmpty()) return@forEach
                        val lastTCard = tableau.truePile.last()
                        _foundation.forEachIndexed { index, foundation ->
                            if (index < _selectedGame.value.numOfFoundationPiles.amount) {
                                if (selectedGame.value.canAddToFoundation(foundation, lastTCard)) {
                                    val aniInfo = AnimateInfo(
                                        start = tableau.gamePile,
                                        end = foundation.gamePile,
                                        animatedCards = listOf(lastTCard),
                                        startTableauIndices = listOf(tableau.truePile.size - 1)
                                    )
                                    aniInfo.actionBeforeAnimation = {
                                        mutex.withLock {
                                            tableau.remove(tableau.truePile.size - 1)
                                            foundation.add(listOf(lastTCard))
                                            tableau.updateDisplayPile()
                                        }
                                    }
                                    aniInfo.actionAfterAnimation = {
                                        mutex.withLock {
                                            foundation.updateDisplayPile()
                                            sbLogic.handleMoveResult(MoveScore)
                                        }
                                    }
                                    _animateInfo.value = aniInfo
                                    delay(animationDurations.autoCompleteDelay)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *  Called during [autoComplete] and uses [Games.gameWon] to determine if user has won.
     */
    private fun gameWon(): Boolean {
        return if (_selectedGame.value.gameWon(foundation)) {
            _autoCompleteActive.value = false
            _gameWon.value = true
            true
        } else {
            false
        }
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
        return when (_selectedGame.value) {
            is Games.SpiderFamily -> checkLegalMoveSpider(
                cards = cards,
                start = start,
                startIndex = startIndex,
                tableauCardFlipInfo = tableauCardFlipInfo,
                ifLegal = ifLegal,
                actionBeforeAnimation = actionBeforeAnimation
            )
            else -> checkLegalMoveStandard(
                cards = cards,
                start = start,
                startIndex = startIndex,
                tableauCardFlipInfo = tableauCardFlipInfo,
                ifLegal = ifLegal,
                actionBeforeAnimation = actionBeforeAnimation
            )
        }
    }

    /**
     *  Checks if move is possible by attempting to add [cards] to piles using canAdd(). If possible,
     *  uses rest of parameters to create [AnimateInfo].
     */
    private fun checkLegalMoveStandard(
        cards: List<Card>,
        start: GamePiles,
        startIndex: Int = 0,
        tableauCardFlipInfo: TableauCardFlipInfo?,
        ifLegal: () -> Unit,
        actionBeforeAnimation: () -> Unit
    ): MoveResult {
        // only one card can be added to Foundation at a time.
        if (cards.size == 1) {
            _foundation.forEachIndexed { index, it ->
                if (index < _selectedGame.value.numOfFoundationPiles.amount) {
                    if (selectedGame.value.canAddToFoundation(it, cards.first())) {
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
                        return MoveScore
                    }
                }
            }
        }
        // try to add to non-empty tableau first
        _tableau.forEachIndexed { index, it ->
            if (index < _selectedGame.value.numOfTableauPiles.amount) {
                if (it.truePile.isNotEmpty() && _selectedGame.value.canAddToTableau(it, cards)) {
                    val endIndex = it.truePile.size
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
        }
        _tableau.forEachIndexed { index, it ->
            if (index < _selectedGame.value.numOfTableauPiles.amount) {
                if (it.truePile.isEmpty() && _selectedGame.value.canAddToTableau(it, cards)) {
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
        }
        return Illegal
    }

    /**
     *  Checks if given [cards] can be added to any [Tableau] pile. If there are multiple valid
     *  piles to add to, the pile with most valid cards past top card (ascending order, same suit,
     *  face up) will receive [cards]. If possible, uses rest of parameters to create [AnimateInfo].
     */
    private fun checkLegalMoveSpider(
        cards: List<Card>,
        start: GamePiles,
        startIndex: Int = 0,
        tableauCardFlipInfo: TableauCardFlipInfo?,
        ifLegal: () -> Unit,
        actionBeforeAnimation: () -> Unit
    ): MoveResult {
        var validTableau: Tableau? = null
        _tableau.forEachIndexed { index, it ->
            if (start != it.gamePile) {
                if (index < _selectedGame.value.numOfTableauPiles.amount) {
                    if (_selectedGame.value.canAddToTableau(it, cards)) {
                        if (validTableau == null) {
                            validTableau = it
                        } else {
                            val validFaceUp = validTableau!!.truePile.numInOrder()
                            val currentFaceUp = it.truePile.numInOrder()
                            if (currentFaceUp > validFaceUp) validTableau = it
                        }
                    }
                }
            }
        }
        validTableau?.let {
            val aniInfo = AnimateInfo(
                start = start,
                end = it.gamePile,
                animatedCards = cards,
                startTableauIndices = listOf(startIndex),
                endTableauIndices = listOf(it.truePile.size),
                tableauCardFlipInfo = tableauCardFlipInfo
            )
            ifLegal()
            it.add(cards)
            val spiderAniInfo = fullPileToFoundation(it)
            aniInfo.actionBeforeAnimation = {
                mutex.withLock {
                    actionBeforeAnimation()
                }
            }
            aniInfo.actionAfterAnimation = {
                mutex.withLock {
                    it.updateDisplayPile()
                    appendHistory(aniInfo.getUndoAnimateInfo())
                }
            }
            _animateInfo.value = aniInfo
            spiderAniInfo?.let {
                viewModelScope.launch {
                    delay(animationDurations.fullDelay)
                    _spiderAnimateInfo.value = it
                }
            }
            return Move
        }
        return Illegal
    }

    /**
     *  Adds 13 cards (Ace to King) from [Tableau] to [Foundation] if they are correctly ranked
     *  and the same suit in given [tPile].
     */
    private fun fullPileToFoundation(tPile: Tableau): AnimateInfo? {
        // A to King is 13 Cards, so no need to check if pile isn't at least 13 Cards
        if (tPile.truePile.size < 13) return null
        val last13Cards = tPile.truePile.takeLast(13)
        if (last13Cards.inOrder() && last13Cards.isNotMultiSuit() && last13Cards.allFaceUp()) {
            _foundation.forEachIndexed { index, foundation ->
                if (index < _selectedGame.value.numOfFoundationPiles.amount) {
                    if (foundation.truePile.isEmpty()) {
                        val tCardIndex = tPile.truePile.indexOfLast { it == last13Cards.first() }
                        val spiderAniInfo = AnimateInfo(
                            start = tPile.gamePile,
                            end = foundation.gamePile,
                            animatedCards = last13Cards,
                            startTableauIndices = List(13) { it + tCardIndex },
                            tableauCardFlipInfo = tPile.getTableauCardFlipInfo(tCardIndex),
                            spiderPile = true
                        )
                        spiderAniInfo.actionBeforeAnimation = {
                            spiderMutex.withLock {
                                tPile.remove(tCardIndex)
                                foundation.addAll(last13Cards)
                                tPile.updateDisplayPile()
                            }
                        }
                        spiderAniInfo.actionAfterAnimation = {
                            spiderMutex.withLock {
                                foundation.updateDisplayPile()
                                appendHistory(spiderAniInfo.getUndoAnimateInfo())
                                autoComplete()
                                sbLogic.handleMoveResult(FullPileScore)
                            }
                        }
                        return spiderAniInfo
                    }
                }
            }
        }
        return null
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
            if (step.spiderPile) {
                viewModelScope.launch {
                    _undoEnabled.value = false
                    _spiderAnimateInfo.value = step
                    delay(animationDurations.fullDelay)
                    undo()
                    _undoEnabled.value = true
                }
            } else {
                sbLogic.undo()
                _animateInfo.value = step
            }
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
            GamePiles.FoundationClubsOne -> return listOf(_foundation[0])
            GamePiles.FoundationDiamondsOne -> return listOf(_foundation[1])
            GamePiles.FoundationHeartsOne -> return listOf(_foundation[2])
            GamePiles.FoundationSpadesOne -> return listOf(_foundation[3])
            GamePiles.FoundationClubsTwo -> return listOf(_foundation[4])
            GamePiles.FoundationDiamondsTwo -> return listOf(_foundation[5])
            GamePiles.FoundationHeartsTwo -> return listOf(_foundation[6])
            GamePiles.FoundationSpadesTwo -> return listOf(_foundation[7])
            GamePiles.TableauZero -> return listOf(_tableau[0])
            GamePiles.TableauOne -> return listOf(_tableau[1])
            GamePiles.TableauTwo -> return listOf(_tableau[2])
            GamePiles.TableauThree -> return listOf(_tableau[3])
            GamePiles.TableauFour -> return listOf(_tableau[4])
            GamePiles.TableauFive -> return listOf(_tableau[5])
            GamePiles.TableauSix -> return listOf(_tableau[6])
            GamePiles.TableauSeven -> return listOf(_tableau[7])
            GamePiles.TableauEight -> return listOf(_tableau[8])
            GamePiles.TableauNine -> return listOf(_tableau[9])
            GamePiles.TableauAll -> return _tableau
        }
    }

    /**
     *  Initializes [Foundation] piles equal to [Games.numOfFoundationPiles].
     */
    private fun initializeFoundation(): List<Foundation> {
        val list = mutableListOf<Foundation>()
        for (i in 0 until 8) {
            list.add(Foundation(Suits.entries[i % 4], GamePiles.entries[i + 2]))
        }
        return list
    }

    /**
     *  Initializes [Tableau] piles equal to [Games.numOfTableauPiles].
     */
    private fun initializeTableau(): List<Tableau> {
        val list = mutableListOf<Tableau>()
        for (i in 0 until 10) {
            list.add(Tableau(GamePiles.entries[i + 10]))
        }
        return list
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

    override fun onCleared() {
        super.onCleared()
        sbLogic.onCleared()
    }
}