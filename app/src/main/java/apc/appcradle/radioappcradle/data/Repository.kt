package apc.appcradle.radioappcradle.data

import android.content.Context
import androidx.core.content.ContextCompat.getString
import apc.appcradle.radioappcradle.R
import apc.appcradle.radioappcradle.domain.RadioStation

class Repository(
    context: Context
) {
    val stationList = listOf<RadioStation>(
        RadioStation("rock", getString(context, R.string.radio_rock_url)),
        RadioStation("lofi", getString(context, R.string.radio_lofi_url))
    )
}