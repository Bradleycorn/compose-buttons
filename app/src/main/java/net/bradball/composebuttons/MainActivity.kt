package net.bradball.composebuttons

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.bradball.composebuttons.ui.theme.ComposeButtonsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeButtonsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ButtonsDemo()
                }
            }
        }
    }
}


/**
 * The primary content.
 *
 * Renders 3 rows of buttons, one for each of the Loading Indicator types.
 */
@Composable
fun ButtonsDemo() {
    Column(modifier = Modifier.fillMaxWidth()) {
        ButtonRow(type = LoadingIndicatorTypes.Pulsing)

        Spacer(modifier = Modifier.height(36.dp))

        ButtonRow(type = LoadingIndicatorTypes.Flashing)

        Spacer(modifier = Modifier.height(36.dp))

        ButtonRow(type = LoadingIndicatorTypes.Bouncing)
    }
}

/**
 * Renders a single "row" of 2 buttons, one solid and one outlined,
 * that use the passed in loading indicator style when clicked.
 * It also includes a title for the row to indicate which type of
 * loading indicator the buttons will use.
 *
 * @param type a [LoadingIndicatorTypes] that specifies which type of
 *  loading indicator to use on the buttons.
 */
@Composable
fun ButtonRow(type: LoadingIndicatorTypes) {

    // This is a very basic click handler setup for demo purposes
    // to show the loading dots for 5 seconds.
    // A real app would likely include a ViewModel
    // that emits a [State]<Boolean> that can be observed
    // to set the `loading` state of the button.
    var buttonLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val onButtonClicked = {
        scope.launch {
            buttonLoading = true
            delay(5000)
            buttonLoading = false
        }
    }

    Text(type.name, modifier = Modifier.padding(start = 16.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth()
    ) {
        MyButton(
            text = "Submit",
            loading = buttonLoading,
            loadingIndicatorType = type,
            onClick = { onButtonClicked() })

        MyOutlinedButton(
            text = "Submit",
            loading = buttonLoading,
            loadingIndicatorType = type,
            onClick = { onButtonClicked() })
    }
}

@Preview
@Composable
fun PreviewButtons() {
    ComposeButtonsTheme {
        ButtonsDemo()
    }
}