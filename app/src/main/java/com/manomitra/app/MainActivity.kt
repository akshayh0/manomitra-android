package com.manomitra.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.manomitra.app.core.theme.ManomitraTheme
import com.manomitra.app.core.navigation.AppNavHost
import com.manomitra.app.core.settings.AppSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.manomitra.app.feature.chat.AIConfig.init(this)
        enableEdgeToEdge()
        setContent {
            var themeState by remember { mutableStateOf(AppSettings.getAppTheme(applicationContext)) }

            DisposableEffect(Unit) {
                val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (key == AppSettings.KEY_THEME) {
                        themeState = AppSettings.getAppTheme(applicationContext)
                    }
                }
                val prefs = getSharedPreferences("manomitra_settings", MODE_PRIVATE)
                prefs.registerOnSharedPreferenceChangeListener(listener)
                onDispose {
                    prefs.unregisterOnSharedPreferenceChangeListener(listener)
                }
            }

            val isDarkTheme = when (themeState.lowercase()) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            ManomitraTheme(darkTheme = isDarkTheme) {
                AppNavHost()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ManomitraTheme {
        Greeting("Android")
    }
}