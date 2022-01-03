package net.bradball.composebuttons

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import net.bradball.composebuttons.ui.theme.ComposeButtonsTheme

enum class LoadingIndicatorTypes {
    Pulsing,
    Flashing,
    Bouncing
}

/**
 * Renders a set of dots that are animated to indicate a "loading" state.
 *
 * @param loadingIndicatorType A [LoadingIndicatorTypes] that sets the type of indicator
 *   to use for this button.
 *   Defaults to [LoadingIndicatorTypes.Pulsing]
 * @param dotSize How big to render the dots.
 *   Defaults to 12.dp
 * @param color A Color to use for the dots. If not specified, the LocalContentColor will be used.
 */
@Composable
fun LoadingIndicator(type: LoadingIndicatorTypes = LoadingIndicatorTypes.Pulsing, dotSize: Dp = 12.dp, modifier: Modifier = Modifier, color: Color = Color.Unspecified) {
    val numberOfDots = 3
    val pulseDuration = 333
    val bouncingDotHeight = 10f

    val timeBetween = pulseDuration * (numberOfDots - 1)
    val delay = pulseDuration / 2
    val transition = rememberInfiniteTransition()

    val minWidth = (dotSize * numberOfDots) + ((dotSize / 2) * (numberOfDots - 1))
    var rowModifier = modifier.widthIn(min = minWidth)
    if (type == LoadingIndicatorTypes.Bouncing) rowModifier = rowModifier.padding(top = bouncingDotHeight.dp)

    val dotColor = color.takeOrElse { LocalContentColor.current }

    Row(modifier = rowModifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        for (index in 0 until numberOfDots) {
            when (type) {
                LoadingIndicatorTypes.Pulsing -> PulsingDot(
                    dotSize = dotSize,
                    pulseDuration = pulseDuration,
                    timeBetweenPulses = timeBetween,
                    delayStart = delay * index,
                    transition = transition,
                    color = dotColor)

                LoadingIndicatorTypes.Flashing -> FlashingDot(
                    dotSize = dotSize,
                    flashDuration = pulseDuration,
                    timeBetweenFlashes = timeBetween,
                    delayStart = delay * index,
                    transition = transition,
                    color = dotColor)

                LoadingIndicatorTypes.Bouncing -> BouncingDot(
                    dotSize = dotSize,
                    bounceHeight = bouncingDotHeight,
                    bounceDuration = pulseDuration,
                    timeBetweenBounces = timeBetween,
                    delayStart = delay * index,
                    transition = transition,
                    color = dotColor)
            }
        }
    }
}


/**
 * Renders a "dot" (circle) shape with a specific color.
 *
 * @param color The Color for the dot. Defaults to the current value of LocalContentColor
 */
@Composable
fun Dot(modifier: Modifier = Modifier, color: Color = LocalContentColor.current) {
    Spacer(modifier.background(color = color, shape = CircleShape))
}

/**
 * Takes in a set of values and a duration, and returns a
 * [State]<Float> that changes the value over the course of the duration,
 * and then repeats indefinitely. The value will be moved from the min
 * to the max, and then back to the min over the course of the duration.
 *
 * This can be used to create an infinite repeatable "loop" that animates
 * a value over and over, and include a "pause" between cycles.
 *
 * @param minValue the starting value at the beginning (and end of the duration).
 * @param maxValue the value that should be output halfway through the duration.
 * @param duration how long (in milliseconds) the transistion from min to max and back to min should take.
 * @param timeBetween how long (in milliseconds) to "wait" between cycles of the duration.
 *   The total time of the repeated loop is `duration + timeBetween`
 * @param delayStart how long (in milliseconds) to wait before starting the initial transition.
 */
@Composable
private fun InfiniteTransition.dotAnimator(
    minValue: Float = 0f,
    maxValue: Float =  1f,
    duration: Int = 333,
    timeBetween: Int = 0,
    delayStart: Int = 0): State<Float> {

    val totalDuration = duration + timeBetween

    return animateFloat (
        initialValue = minValue,
        targetValue = minValue,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = totalDuration
                minValue at 0 with LinearOutSlowInEasing
                maxValue at (duration / 2) with FastOutLinearInEasing
                minValue at duration
            },
            initialStartOffset = StartOffset(delayStart)
        )
    )
}

/**
 * Renders a [Dot] that "pulses". The dot will grow and shrink
 * as well as fade in and out in a loop.
 *
 * @param dotSize The size of the dot to be rendered.
 *   Defaults to 12.dp
 * @param color The Color of the dot.
 *   Defaults to the current value of LocalContentColor.
 * @param pulseDuration how long one "pulse" of the dot should take.
 *   Defaults to 300 milliseconds.
 * @param timeBetweenPulses how long to "pause" between pulses.
 *   Defaults to 0 milliseconds (no pause).
 * @param delayStart how long to "wait" before the initial pulse.
 *   The dot will not be displayed during this time.
 *   Defaults to 0 milliseconds (no delay).
 * @param transition an [InfiniteTransition] to use for animating the dot.
 *   This is useful if you want to combine this animation with others
 */
