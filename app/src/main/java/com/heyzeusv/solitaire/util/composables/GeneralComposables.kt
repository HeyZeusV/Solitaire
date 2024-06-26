package com.heyzeusv.solitaire.util.composables

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Transition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.service.AccountStatus
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.getContentColor
import com.heyzeusv.solitaire.util.icons.Games
import com.heyzeusv.solitaire.util.theme.Purple80
import com.heyzeusv.solitaire.util.theme.TransparentDarkBG
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 *  Custom Button Composable that uses [Box] rather than standard [OutlinedButton] to allow for
 *  easier animations using [Transition]. This custom Button is very bare bones, so it requires
 *  [modifier] to provide additional styling. Displays given [iconPainter] or [iconImgVector] with
 *  content description of [iconContentDesc] and given [buttonText]. [enabled] is used to determine
 *  which colors from [buttonColors] should be used and if [onClick] is ran when Button is clicked.
 */
@Composable
fun BaseButton(
    modifier: Modifier,
    iconContentDesc: String,
    buttonText: String,
    iconPainter: Painter? = null,
    iconImgVector: ImageVector? = null,
    enabled: Boolean = true,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        contentColor = Color.White
    ),
    onClick: () -> Unit
) {
    val contentColor = buttonColors.getContentColor(enabled)

    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(color = contentColor),
            enabled = enabled,
            onClick = onClick
        ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.buttonPaddingHorizontal)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            iconPainter?.let {
                Icon(
                    painter = iconPainter,
                    contentDescription = iconContentDesc,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    tint = contentColor
                )
            }
            iconImgVector?.let {
                Icon(
                    imageVector = iconImgVector,
                    contentDescription = iconContentDesc,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    tint = contentColor
                )
            }
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = buttonText,
                color = contentColor,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun AccountStatusIndicator(accountStatus: AccountStatus) {
    if (accountStatus !is AccountStatus.Idle) {
        BackHandler { }
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10f),
            color = TransparentDarkBG
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(200.dp),
                    color = Purple80,
                    strokeWidth = 10.dp
                )
                Text(
                    text = stringResource(accountStatus.message),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

/**
 *  Taken from deprecated Accompanist Pager library.
 *  [https://github.com/google/accompanist/blob/main/pager-indicators/src/main/java/com/google/accompanist/pager/PagerIndicator.kt]
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagerIndicator(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier,
    pageIndexMapping: (Int) -> Int = { it },
    activeColor: Color = LocalContentColor.current.copy(alpha = 0.9f),
    inactiveColor: Color = activeColor.copy(alpha = 0.38f),
    indicatorWidth: Dp = 8.dp,
    indicatorHeight: Dp = indicatorWidth,
    spacing: Dp = indicatorWidth,
    indicatorShape: RoundedCornerShape = CircleShape,
) {
    val indicatorWidthPx = LocalDensity.current.run { indicatorWidth.roundToPx() }
    val spacingPx = LocalDensity.current.run { spacing.roundToPx() }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val indicatorModifier = Modifier
                .size(width = indicatorWidth, height = indicatorHeight)
                .background(color = inactiveColor, shape = indicatorShape)

            repeat(pageCount) {
                Box(indicatorModifier)
            }
        }
        Box(
            Modifier
                .offset {
                    val position = pageIndexMapping(pagerState.currentPage)
                    val offset = pagerState.currentPageOffsetFraction
                    val next = pageIndexMapping(pagerState.currentPage + offset.sign.toInt())
                    val scrollPosition = ((next - position) * offset.absoluteValue + position)
                        .coerceIn(
                            0f,
                            (pageCount - 1)
                                .coerceAtLeast(0)
                                .toFloat()
                        )
                    IntOffset(
                        x = ((spacingPx + indicatorWidthPx) * scrollPosition).toInt(),
                        y = 0
                    )
                }
                .size(width = indicatorWidth, height = indicatorHeight)
                .then(
                    if (pageCount > 0) Modifier.background(
                        color = activeColor,
                        shape = indicatorShape,
                    )
                    else Modifier
                )
        )
    }
}

@Preview
@Composable
fun BaseButtonPreview() {
    PreviewUtil().apply {
        Preview {
            Row {
                BaseButton(
                    modifier = Modifier,
                    iconContentDesc = "",
                    buttonText = "Enabled",
                    iconPainter = painterResource(R.drawable.button_reset),
                ) { }
                BaseButton(
                    modifier = Modifier,
                    iconContentDesc = "",
                    buttonText = "Disabled",
                    iconImgVector = Icons.Filled.Games,
                    enabled = false
                ) { }
            }
        }
    }
}