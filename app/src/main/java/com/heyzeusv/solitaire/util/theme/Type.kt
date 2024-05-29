package com.heyzeusv.solitaire.util.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.heyzeusv.solitaire.menu.RulesMenu

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val Typography.scoreboardText: TextStyle
    get() =  TextStyle(
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = FontFamily.SansSerif,
        letterSpacing = 0.0.sp,
        textAlign = TextAlign.Center,
        lineHeight = 32.0.sp
    )

/**
 *  @param color Text color to use.
 *
 *  @return Text style for Pile name in [RulesMenu].
 */
@Composable
fun pileTitle(color: Color): TextStyle {
    return MaterialTheme.typography.titleLarge.copy(
        color = color,
        fontWeight = FontWeight.Bold,
        textDecoration = TextDecoration.Underline,
    )
}