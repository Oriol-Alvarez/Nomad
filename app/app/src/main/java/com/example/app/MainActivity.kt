package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.PermissionChecker
import androidx.navigation.compose.rememberNavController
import com.example.app.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // contenido full screen

        setContent {
            AppTheme {
                val navController = rememberNavController()
                NavGraph(navController)
            }
        }
    }
}
