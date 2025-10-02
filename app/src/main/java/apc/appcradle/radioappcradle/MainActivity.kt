package apc.appcradle.radioappcradle

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.Surface
import apc.appcradle.radioappcradle.presentation.ui.PlayerMainHost
import apc.appcradle.radioappcradle.presentation.ui.theme.RadioAppCradleTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RadioAppCradleTheme {
                Surface {
                    PlayerMainHost()
                }
            }
        }
    }
}