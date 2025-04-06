package apc.appcradle.radioappcradle

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import apc.appcradle.radioappcradle.first_screen.ScreenRadioOnline
import apc.appcradle.radioappcradle.second_screen.ScreenLocalPlayer
import apc.appcradle.radioappcradle.ui.theme.RadioAppCradleTheme
import apc.appcradle.radioappcradle.ui.theme.Typography
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RadioAppCradleTheme {
                Surface {
                    MyApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MyApp() {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val viewModel: MainViewModel = koinViewModel()
    val pages = listOf("Online Radio", "Local Music")
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "AppCradle Audio player",style = Typography.h1) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(paddingValues)
        ) {
            SecondaryTabRow(
                selectedTabIndex = pagerState.currentPage
            ) {
                pages.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(title,style = Typography.labels) })
                }
            }
            HorizontalPager(
                state = pagerState,
            ) { page ->
                when (page) {
                    0 -> ScreenRadioOnline(viewModel = viewModel)
                    1 -> ScreenLocalPlayer(viewModel = viewModel)
                }
            }
        }
    }
}

