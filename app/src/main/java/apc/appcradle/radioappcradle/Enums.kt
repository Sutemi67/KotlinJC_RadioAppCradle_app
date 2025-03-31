package apc.appcradle.radioappcradle

import android.os.Bundle
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand

class Enums {
    companion object {
        private const val CUSTOM_COMMAND_REWIND_ACTION_ID = "REWIND_15"
        private const val CUSTOM_COMMAND_FORWARD_ACTION_ID = "FAST_FWD_15"

        enum class NotificationPlayerCustomCommandButton(
            val customAction: String,
            val commandButton: CommandButton,
        ) {
            REWIND(
                customAction = CUSTOM_COMMAND_REWIND_ACTION_ID,
                commandButton = CommandButton.Builder()
                    .setDisplayName("Rewind")
                    .setSessionCommand(SessionCommand(CUSTOM_COMMAND_REWIND_ACTION_ID, Bundle()))
                    .setIconResId(R.drawable.previous_button)
                    .build(),
            ),
            FORWARD(
                customAction = CUSTOM_COMMAND_FORWARD_ACTION_ID,
                commandButton = CommandButton.Builder()
                    .setDisplayName("Forward")
                    .setSessionCommand(SessionCommand(CUSTOM_COMMAND_FORWARD_ACTION_ID, Bundle()))
                    .setIconResId(R.drawable.next_button)
                    .build(),
            );
        }
    }
}