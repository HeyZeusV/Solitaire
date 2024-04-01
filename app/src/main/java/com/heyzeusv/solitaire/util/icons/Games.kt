package com.heyzeusv.solitaire.util.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Filled.Games: ImageVector
    get() {
        if (mGames != null) {
            return mGames !!
        }
        mGames = materialIcon(name = "Filled.Games") {
            materialPath {
                // M15,7.5V2H9v5.5l3,3 3,-3z
                moveTo(15f, 7.5f)
                verticalLineTo(2f)
                horizontalLineTo(9f)
                verticalLineToRelative(5.5f)
                lineToRelative(3f, 3f)
                lineToRelative(3f, -3f)
                close()
                // M7.5,9H2v6h5.5l3,-3 -3,-3z
                moveTo(7.5f, 9f)
                horizontalLineTo(2f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(5.5f)
                lineToRelative(3f, -3f)
                lineToRelative(-3f, -3f)
                close()
                // M9,16.5V22h6v-5.5l-3,-3 -3,3z
                moveTo(9f, 16.5f)
                verticalLineTo(22f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(-5.5f)
                lineToRelative(-3f, -3f)
                lineToRelative(-3f, 3f)
                close()
                // M16.5,9l-3,3 3,3H22V9h-5.5z
                moveTo(16.5f, 9f)
                lineToRelative(-3f, 3f)
                lineToRelative(3f, 3f)
                horizontalLineTo(22f)
                verticalLineTo(9f)
                horizontalLineToRelative(-5.5f)
                close()
            }
        }
        return mGames!!
    }

private var mGames: ImageVector? = null