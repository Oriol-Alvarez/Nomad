package com.example.app.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.R
import com.example.app.ui.theme.AppTheme

@Composable
fun TerminosCondicionesScreen(navController: NavHostController) {
    val context = LocalContext.current

    // Acceso a SharedPreferences para persistir la aceptación de términos localmente
    val sharedPreferences = remember {
        context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
    }

    // Estado que determina si el usuario ya aceptó previamente los términos
    var accepted by remember {
        mutableStateOf(sharedPreferences.getBoolean("terms_accepted", false))
    }

    // Estado volátil para el control del Checkbox de validación
    var checkboxChecked by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Habilita el desplazamiento si el texto legal es extenso
        ) {

            // Componente de cabecera reutilizable con título y fecha de actualización
            CustomHeader(
                stringResource(id = R.string.terminos_titulo),
                stringResource(id = R.string.terminos_actualizacion),
                true
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                // Tarjeta contenedora del cuerpo legal del contrato
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = stringResource(id = R.string.terminos_cuerpo_titulo),
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(id = R.string.terminos_cuerpo_texto)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bloque condicional: Si no se han aceptado los términos, se muestra el formulario de validación
                if (!accepted) {

                    // Fila de interacción para el consentimiento del usuario
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkboxChecked,
                            onCheckedChange = { checkboxChecked = it }
                        )

                        Text(stringResource(id = R.string.terminos_aceptar_checkbox))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Acciones de navegación y confirmación
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        OutlinedButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Text(stringResource(id = R.string.terminos_rechazar_btn))
                        }

                        Button(
                            onClick = {
                                // Persistencia del cambio en el almacenamiento local
                                sharedPreferences.edit().putBoolean("terms_accepted", true).apply()
                                accepted = true
                            },
                            enabled = checkboxChecked // El botón solo es accionable si el checkbox está marcado
                        ) {
                            Text(stringResource(id = R.string.terminos_aceptar_btn))
                        }
                    }

                } else {

                    // Estado visual que se muestra una vez que el usuario ya ha aceptado los términos
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = stringResource(id = R.string.terminos_aceptados_titulo),
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(id = R.string.terminos_aceptados_msg)
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun TerminosYcondicionesPreviewDark() {
    AppTheme {
        TerminosCondicionesScreen(
            navController = rememberNavController()
        )
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun TerminosYcondicionesPreviewLight() {
    AppTheme {
        TerminosCondicionesScreen(
            navController = rememberNavController()
        )
    }
}