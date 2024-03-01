package com.heyzeusv.solitaire.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.solitaire.util.theme.PreviewBG
import com.heyzeusv.solitaire.util.theme.SolitaireTheme

/**
 *  Helper function for Composable Previews. Gives semi-transparent background to previews to make
 *  it easier to see solo components
 */
@Composable
fun SolitairePreview(content: @Composable () -> Unit) {
    SolitaireTheme {
        Box(Modifier.fillMaxWidth().background(PreviewBG)) {
            content()
        }
    }
}

/**
 *  A MultiPreview annotation for displaying a [Composable] method using various devices.
 */
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@Preview(name = "NEXUS5_480_1080_1920", device = Devices.NEXUS_5) // 2013
@Preview(name = "PIXEL3_440_1080_2160", device = Devices.PIXEL_3)
@Preview(name = "PIXEL3XL_560_1440_2960", device = Devices.PIXEL_3_XL)
@Preview(name = "PIXEL3A_440_1080_2200", device = Devices.PIXEL_3A)
@Preview(name = "PIXEL3AXL_400_1080_2160", device = Devices.PIXEL_3A_XL)
@Preview(name = "PIXEL4_440_1080_2280", device = Devices.PIXEL_4)
@Preview(name = "PIXEL4XL_560_1440_3040", device = Devices.PIXEL_4_XL)
@Preview(name = "PIXEL4A_440_1080_2340", device = Devices.PIXEL_4A)
@Preview(name = "PIXEL5_440_1080_2340", device = Devices.PIXEL_5)
@Preview(name = "PIXEL6_420_1080_2400", device = Devices.PIXEL_6)
@Preview(name = "PIXEL6PRO_560_1440_3120", device = Devices.PIXEL_6_PRO)
@Preview(name = "PIXEL6A_420_1080_2400", device = Devices.PIXEL_6A)
@Preview(name = "PIXEL7_420_1080_2400", device = Devices.PIXEL_7)
@Preview(name = "PIXEL7PRO_560_1440_3120", device = Devices.PIXEL_7_PRO)
annotation class PreviewDevices