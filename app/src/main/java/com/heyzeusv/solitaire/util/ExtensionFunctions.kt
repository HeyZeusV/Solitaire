package com.heyzeusv.solitaire.util

import android.net.NetworkCapabilities
import android.util.Patterns
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.heyzeusv.solitaire.Game
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.scoreboard.LastGameStats
import java.text.DecimalFormat
import java.util.Date
import java.util.regex.Pattern

fun Long.formatTimeDisplay(): String {
    val minutes = this / 60
    val seconds = this % 60

    return String.format("%02d:%02d", minutes, seconds)
}

fun Long.formatTimeStats(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    val hourString = if (hours == 0L) "" else "$hours hours, "

    return "$hourString$minutes minutes, $seconds seconds"
}

fun GameStats.getWinPercentage(): String {
    val df = DecimalFormat("#.##")
    val gamePercent: Double = if (gamesPlayed == 0) {
        0.0
    } else {
        (gamesWon.toDouble() / gamesPlayed) * 100
    }
    return df.format(gamePercent)
}

fun GameStats.getAverageMoves(): Int = if (gamesPlayed == 0) 0 else (totalMoves / gamesPlayed)

fun GameStats.getAverageTime(): Long = if (gamesPlayed == 0) 0L else (totalTime / gamesPlayed)

fun GameStats.getAverageScore(): Int = if (gamesPlayed == 0) 0 else (totalScore / gamesPlayed)

fun GameStats.getScorePercentage(maxScore: MaxScore): String {
    val df = DecimalFormat("#.##")
    val scorePercent: Double = (getAverageScore().toDouble() / maxScore.amount) * 100
    return df.format(scorePercent)
}

fun GameStats.updateStats(lgs: LastGameStats): GameStats {
    return GameStats.newBuilder().also { new ->
        new.game = this.game
        new.gamesPlayed = this.gamesPlayed.plus(1)
        new.gamesWon = this.gamesWon.plus(if (lgs.gameWon) 1 else 0)
        new.lowestMoves =
            if (lgs.gameWon) this.lowestMoves.coerceAtMost(lgs.moves) else this.lowestMoves
        new.totalMoves = this.totalMoves.plus(lgs.moves)
        new.fastestWin =
            if (lgs.gameWon) this.fastestWin.coerceAtMost(lgs.time) else this.fastestWin
        new.totalTime = this.totalTime.plus(lgs.time)
        new.totalScore = this.totalScore.plus(lgs.score)
        new.bestCombinedScore =
            if (lgs.gameWon) {
                this.bestCombinedScore.coerceAtMost(lgs.totalScore)
            } else {
                this.bestCombinedScore
            }
    }.build()
}

fun GameStats.combineGameStats(gs: GameStats): GameStats {
    return GameStats.newBuilder().also { new ->
        new.game = Game.GAME_ALL
        new.gamesPlayed = this.gamesPlayed.plus(gs.gamesPlayed)
        new.gamesWon = this.gamesWon.plus(gs.gamesWon)
        new.lowestMoves = this.lowestMoves.coerceAtMost(gs.lowestMoves)
        new.totalMoves = this.totalMoves.plus(gs.totalMoves)
        new.fastestWin = this.fastestWin.coerceAtMost(gs.fastestWin)
        new.totalTime = this.totalTime.plus(gs.totalTime)
        new.totalScore = this.totalScore.plus(gs.totalScore)
        new.bestCombinedScore = this.bestCombinedScore.coerceAtMost(gs.bestCombinedScore)
    }.build()
}

fun IntOffset.plusX(x: Int) = IntOffset(this.x + x, this.y)

@Composable
fun Int.toDp() = with(LocalDensity.current) { this@toDp.toDp() }

/**
 *  Disables all clicks on children if [disabled] is true.
 *  Found here: https://stackoverflow.com/a/69146178
 */
fun Modifier.gesturesDisabled(disabled: Boolean = true) =
    if (disabled) {
        pointerInput(Unit) {
            awaitPointerEventScope {
                // we should wait for all new pointer events
                while (true) {
                    awaitPointerEvent(pass = PointerEventPass.Initial)
                        .changes
                        .forEach(PointerInputChange::consume)
                }
            }
        }
    } else {
        this
    }

