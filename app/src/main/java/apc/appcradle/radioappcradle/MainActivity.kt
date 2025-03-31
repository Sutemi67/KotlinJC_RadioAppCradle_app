package apc.appcradle.radioappcradle

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import apc.appcradle.radioappcradle.first_screen.FirstScreen
import apc.appcradle.radioappcradle.second_screen.SecondScreen
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MyApp() {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val viewModel: MainViewModel = koinViewModel()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "My Music App") })
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> FirstScreen(viewModel = viewModel)
                1 -> SecondScreen(viewModel = viewModel)
            }
        }
    }
}


