package com.heyzeusv.solitaire.ui.toolbar.menu

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.toSize
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.theme.ARevealBG
import com.heyzeusv.solitaire.util.theme.Pink80
import com.heyzeusv.solitaire.util.theme.Purple40
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 *  Composable that displays About Menu Screen where users can see info about app. [onBackPress]
 *  navigates user away from [AboutMenu].
 */
@Composable
fun AboutMenu(
    onBackPress: () -> Unit
) {
    MenuScreen(
        menu = MenuState.About,
        modifier = Modifier.testTag("About Menu"),
        onBackPress = { onBackPress() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.aColumnSpacedBy)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Image(
                    painter = painterResource(R.mipmap.ic_launcher_background),
                    contentDescription = null,
                    modifier = Modifier.shadow(
                        elevation = dimensionResource(R.dimen.aImageElevation),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.aImageRoundSize))
                    )
                )
                Image(
                    painter = painterResource(R.mipmap.ic_launcher_foreground),
                    contentDescription = stringResource(R.string.app_name)
                )
            }
            CenterAlignText(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            CenterAlignText(text = stringResource(R.string.about_by_name))
            CenterAlignText(text = stringResource(R.string.about_version))
            ButtonRevealContent(
                buttonText = stringResource(R.string.about_changelog),
                file = "Changelog.txt"
            )
            HyperlinkText(
                text = stringResource(R.string.about_privacy_policy),
                link = stringResource(R.string.about_privacy_policy_link)
            )
            CenterAlignText(text = stringResource(R.string.about_special_thanks))
            CenterAlignText(text = stringResource(R.string.about_contact_me))
            HyperlinkText(
                text = stringResource(R.string.about_email),
                link = stringResource(R.string.about_email_link)
            )
            CenterAlignText(
                text = stringResource(R.string.about_external_icons),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            HyperlinkText(
                text = stringResource(R.string.about_icon_credit),
                link = stringResource(R.string.about_icon_credit_link)
            )
        }
    }
}

/**
 *  Button with [buttonText] that when pressed, reveals Surface containing Text with [file] as
 *  its content
 */
@Composable
fun ButtonRevealContent(buttonText: String, file: String) {
    var revealContent by remember { mutableStateOf(false) }
    var composeSize by remember { mutableStateOf(Size.Zero) }

    val fileContent = loadFile(file, LocalContext.current)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { revealContent = !revealContent },
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.aButtonPaddingBottom)),
            shape = RoundedCornerShape(dimensionResource(R.dimen.aButtonRoundSize)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple40
            )
        ) {
            Text(
                text = buttonText.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = TextUnit(1f, TextUnitType.Sp)
                )
            )
        }
        AnimatedVisibility(
            visible = revealContent,
            enter = expandVertically(
                animationSpec = tween(
                    durationMillis = integerResource(R.integer.aButtonDuration),
                    easing = LinearEasing
                ),
                initialHeight = { -composeSize.height.toInt() - 50 }
            ),
            exit = shrinkVertically(
                animationSpec = tween(
                    durationMillis = integerResource(R.integer.aButtonDuration),
                    easing = LinearEasing
                ),
                targetHeight = { -composeSize.height.toInt() - 50 }
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.aRevealHeight))
                    .onGloballyPositioned { composeSize = it.size.toSize() },
                shape = MaterialTheme.shapes.medium,
                color = ARevealBG
            ) {
                CenterAlignText(
                    text = fileContent,
                    modifier = Modifier
                        .padding(all = dimensionResource(R.dimen.aRevealPaddingAll))
                        .verticalScroll(rememberScrollState())
                        .testTag("About $file"),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

/**
 *  Text Composable with center text alignment. [text] is text to be displayed. Default [modifier]
 *  in case any additional modifiers needed. Default [style] in case a different TextStyle is needed.
 */
@Composable
fun CenterAlignText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge.copy(
        fontSize = TextUnit(
            integerResource(R.integer.aTextSize).toFloat(),
            TextUnitType.Sp
        )
    )
) {
    Text(
        text = text,
        modifier = modifier,
        textAlign = TextAlign.Center,
        style = style
    )
}

/**
 *  Creates a ClickableText with [text] that when clicked leads to [link].
 *  Modified from https://gist.github.com/stevdza-san/ff9dbec0e072d8090e1e6d16e6b73c91
 */
@Composable
fun HyperlinkText(text: String, link: String) {
    val uriHandler = LocalUriHandler.current
    val annotatedString = buildAnnotatedString {
        append(text)
        addStyle(
            style = SpanStyle(
                color = Pink80,
                textDecoration = TextDecoration.Underline
            ),
            start = 0,
            end = text.length
        )
        addStringAnnotation(
            tag = "tag",
            annotation = link,
            start = 0,
            end = text.length
        )
    }

    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.titleLarge.copy(
            fontSize = TextUnit(
                integerResource(R.integer.aTextSize).toFloat(),
                TextUnitType.Sp
            ),
            textAlign = TextAlign.Center
        ),
        onClick = { offset ->
            annotatedString
                .getStringAnnotations(
                    start = offset,
                    end = offset,
                ).firstOrNull()?.let { result -> uriHandler.openUri(result.item) }
        }
    )
}

/**
 *  Uses [context] in order to be able to use BufferedReader to open [file] in order to return
 *  its contents.
 */
private fun loadFile(file: String, context: Context): String {
    var fileText = ""
    var reader: BufferedReader? = null

    try {
        // open file and read through it
        reader = BufferedReader(InputStreamReader(context.assets.open(file)))
        fileText = reader.readLines().joinToString("\n")
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            // close reader
            reader?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return fileText
}

@Preview
@Composable
fun AboutMenuPreview() {
    SolitairePreview {
        AboutMenu(onBackPress = { })
    }
}

@Preview
@Composable
fun TextCenterAlignPreview() {
    SolitairePreview {
        CenterAlignText(text = "Text Center Align Preview")
    }
}

@Preview
@Composable
fun ButtonRevealContentPreview() {
    SolitairePreview {
        ButtonRevealContent(
            buttonText = "Button Reveal Content Preview",
            file = "Changelog.txt"
        )
    }
}

@Preview
@Composable
fun HyperlinkTextPreview() {
    SolitairePreview {
        HyperlinkText(
            text = "Hyperlink Text Preview",
            link = stringResource(R.string.about_privacy_policy_link)
        )
    }
}