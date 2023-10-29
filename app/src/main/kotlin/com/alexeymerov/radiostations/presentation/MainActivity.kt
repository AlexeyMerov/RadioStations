package com.alexeymerov.radiostations.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.alexeymerov.radiostations.common.collectWhenStarted
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewState
import com.alexeymerov.radiostations.presentation.navigation.MainNavGraph
import com.alexeymerov.radiostations.presentation.theme.StationsAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var viewState by mutableStateOf(viewModel.initialState)

        splashScreen.setKeepOnScreenCondition { viewState == ViewState.Loading }

        viewModel.viewState.collectWhenStarted(this) { viewState = it }

        enableEdgeToEdge()
        setContent {
            val themeState = when (val state = viewState) {
                is ViewState.Loaded -> state.themeState
                else -> return@setContent // not sure about the best practice
            }
            StationsAppTheme(themeState) {
                MainNavGraph()
            }
        }
    }

}