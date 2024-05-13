package com.heyzeusv.solitaire.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.heyzeusv.solitaire.GameScreen
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.NavScreens
import com.heyzeusv.solitaire.util.PreviewUtil
import kotlinx.coroutines.delay

/**
 *  First screen that user sees in order to give Settings to load correct game. Uses
 *  [navController] to navigate to [GameScreen].
 */
@Composable
fun SplashScreen(
    navController: NavHostController
) {
    SplashScreen {
        navController.navigate(NavScreens.Game.route)
    }
}

/**
 *  First screen that user sees in order to give Settings to load correct game. [navigate] is
 *  called to a short delay and navigates user to [GameScreen].
 */
@Composable
fun SplashScreen(
    navigate: () -> Unit = { }
) {
    LaunchedEffect(key1 = Unit) {
        delay((1500L..2500L).random())
        navigate()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.align(Alignment.Center)) {
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
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    PreviewUtil().apply {
        Preview {
            SplashScreen()
        }
    }
}