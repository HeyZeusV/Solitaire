package com.heyzeusv.solitaire.util.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Filled.Stats: ImageVector
    get() {
        if (mStats != null) {
            return mStats!!
        }
        mStats = materialIcon(name = "Filled.Games") {
            materialPath {
                // M3.5,18.49l6,-6.01 4,4L22,6.92l-1.41,-1.41 -7.09,7.97 -4,-4L2,16.99z
                moveTo(3.5f, 18.49f)
                lineToRelative(6f, -6.01f)
                lineToRelative(4f, 4f)
                lineTo(22f, 6.92f)
                lineToRelative(-1.41f, -1.41f)
                lineToRelative(-7.09f, 7.97f)
                lineToRelative(-4f, -4f)
                lineTo(2f, 16.99f)
                close()
            }
        }
        return mStats!!
    }

private var mStats: ImageVector? = null