@Composable
fun PulsingDot(
    dotSize: Dp = 12.dp,
    color: Color = LocalContentColor.current,
    pulseDuration: Int = 300,
    timeBetweenPulses: Int = 0,
    delayStart: Int = 0,
    transition: InfiniteTransition = rememberInfiniteTransition()) {

    val scale: Float by transition.dotAnimator(
        duration = pulseDuration,
        timeBetween = timeBetweenPulses,
        delayStart = delayStart)

    val alpha: Float by transition.dotAnimator(
        minValue = 0.25f,
        maxValue = 1f,
        duration = pulseDuration,
        timeBetween = timeBetweenPulses,
        delayStart = delayStart)

    Dot(modifier = Modifier
        .size(dotSize)
        .scale(scale), color = color.copy(alpha = alpha))
}

/**
 * Renders a [Dot] that "flashes". The dot will fade in and out in a loop.
 * The dot will always be visible somewhat (with very low alpha) even when fully
 * faded out.
 *
 * @param dotSize The size of the dot to be rendered.
 *   Defaults to 12.dp
 * @param color The Color of the dot.
 *   Defaults to the current value of LocalContentColor.
 * @param flashDuration how long one "flash" of the dot should take.
 *   Defaults to 300 milliseconds.
 * @param timeBetweenFlashes how long to "pause" between flashes.
 *   Defaults to 0 milliseconds (no pause).
 * @param delayStart how long to "wait" before the initial flash.
 *   Defaults to 0 milliseconds (no delay).
 * @param transition an [InfiniteTransition] to use for animating the dot.
 *   This is useful if you want to combine this animation with others
 */
@Composable
fun FlashingDot(
    dotSize: Dp = 12.dp,
    color: Color = LocalContentColor.current,
    flashDuration: Int = 300,
    timeBetweenFlashes: Int = 0,
    delayStart: Int = 0,
    transition: InfiniteTransition = rememberInfiniteTransition()) {

    val alpha: Float by transition.dotAnimator(
        minValue = 0.25f, // You could set this to 0 to fully fade out the dot
        maxValue = 1f,
        duration = flashDuration,
        timeBetween = timeBetweenFlashes,
        delayStart = delayStart)

    Dot(modifier = Modifier.size(dotSize), color = color.copy(alpha = alpha))
}

/**
 * Renders a [Dot] that "bounces" up and down.
 *
 * @param dotSize The size of the dot to be rendered.
 *   Defaults to 12.dp
 * @param color The Color of the dot.
 *   Defaults to the current value of LocalContentColor.
 * @param bounceHeight - How high (in pixels) the dot should "bounce" (move upward).
 *   Defaults to 10.
 * @param bounceDuration how long one "bounce" of the dot should take.
 *   Defaults to 300 milliseconds.
 * @param timeBetweenBounces how long to "pause" between bounces.
 *   Defaults to 0 milliseconds (no pause).
 * @param delayStart how long to "wait" before the initial bounce.
 *   Defaults to 0 milliseconds (no delay).
 * @param transition an [InfiniteTransition] to use for animating the dot.
 *   This is useful if you want to combine this animation with others
 */
@Composable
fun BouncingDot(
    dotSize: Dp = 12.dp,
    color: Color = LocalContentColor.current,
    bounceHeight: Float = 10f,
    bounceDuration: Int = 300,
    timeBetweenBounces: Int = 0,
    delayStart: Int = 0,
    transition: InfiniteTransition = rememberInfiniteTransition()) {

    val offset: Float by transition.dotAnimator(
        minValue = 0f,
        maxValue = bounceHeight,
        duration = bounceDuration,
        timeBetween = timeBetweenBounces,
        delayStart = delayStart)

    Dot(modifier = Modifier
        .size(dotSize)
        .offset(y = -offset.dp), color = color)
}


@Preview()
@Composable
fun PulsingPreview() {
    ComposeButtonsTheme {
        LoadingIndicator()
    }
}

@Preview
@Composable
fun FlashingPreview() {
    ComposeButtonsTheme {
        LoadingIndicator(type = LoadingIndicatorTypes.Flashing)
    }
}

@Preview
@Composable
fun BouncingPreview() {
    ComposeButtonsTheme {
        LoadingIndicator(type = LoadingIndicatorTypes.Bouncing)
    }
}

