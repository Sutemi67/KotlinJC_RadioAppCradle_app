package apc.appcradle.radioappcradle.app.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import apc.appcradle.radioappcradle.ui.theme.Typography

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    textStyle: TextStyle = Typography.buttonsText
) {
    Button(
        onClick = { onClick() },
        modifier = modifier,
        enabled = isEnabled
    ) {
        Text(text = text, style = textStyle)
    }
}