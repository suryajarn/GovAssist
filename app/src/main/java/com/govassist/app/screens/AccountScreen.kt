package com.govassist.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.govassist.app.components.GovAssistButton
import com.govassist.app.components.GovAssistOutlinedButton
import com.govassist.app.components.ScreenHeader
import com.govassist.app.ui.theme.SurfaceWhite
import com.govassist.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun AccountScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    fun showMessage(message: String) {
        coroutineScope.launch { snackbarHostState.showSnackbar(message) }
    }

    Scaffold(
        containerColor = SurfaceWhite,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            ScreenHeader(title = "Sign in to GovAssist", onBack = onBack)

            Text(
                text = "Signing in lets GovAssist save your progress on government forms.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email or username") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Email or username input field" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Password input field" }
            )

            Spacer(modifier = Modifier.height(28.dp))

            GovAssistButton(
                text = "Sign In",
                onClick = { showMessage("Sign-in functionality coming soon.") },
                icon = Icons.Filled.Login
            )

            Spacer(modifier = Modifier.height(14.dp))

            GovAssistOutlinedButton(
                text = "Create account",
                onClick = { showMessage("Account creation coming soon.") }
            )

            Spacer(modifier = Modifier.height(14.dp))

            GovAssistOutlinedButton(
                text = "Forgot password",
                onClick = { showMessage("Password recovery coming soon.") }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