fun ButtonColors.getContainerColor(enabled: Boolean): Color =
    if (enabled) containerColor else disabledContainerColor

fun ButtonColors.getContentColor(enabled: Boolean): Color =
    if (enabled) contentColor else disabledContentColor

/**
 *  Checks if pile is not in descending order.
 */
fun List<Card>.notInOrder(): Boolean {
    val it = this.iterator()
    if (!it.hasNext()) return false
    var current = it.next()
    while (true) {
        if (!it.hasNext()) return false
        val next = it.next()
        if (current.value - 1 != next.value) return true
        current = next
    }
}

/**
 *  Checks if pile is in descending order.
 */
fun List<Card>.inOrder(): Boolean = !this.notInOrder()

/**
 *  Checks if pile contains more than 1 [Suits] type.
 */
fun List<Card>.isMultiSuit(): Boolean = this.map { it.suit }.distinct().size > 1

/**
 *  Checks if piles contains only 1 [Suits] type
 */
fun List<Card>.isNotMultiSuit(): Boolean = !this.isMultiSuit()

/**
 *  Returns the number of cards in order ascending and face up starting from the end of [List].
 */
fun List<Card>.numInOrder(): Int {
    if (this.isEmpty()) return 0
    var num = 1
    val it = this.reversed().iterator()
    var current = it.next()
    while (true) {
        if (!it.hasNext()) return num
        val next = it.next()
        if (current.value + 1 != next.value || current.suit != next.suit || !next.faceUp) return num
        current = next
        num++
    }
}

/**
 *  Checks if entire pile is face up.
 */
fun List<Card>.allFaceUp(): Boolean {
    this.forEach { if (!it.faceUp) return false }
    return true
}

/**
 *  Checks if list is not in order or not alternating color
 */
fun List<Card>.notInOrderOrAltColor(): Boolean {
    val it = this.iterator()
    if (!it.hasNext()) return false
    var current = it.next()
    while (true) {
        if (!it.hasNext()) return false
        val next = it.next()
        if (current.value - 1 != next.value || current.suit.color == next.suit.color) return true
        current = next
    }
}

/**
 *  Checks if list is in order and alternating color
 */
fun List<Card>.inOrderAndAltColor(): Boolean = !this.notInOrderOrAltColor()

/**
 *  Checks if current network, if any, has a valid internet connect.
 */
fun NetworkCapabilities?.isNetworkCapabilitiesValid(): Boolean = when {
    this == null -> false
    hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
    hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
    (hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
    hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) -> true
    else -> false
}

private const val PASS_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\p{P}|.*\\p{S}).{6,}\$"
private val profanityList = listOf(
    "admin", "anal", "anus", "ballsack", "blowjob", "blow job", "boner", "clitoris", "cock", "cunt",
    "dick", "dildo", "dyke", "fag", "fuck", "jizz", "labia", "muff", "nigger", "nigga", "penis",
    "piss", "pussy", "scrotum", "sex", "shit", "slut", "smegma", "spunk", "twat", "vagina", "wank",
    "whore"
)

/**
 *  Checks if entered username contains any of the string from [profanityList].
 */
fun String.isValidUsername(): Boolean {
    if (this.isBlank()) return false
    profanityList.forEach { if (this.contains(it, ignoreCase = true)) return false }
    return true
}

/**
 *  Uses pre-made [Patterns.EMAIL_ADDRESS] to check if email is valid.
 */
fun String.isValidEmail(): Boolean {
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 *  Uses custom regex [PASS_PATTERN] which requires at least 1 digit, upper, lower, and symbol and
 *  must be a size of at least 6.
 */
fun String.isValidPassword(): Boolean {
    return this.isNotBlank() && Pattern.compile(PASS_PATTERN).matcher(this).matches()
}

private const val UTC_MINUS7 = 25200
private const val DAY_IN_SECONDS = 86400

/**
 *  Returns [Long] from this [Date] that represents the end of day today using UTC-7 as timezone.
 */
fun Date.endOfDay(): Long {
    val dateNowSeconds = this.time / 1000
    val timeIntoDay = (dateNowSeconds - UTC_MINUS7) % DAY_IN_SECONDS
    return (dateNowSeconds - timeIntoDay + DAY_IN_SECONDS) * 1000
}