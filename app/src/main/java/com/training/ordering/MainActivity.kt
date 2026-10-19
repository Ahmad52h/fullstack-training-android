package com.training.ordering

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.training.ordering.ui.screen.MainScreen
import com.training.ordering.ui.theme.FullStackOrderingAppTheme
import com.training.ordering.ui.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FullStackOrderingAppTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}