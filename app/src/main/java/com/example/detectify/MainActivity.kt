package com.example.detectify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.example.detectify.ui.NavGraph
import com.example.detectify.ui.theme.DetectifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DetectifyTheme {
                Surface {
                    NavGraph()     // <-- FIXED
                }
            }
        }
    }
}
