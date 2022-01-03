package net.bradball.composebuttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


/**
 * Sets a shape to use for all My Buttons.
 */
private val buttonShape = RoundedCornerShape(50)


/**
 * Alpha to use for disabled buttons and disabled content
 */
private const val DISABLED_BUTTON_ALPHA = 0.5f


/**
 * Sets the content padding to use on all buttons
 */
private val buttonContentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp)


/**
 * Renders a solid, filled in Button.
 * The button will have a solid background color with text on top.
 *
 * @param text The text to show on the button.
 * @param onClick A callback that is invoked when the button is clicked.
 * @param enabled Whether the button can be clicked or not.
 *   When NOT enabled, the onClick() handler will NOT be called when the button is clicked.
 *   When NOT enabled, the button will use the "disabled" colors in the passed in ButtonColors.
 *   This value will be ignored (and set to false) if the loading argument is true.
 *   Defaults to true.
 * @param loading A boolean indicating if the button is in the loading state. If this is
 *   set to true, then enabled will automatically be set to false.
 *   Defaults to false.
 * @param loadingIndicatorType A [LoadingIndicatorTypes] that sets the type of indicator
 *   to use for this button.
 *   Defaults to [LoadingIndicatorTypes.Pulsing]
 */
@Composable
fun MyButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: ()->Unit = {},
    enabled: Boolean = true,
    loading: Boolean = false,
    loadingIndicatorType: LoadingIndicatorTypes = LoadingIndicatorTypes.Pulsing,
) {

    // The default material button colors use a shade of gray
    // for the disabled state. These buttons instead use
    // an alpha on the primary color.
    // If you want to use a different overall button color, just
    // change the `buttonColor` variable below. Or, for even more flexibility,
    // allow the caller to pass in a `buttonColor`.

    val buttonColor = MaterialTheme.colors.primary
    val textColor = contentColorFor(backgroundColor = buttonColor)

    val colors = ButtonDefaults.buttonColors(
        backgroundColor = buttonColor,
        contentColor = textColor,
        disabledBackgroundColor = buttonColor.copy(alpha = DISABLED_BUTTON_ALPHA),
        disabledContentColor =  textColor.copy(alpha = DISABLED_BUTTON_ALPHA)
    )

    Button(
        modifier = modifier,
        colors = colors,
        shape = buttonShape,
        contentPadding = buttonContentPadding,
        enabled = enabled && !loading,
        onClick = onClick) {

        MyButtonContent(
            text = text,
            loading = loading,
            loadingIndicatorType = loadingIndicatorType)
    }
}


/**
 * Renders a outlined Button.
 * The button will have a transparent background with
 * a colored border and text in the center.
 *
 * @param text The text to show on the button.
 * @param onClick A callback that is invoked when the button is clicked.
 * @param enabled Whether the button can be clicked or not.
 *   When NOT enabled, the onClick() handler will NOT be called when the button is clicked.
 *   When NOT enabled, the button will use the "disabled" colors in the passed in ButtonColors.
 *   This value will be ignored (and set to false) if the loading argument is true.
 *   Defaults to true.
 * @param loading A boolean indicating if the button is in the loading state. If this is
 *   set to true, then enabled will automatically be set to false.
 *   Defaults to false.
 * @param loadingIndicatorType A [LoadingIndicatorTypes] that sets the type of indicator
 *   to use for this button.
 *   Defaults to [LoadingIndicatorTypes.Pulsing]
 */
@Composable
fun MyOutlinedButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: ()->Unit = {},
    enabled: Boolean = true,
    loading: Boolean = false,
    loadingIndicatorType: LoadingIndicatorTypes = LoadingIndicatorTypes.Pulsing,
) {
    val colors = ButtonDefaults.outlinedButtonColors()

    // Set the border color using the button content color
    val borderColor by animateColorAsState(targetValue = colors.contentColor(enabled = enabled).value)

    OutlinedButton(
        enabled = enabled,
        contentPadding = buttonContentPadding,
        shape = buttonShape,
        border = BorderStroke(2.dp, borderColor),
        modifier = modifier,
        colors = colors,
        onClick = onClick) {

        MyButtonContent(
            text = text,
            loading = loading,
            loadingIndicatorType = loadingIndicatorType)
    }
}

/**
 * Renders the content in a Solid or Outlined button.
 *
 * @param text The text to show on the button.
 * @param loading Whether to show the loading indicator.
 *   If true, the text will still be rendered, but will be transparent/invisible.
 * @param loadingIndicatorType A [LoadingIndicatorTypes] that sets the type of indicator
 *   to use for this button.
 */
@Composable
private fun MyButtonContent(
    text: String,
    loading: Boolean,
    loadingIndicatorType: LoadingIndicatorTypes) {

    // TODO: this could probably be done better with something like
    //  a custom layout (so we could measure the text and then just
    //  not place it if we don't want to render it) or other tools.

    // Many times, a button's width will be determined
    // by the width of the text content. If the button
    // enters the "loading" state, we want it to keep the
    // width of it's text content.
    // So to achieve that, we'll still draw the text, and just
    // make it transparent when in the "loading" state, and
    // render the loading indicator over the top of it.

    // Set the text color.
    // using "Unspecified" as the default will cause it to use the button content color
    // which is what we want.
    val textColor = when {
        loading -> Color.Transparent
        else -> Color.Unspecified
    }

    // Wrap the text and loading indicator in a box, so that they
    // are drawn over the top of each other.
    Box(contentAlignment = Alignment.Center) {
        Text(text = text, color = textColor)

        // Draw the loading indicator when necessary.
        if (loading) {
            LoadingIndicator(type = loadingIndicatorType)
        }
    }
}