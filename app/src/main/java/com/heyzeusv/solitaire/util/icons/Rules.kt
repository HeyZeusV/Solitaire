package com.heyzeusv.solitaire.util.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Filled.Rules: ImageVector
    get() {
        if (mRules != null) {
            return mRules!!
        }
        mRules = materialIcon(name = "Filled.Rules") {
            materialPath {
                // M16.54,11L13,7.46l1.41,-1.41l2.12,2.12l4.24,-4.24l1.41,1.41L16.54,11z
                moveTo(16.54f, 11f)
                lineTo(13f, 7.46f)
                lineToRelative(1.41f, -1.41f)
                lineToRelative(2.12f, 2.12f)
                lineToRelative(4.24f, -4.24f)
                lineToRelative(1.41f, 1.41f)
                lineTo(16.54f, 11f)
                close()
                // M11,7H2v2h9V7z
                moveTo(11f, 7f)
                horizontalLineTo(2f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(9f)
                verticalLineTo(7f)
                close()
                // M21,13.41L19.59,12L17,14.59L14.41,12L13,13.41L15.59,16L13,18.59L14.41,20
                moveTo(21f, 13.41f)
                lineTo(19.59f, 12f)
                lineTo(17f, 14.59f)
                lineTo(14.41f, 12f)
                lineTo(13f, 13.41f)
                lineTo(15.59f, 16f)
                lineTo(13f, 18.59f)
                lineTo(14.41f, 20f)
                // L17,17.41L19.59,20L21,18.59L18.41,16L21,13.41z
                lineTo(17f, 17.41f)
                lineTo(19.59f, 20f)
                lineTo(21f, 18.59f)
                lineTo(18.41f, 16f)
                lineTo(21f, 13.41f)
                close()
                // M11,15H2v2h9V15z
                moveTo(11f, 15f)
                horizontalLineTo(2f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(9f)
                verticalLineTo(15f)
                close()
            }
        }
        return mRules!!
    }

private var mRules: ImageVector? = null