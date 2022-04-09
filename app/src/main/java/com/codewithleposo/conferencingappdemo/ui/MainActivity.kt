package com.codewithleposo.conferencingappdemo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.codewithleposo.conferencingappdemo.screens.NavGraphs
import com.codewithleposo.conferencingappdemo.ui.theme.ConferencingAppDemoTheme
import com.codewithleposo.conferencingappdemo.ui.theme.Purple700
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.rememberNavHostEngine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConferencingAppDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val navController = rememberNavController()
                    val navHostEngine = rememberNavHostEngine()

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        "Video Conferencing App",
                                        color = Color.White,
                                    )
                                },
                                backgroundColor = Purple700,
                                elevation = 5.dp
                            )
                        }
                    ) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            navController = navController,
                            engine = navHostEngine
                        )
                    }
                }
            }
        }
    }
}