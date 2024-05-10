package com.heyzeusv.solitaire.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.heyzeusv.solitaire.GameScreen
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.NavScreens
import com.heyzeusv.solitaire.util.PreviewUtil
import kotlinx.coroutines.delay

/**
 *  First screen that user sees in order to give Settings to load correct game and account. Uses
 *  [navController] to navigate to [GameScreen]. [vm] is used to connect to Firebase Auth.
 */
@Composable
fun SplashScreen(
    navController: NavHostController,
    vm: SplashViewModel = hiltViewModel()
) {
    val showError by vm.showError.collectAsState()

    SplashScreen(
        onAppStart = { vm.onAppStart { navController.navigate(NavScreens.Game.route) } },
        navigateOnError = { navController.navigate(NavScreens.Splash.route) },
        showError = showError
    )
}

/**
 *  First screen that user sees in order to give Settings to load correct game and account.
 *  [onAppStart] is ran after a short delay, which either runs successfully and navigates user to
 *  [GameScreen] or throws an error that is passed to here through [showError].
 */
@Composable
fun SplashScreen(
    onAppStart: () -> Unit,
    navigateOnError: () -> Unit,
    showError: Boolean
) {
    LaunchedEffect(key1 = Unit) {
        val delay = (1500L..2500L).random()
        delay(delay)
        onAppStart()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showError) {
            Text(
                text = stringResource(R.string.splash_error),
                color = MaterialTheme.colorScheme.onSurface
            )
            Button(onClick = { navigateOnError() }) {
                Text(text = stringResource(R.string.splash_error_button))
            }
        } else {
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
        }
    }
}

@Preview
@Composable
fun SplashScreenErrorPreview() {
    PreviewUtil().apply {
        Preview {
            SplashScreen(onAppStart = { }, navigateOnError = { }, showError = true)
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    PreviewUtil().apply {
        Preview {
            SplashScreen(onAppStart = { }, navigateOnError = { }, showError = false)
        }
    }
}