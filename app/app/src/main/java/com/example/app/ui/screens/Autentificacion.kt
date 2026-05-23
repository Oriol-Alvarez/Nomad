package com.example.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.app.R
import com.example.app.Routes
import com.example.app.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutentificacionScreen(
    navController: NavHostController,
    vm: AuthViewModel = viewModel(),
    onUserDataSaved: (String, String, Boolean) -> Unit
) {
    var showResetPasswordDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            CustomHeader(
                title = stringResource(id = R.string.auth_titulo),
                subtitle = stringResource(id = R.string.auth_subtitulo)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (vm.isLoginMode) stringResource(id = R.string.auth_login).uppercase() 
                                   else stringResource(id = R.string.auth_signup).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // --- EMAIL ---
                        OutlinedTextField(
                            value = vm.email,
                            onValueChange = { vm.email = it; vm.errorMessage = null },
                            label = { Text(stringResource(id = R.string.auth_email)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- PASSWORD ---
                        var passwordVisibleState by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            value = vm.password,
                            onValueChange = { vm.password = it; vm.errorMessage = null },
                            label = { Text(stringResource(id = R.string.auth_password)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = if (passwordVisibleState) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passwordVisibleState) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = { passwordVisibleState = !passwordVisibleState }) {
                                    Icon(image, contentDescription = null)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true
                        )

                        // --- CAMPOS EXTRA SOLO PARA REGISTRO (T4.1) ---
                        if (!vm.isLoginMode) {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = vm.username,
                                onValueChange = { vm.username = it; vm.errorMessage = null },
                                label = { Text(stringResource(id = R.string.auth_username)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            
                            SelectorFechaModular(
                                label = stringResource(id = R.string.auth_birthdate),
                                fechaSeleccionada = vm.birthdate,
                                onFechaElegida = { 
                                    vm.birthdate = it
                                    vm.errorMessage = null 
                                },
                                fechaMaxima = System.currentTimeMillis()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Dirección (T4.1)
                            OutlinedTextField(
                                value = vm.address,
                                onValueChange = { vm.address = it },
                                label = { Text("Dirección") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // País (T4.1)
                            OutlinedTextField(
                                value = vm.country,
                                onValueChange = { vm.country = it },
                                label = { Text("País") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = { Icon(Icons.Default.Public, contentDescription = null) },
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Teléfono (T4.1)
                            OutlinedTextField(
                                value = vm.phoneNumber,
                                onValueChange = { vm.phoneNumber = it },
                                label = { Text("Teléfono") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Aceptar Emails (T4.1)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = vm.acceptEmails,
                                    onCheckedChange = { vm.acceptEmails = it }
                                )
                                Text(
                                    text = "Acepto recibir correos electrónicos",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            // --- TÉRMINOS Y CONDICIONES ---
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = vm.acceptTerms,
                                    onCheckedChange = { vm.acceptTerms = it }
                                )
                                val annotatedString = buildAnnotatedString {
                                    append("Acepto los ")
                                    pushLink(
                                        LinkAnnotation.Clickable(
                                            tag = "terms",
                                            styles = TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)),
                                            linkInteractionListener = {
                                                navController.navigate(Routes.TERMINOS_CONDICIONES)
                                            }
                                        )
                                    )
                                    append("términos y condiciones")
                                    pop()
                                }
                                Text(
                                    text = annotatedString,
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                                )
                            }
                        } else {
                            TextButton(
                                onClick = { showResetPasswordDialog = true },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.auth_olvido_pass),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        val error = vm.errorMessage
                        if (error != null) {
                            Text(
                                text = error.asString(),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        if (vm.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(40.dp))
                        } else {
                            Button(
                                onClick = { vm.onAuthAction(navController, onUserDataSaved) },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Text(
                                    text = if (vm.isLoginMode) stringResource(id = R.string.auth_login).uppercase() 
                                           else stringResource(id = R.string.auth_signup).uppercase(),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = { 
                                vm.isLoginMode = !vm.isLoginMode 
                                vm.errorMessage = null
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                text = if (vm.isLoginMode) stringResource(id = R.string.auth_no_account).uppercase() 
                                       else stringResource(id = R.string.auth_have_account).uppercase()
                            )
                        }
                    }
                }
            }
        }
    }

    if (showResetPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showResetPasswordDialog = false },
            title = { Text(stringResource(id = R.string.auth_recuperar_titulo)) },
            text = {
                Column {
                    Text(stringResource(id = R.string.auth_recuperar_msg))
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text(stringResource(id = R.string.auth_email)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.resetPassword(
                            email = resetEmail,
                            onSuccess = {
                                Toast.makeText(context, "Enlace enviado a $resetEmail", Toast.LENGTH_LONG).show()
                                showResetPasswordDialog = false
                            },
                            onError = { errorText ->
                                val msg = errorText.asString(context)
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                ) { Text(stringResource(id = R.string.auth_recuperar_btn)) }
            },
            dismissButton = {
                TextButton(onClick = { showResetPasswordDialog = false }) {
                    Text(stringResource(id = R.string.act_cancelar))
                }
            }
        )
    }
}
