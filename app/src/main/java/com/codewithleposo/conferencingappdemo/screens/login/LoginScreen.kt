package com.codewithleposo.conferencingappdemo.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.codewithleposo.conferencingappdemo.screens.destinations.CallScreenDestination
import com.codewithleposo.conferencingappdemo.ui.theme.Purple200
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(start = true)
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Enter name in order to join the room")

        Spacer(modifier = Modifier.height(16.dp))

        var text by remember {
            mutableStateOf("")
        }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = {
                text = it
            },
            label = {
                Text(text = "Enter name")
            },
            placeholder = {
                Text(text = "Doe John")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                navigator.navigate(CallScreenDestination(text))
            },
            colors = ButtonDefaults.buttonColors(Purple200)
        ) {
            Text(
                modifier = Modifier
                    .padding(5.dp),
                text = "Join room",
                color = Color.White
            )
        }
    }
}