/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.ui.graphics

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.util.nativeClass
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.unit.center

@Immutable
sealed class Brush {
    @Deprecated(
        "Use applyTo(size, paint, alpha) instead",
        ReplaceWith("applyTo(size, p, alpha", "androidx.compose.ui.graphics")
    )
    open fun applyTo(p: Paint, alpha: Float) = applyTo(Size.Zero, p, alpha)

    abstract fun applyTo(size: Size, p: Paint, alpha: Float)

    companion object {

        /**
         * Creates a linear gradient with the provided colors along the given start and end
         * coordinates. The colors are dispersed at the provided offset defined in the [ColorStop]
         *
         * ```
         *  Brush.linearGradient(
         *      0.0f to Color.Red,
         *      0.3f to Color.Green,
         *      1.0f to Color.Blue,
         *      start = Offset(0.0f, 50.0f),
         *      end = Offset(0.0f, 100.0f)
         * )
         * ```
         *
         * @see androidx.compose.ui.graphics.samples.GradientBrushSample
         *
         * @param colorStops Colors and their offset in the gradient area
         * @param start Starting position of the linear gradient. This can be set to
         * [Offset.Infinite] to position at the far right and bottom of the drawing area
         * @param end Ending position of the linear gradient. This can be set to
         * [Offset.Infinite] to position at the far right and bottom of the drawing area
         * @param tileMode Determines the behavior for how the shader is to fill a region outside
         * its bounds. Defaults to [TileMode.Clamp] to repeat the edge pixels
         */
        @Stable
        fun linearGradient(
            vararg colorStops: ColorStop,
            start: Offset = Offset.Zero,
            end: Offset = Offset.Infinite,
            tileMode: TileMode = TileMode.Clamp
        ): Brush = LinearGradient(
            colors = List<Color>(colorStops.size) { i -> colorStops[i].second },
            stops = List<Float>(colorStops.size) { i -> colorStops[i].first },
            start = start,
            end = end,
            tileMode = tileMode
        )

        /**
         * Creates a linear gradient with the provided colors along the given start and end coordinates.
         * The colors are
         *
         * ```
         *  Brush.linearGradient(
         *      listOf(Color.Red, Color.Green, Color.Blue),
         *      start = Offset(0.0f, 50.0f)
         *      end = Offset(0.0f, 100.0f)
         * )
         * ```
         *
         * @sample androidx.compose.ui.graphics.samples.GradientBrushSample
         *
         * @param colors Colors to be rendered as part of the gradient
         * @param start Starting position of the linear gradient. This can be set to
         * [Offset.Infinite] to position at the far right and bottom of the drawing area
         * @param end Ending position of the linear gradient. This can be set to
         * [Offset.Infinite] to position at the far right and bottom of the drawing area
         * @param tileMode Determines the behavior for how the shader is to fill a region outside
         * its bounds. Defaults to [TileMode.Clamp] to repeat the edge pixels
         */
        @Stable
        fun linearGradient(
            colors: List<Color>,
            start: Offset = Offset.Zero,
            end: Offset = Offset.Infinite,
            tileMode: TileMode = TileMode.Clamp
        ): Brush = LinearGradient(
            colors = colors,
            stops = null,
            start = start,
            end = end,
            tileMode = tileMode
        )

        /**
         * Creates a horizontal gradient with the given colors evenly dispersed within the gradient
         *
         * Ex:
         * ```
         *  Brush.horizontalGradient(
         *      listOf(Color.Red, Color.Green, Color.Blue),
         *      startX = 10.0f,
         *      endX = 20.0f
         * )
         * ```
         *
         * @sample androidx.compose.ui.graphics.samples.GradientBrushSample
         *
         * @param colors colors Colors to be rendered as part of the gradient
         * @param startX Starting x position of the horizontal gradient. Defaults to 0 which
         * represents the left of the drawing area
         * @param endX Ending x position of the horizontal gradient.
         * Defaults to [Float.POSITIVE_INFINITY] which indicates the right of the specified
         * drawing area
         * @param tileMode Determines the behavior for how the shader is to fill a region outside
         * its bounds. Defaults to [TileMode.Clamp] to repeat the edge pixels
         */
        @Stable
        fun horizontalGradient(
            colors: List<Color>,
            startX: Float = 0.0f,
            endX: Float = Float.POSITIVE_INFINITY,
            tileMode: TileMode = TileMode.Clamp
        ): Brush = linearGradient(colors, Offset(startX, 0.0f), Offset(endX, 0.0f), tileMode)

        /**
         * Creates a horizontal gradient with the given colors dispersed at the provided offset
         * defined in the [ColorStop]
         *
         * Ex:
         * ```
         *  Brush.horizontalGradient(
         *      0.0f to Color.Red,
         *      0.3f to Color.Green,
         *      1.0f to Color.Blue,
         *      startX = 0.0f,
         *      endX = 100.0f
         * )
         * ```
         *
         * @sample androidx.compose.ui.graphics.samples.GradientBrushSample
         *
         * @param colorStops Colors and offsets to determine how the colors are dispersed throughout
         * the vertical gradient
         * @param startX Starting x position of the horizontal gradient. Defaults to 0 which
         * represents the left of the drawing area
         * @param endX Ending x position of the horizontal gradient.
         * Defaults to [Float.POSITIVE_INFINITY] which indicates the right of the specified
         * drawing area
         * @param tileMode Determines the behavior for how the shader is to fill a region outside
         * its bounds. Defaults to [TileMode.Clamp] to repeat the edge pixels
         */
        @Stable
        fun horizontalGradient(
            vararg colorStops: ColorStop,
            startX: Float = 0.0f,
            endX: Float = Float.POSITIVE_INFINITY,
            tileMode: TileMode = TileMode.Clamp
        ): Brush = linearGradient(
            *colorStops,
            start = Offset(startX, 0.0f),
            end = Offset(endX, 0.0f),
            tileMode = tileMode
        )

        /**
         * Creates a vertical gradient with the given colors evenly dispersed within the gradient
         * Ex:
         * ```
         *  Brush.verticalGradient(
         *      listOf(Color.Red, Color.Green, Color.Blue),
         *      startY = 0.0f,
         *      endY = 100.0f
         * )
         *
         * ```
         *
         * @sample androidx.compose.ui.graphics.samples.GradientBrushSample
         *
         * @param colors colors Colors to be rendered as part of the gradient
         * @param startY Starting y position of the vertical gradient. Defaults to 0 which
         * represents the top of the drawing area
         * @param endY Ending y position of the vertical gradient.
         * Defaults to [Float.POSITIVE_INFINITY] which indicates the bottom of the specified
         * drawing area
         * @param tileMode Determines the behavior for how the shader is to fill a region outside
         * its bounds. Defaults to [TileMode.Clamp] to repeat the edge pixels
         */
        @Stable
        fun verticalGradient(
            colors: List<Color>,
            startY: Float = 0.0f,
            endY: Float = Float.POSITIVE_INFINITY,
            tileMode: TileMode = TileMode.Clamp
        ): Brush = linearGradient(colors, Offset(0.0f, startY), Offset(0.0f, endY), tileMode)

        /**
         * Creates a vertical gradient with the given colors at the provided offset defined
         * in the [ColorStop]
         *
         * Ex:
         * ```
         *  Brush.verticalGradient(
         *      0.1f to Color.Red,
         *      0.3f to Color.Green,
         *      0.5f to Color.Blue,
         *      startY = 0.0f,
         *      endY = 100.0f
         * )
         * ```
         *
         * @sample androidx.compose.ui.graphics.samples.GradientBrushSample
         *
         * @param colorStops Colors and offsets to determine how the colors are dispersed throughout
         * the vertical gradient
         * @param startY Starting y position of the vertical gradient. Defaults to 0 which
         * represents the top of the drawing area
         * @param endY Ending y position of the vertical gradient.
         * Defaults to [Float.POSITIVE_INFINITY] which indicates the bottom of the specified
         * drawing area
         * @param tileMode Determines the behavior for how the shader is to fill a region outside
         * its bounds. Defaults to [TileMode.Clamp] to repeat the edge pixels
         */
        @Stable
        fun verticalGradient(
            vararg colorStops: ColorStop,
            startY: Float = 0f,
            endY: Float = Float.POSITIVE_INFINITY,
            tileMode: TileMode = TileMode.Clamp
        ): Brush = linearGradient(
            *colorStops,
            start = Offset(0.0f, startY),
            end = Offset(0.0f, endY),
            tileMode = tileMode
        )

        /**
         * Creates a radial gradient with the given colors at the provided offset
         * defined in the [ColorStop]
         * ```
         * Brush.radialGradient(
         *      0.0f to Color.Red,
         *      0.3f to Color.Green,
         *      1.0f to Color.Blue,
         *      center = Offset(side1 / 2.0f, side2 / 2.0f),
         *      radius = side1 / 2.0f,
         *      tileMode = TileMode.Repeated
         * )
         * ```
         *
         * @sample androidx.compose.ui.graphics.samples.GradientBrushSample
         *
         * @param colorStops Colors and offsets to determine how the colors are dispersed throughout
         * the radial gradient
         * @param center Center position of the radial gradient circle. If this is set to
         * [Offset.Unspecified] then the center of the drawing area is used as the center for
         * the radial gradient. [Float.POSITIVE_INFINITY] can be used for either [Offset.x] or
         * [Offset.y] to indicate the far right or far bottom of the drawing area respectively.
         * @param radius Radius for the radial gradient. Defaults to positive infinity to indicate
         * the largest radius that can fit within the bounds of the drawing area
         * @param tileMode Determines the behavior for how the shader is to fill a region outside
         * its bounds. Defaults to [TileMode.Clamp] to repeat the edge pixels
         */
        @Stable
        fun radialGradient(
            vararg colorStops: ColorStop,
            center: Offset = Offset.Unspecified,
            radius: Float = Float.POSITIVE_INFINITY,
            tileMode: TileMode = TileMode.Clamp
        ): Brush = RadialGradient(
            colors = List<Color>(colorStops.size) { i -> colorStops[i].second },
            stops = List<Float>(colorStops.size) { i -> colorStops[i].first },
            center = center,
            radius = radius,
            tileMode = tileMode
        )

        /**
         * Creates a radial gradient with the given colors evenly dispersed within the gradient
         * ```
         * Brush.radialGradient(
         *      listOf(Color.Red, Color.Green, Color.Blue),
         *      centerX = side1 / 2.0f,
         *      centerY = side2 / 2.0f,
         *      radius = side1 / 2.0f,
         *      tileMode = TileMode.Repeated
         * )
         * ```
         *
         * @sample androidx.compose.ui.graphics.samples.GradientBrushSample
         *
         * @param colors Colors to be rendered as part of the gradient
         * @param center Center position of the radial gradient circle. If this is set to
         * [Offset.Unspecified] then the center of the drawing area is used as the center for
         * the radial gradient. [Float.POSITIVE_INFINITY] can be used for either [Offset.x] or
         * [Offset.y] to indicate the far right or far bottom of the drawing area respectively.
         * @param radius Radius for the radial gradient. Defaults to positive infinity to indicate
         * the largest radius that can fit within the bounds of the drawing area
         * @param tileMode Determines the behavior for how the shader is to fill a region outside
         * its bounds. Defaults to [TileMode.Clamp] to repeat the edge pixels
         */
        @Stable
        fun radialGradient(
            colors: List<Color>,
            center: Offset = Offset.Unspecified,
            radius: Float = Float.POSITIVE_INFINITY,
            tileMode: TileMode = TileMode.Clamp
        ): Brush = RadialGradient(
            colors = colors,
            stops = null,
            center = center,
            radius = radius,
            tileMode = tileMode
        )

        /**
         * Creates a sweep gradient with the given colors dispersed around the center with
         * offsets defined in each [ColorStop]. The sweep begins relative to 3 o'clock and continues
         * clockwise until it reaches the starting position again.
         *
         * Ex:
         * ```
         *  Brush.sweepGradient(
         *      0.0f to Color.Red,
         *      0.3f to Color.Green,
         *      1.0f to Color.Blue,
         *      center = Offset(0.0f, 100.0f)
         * )
         * ```
         *
         * @sample androidx.compose.ui.graphics.samples.GradientBrushSample
         *
         * @param colorStops Colors and offsets to determine how the colors are dispersed throughout
         * the sweep gradient
         * @param center Center position of the sweep gradient circle. If this is set to
         * [Offset.Unspecified] then the center of the drawing area is used as the center for
         * the sweep gradient
         */
        @Stable
        fun sweepGradient(
            vararg colorStops: ColorStop,
            center: Offset = Offset.Unspecified
        ): Brush = SweepGradient(
            colors = List<Color>(colorStops.size) { i -> colorStops[i].second },
            stops = List<Float>(colorStops.size) { i -> colorStops[i].first },
            center = center
        )

        /**
         * Creates a sweep gradient with the given colors dispersed evenly around the center.
         * The sweep begins relative to 3 o'clock and continues clockwise until it reaches the
         * starting position again.
         *
         * Ex:
         * ```
         *  Brush.sweepGradient(
         *      listOf(Color.Red, Color.Green, Color.Blue),
         *      center = Offset(10.0f, 20.0f)
         * )
         * ```
         *
         * @sample androidx.compose.ui.graphics.samples.GradientBrushSample
         *
         * @param colors List of colors to fill the sweep gradient
         * @param center Center position of the sweep gradient circle. If this is set to
         * [Offset.Unspecified] then the center of the drawing area is used as the center for
         * the sweep gradient
         */
        @Stable
        fun sweepGradient(
            colors: List<Color>,
            center: Offset = Offset.Unspecified
        ): Brush = SweepGradient(
            colors = colors,
            stops = null,
            center = center
        )
    }
}

@Immutable
class SolidColor(val value: Color) : Brush() {
    override fun applyTo(size: Size, p: Paint, alpha: Float) {
        p.alpha = DefaultAlpha
        p.color = if (alpha != DefaultAlpha) {
            value.copy(alpha = value.alpha * alpha)
        } else {
            value
        }
        if (p.shader != null) p.shader = null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (nativeClass() != other?.nativeClass()) return false

        other as SolidColor

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "SolidColor(value=$value)"
    }
}

typealias ColorStop = Pair<Float, Color>

/**
 * Creates a linear gradient with the provided colors along the given start and end coordinates.
 * The colors are
 *
 * ```
 *  LinearGradient(
 *      listOf(Color.Red, Color.Green, Color.Blue),
 *      startX = 0.0f,
 *      startY = 50.0f,
 *      endY = 0.0f,
 *      endY = 100.0f
 * )
 * ```
 */
@Deprecated(
    "Use Brush.linearGradient instead",
    ReplaceWith(
        "Brush.linearGradient(colors = colors," +
            "start = Offset(startX, startY), " +
            "end = Offset(endX, endY), " +
            "tileMode = tileMode)",
        "androidx.compose.ui.ui.graphics"
    )
)
@Stable
fun LinearGradient(
    colors: List<Color>,
    startX: Float,
    startY: Float,
    endX: Float,
    endY: Float,
    tileMode: TileMode = TileMode.Clamp
) = LinearGradient(
    colors,
    null,
    Offset(startX, startY),
    Offset(endX, endY),
    tileMode
)

/**
 * Creates a linear gradient with the provided colors along the given start and end coordinates.
 * The colors are dispersed at the provided offset defined in the [ColorStop]
 *
 * ```
 *  LinearGradient(
 *      0.0f to Color.Red,
 *      0.3f to Color.Green,
 *      1.0f to Color.Blue,
 *      startX = 0.0f,
 *      startY = 50.0f,
 *      endY = 0.0f,
 *      endY = 100.0f
 * )
 * ```
 */
@Deprecated(
    "Use Brush.linearGradient instead",
    ReplaceWith(
        "Brush.linearGradient(colorStops, " +
            "start = Offset(startX, startY), " +
            "end = Offset(endX, endY), " +
            "tileMode = tileMode)",
        "androidx.compose.ui.ui.graphics"
    )
)
@Stable
fun LinearGradient(
    vararg colorStops: ColorStop,
    startX: Float,
    startY: Float,
    endX: Float,
    endY: Float,
    tileMode: TileMode = TileMode.Clamp
) = LinearGradient(
    List<Color>(colorStops.size) { i -> colorStops[i].second },
    List<Float>(colorStops.size) { i -> colorStops[i].first },
    Offset(startX, startY),
    Offset(endX, endY),
    tileMode
)

/**
 * Creates a radial gradient with the given colors at the provided offset defined in the [ColorStop]
 * ```
 * RadialGradient(
 *      0.0f to Color.Red,
 *      0.3f to Color.Green,
 *      1.0f to Color.Blue,
 *      centerX = side1 / 2.0f,
 *      centerY = side2 / 2.0f,
 *      radius = side1 / 2.0f,
 *      tileMode = TileMode.Repeated
 * )
 * ```
 */
@Deprecated(
    "Use Brush.radialGradient instead",
    ReplaceWith(
        "Brush.radialGradient(" +
            "colorStops, " +
            "center = Offset(centerX, centerY)," +
            "radius = radius, " +
            "tileMode = tileMode" +
            ")",
        "androidx.compose.ui.ui.graphics"
    )
)
@Stable
fun RadialGradient(
    vararg colorStops: ColorStop,
    centerX: Float,
    centerY: Float,
    radius: Float,
    tileMode: TileMode = TileMode.Clamp
) = RadialGradient(
    List<Color>(colorStops.size) { i -> colorStops[i].second },
    List<Float>(colorStops.size) { i -> colorStops[i].first },
    Offset(centerX, centerY),
    radius,
    tileMode
)

/**
 * Creates a radial gradient with the given colors evenly dispersed within the gradient
 * ```
 * RadialGradient(
 *      listOf(Color.Red, Color.Green, Color.Blue),
 *      centerX = side1 / 2.0f,
 *      centerY = side2 / 2.0f,
 *      radius = side1 / 2.0f,
 *      tileMode = TileMode.Repeated
 * )
 * ```
 */
@Deprecated(
    "Use Brush.radialGradient instead",
    ReplaceWith(
        "Brush.radialGradient(colors, Offset(centerX, centerY), radius, tileMode)",
        "androidx.compose.ui.ui.graphics"
    )
)
@Stable
fun RadialGradient(
    colors: List<Color>,
    centerX: Float,
    centerY: Float,
    radius: Float,
    tileMode: TileMode = TileMode.Clamp
) = RadialGradient(colors, null, Offset(centerX, centerY), radius, tileMode)

/**
 * Creates a vertical gradient with the given colors evenly dispersed within the gradient
 * Ex:
 * ```
 *  VerticalGradient(
 *      listOf(Color.Red, Color.Green, Color.Blue),
 *      startY = 0.0f,
 *      endY = 100.0f
 * )
 *
 * ```
 */
@Deprecated(
    "Use Brush.verticalGradient instead",
    ReplaceWith(
        "Brush.verticalGradient(colors, startY, endY, tileMode)",
        "androidx.compose.ui.ui.graphics"
    )
)
@Stable
fun VerticalGradient(
    colors: List<Color>,
    startY: Float,
    endY: Float,
    tileMode: TileMode = TileMode.Clamp
) = LinearGradient(
    colors,
    null,
    start = Offset(0.0f, startY),
    end = Offset(0.0f, endY),
    tileMode = tileMode
)

/**
 * Creates a vertical gradient with the given colors at the provided offset defined in the [ColorStop]
 * Ex:
 * ```
 *  VerticalGradient(
 *      0.1f to Color.Red,
 *      0.3f to Color.Green,
 *      0.5f to Color.Blue,
 *      startY = 0.0f,
 *      endY = 100.0f
 * )
 * ```
 */
@Deprecated(
    "Use Brush.verticalGradient instead",
    ReplaceWith(
        "Brush.verticalGradient(colorStops, startY = startY, endY = endY, tileMode = tileMode)",
        "androidx.compose.ui.ui.graphics"
    )
)
@Stable
fun VerticalGradient(
    vararg colorStops: ColorStop,
    startY: Float,
    endY: Float,
    tileMode: TileMode = TileMode.Clamp
) = LinearGradient(
    List<Color>(colorStops.size) { i -> colorStops[i].second },
    List<Float>(colorStops.size) { i -> colorStops[i].first },
    start = Offset(0.0f, startY),
    end = Offset(0.0f, endY),
    tileMode = tileMode
)

/**
 * Creates a horizontal gradient with the given colors evenly dispersed within the gradient
 *
 * Ex:
 * ```
 *  HorizontalGradient(
 *      listOf(Color.Red, Color.Green, Color.Blue),
 *      startX = 10.0f,
 *      endX = 20.0f
 * )
 * ```
 */
@Deprecated(
    "Use Brush.horizontalGradient instead",
    ReplaceWith(
        "Brush.horizontalGradient(colors, startX, endX, tileMode)",
        "androidx.compose.ui.ui.graphics"
    )
)
@Stable
fun HorizontalGradient(
    colors: List<Color>,
    startX: Float,
    endX: Float,
    tileMode: TileMode = TileMode.Clamp
) = LinearGradient(
    colors,
    null,
    start = Offset(startX, 0.0f),
    end = Offset(endX, 0.0f),
    tileMode = tileMode
)

/**
 * Creates a horizontal gradient with the given colors dispersed at the provided offset defined in the [ColorStop]
 *
 * Ex:
 * ```
 *  HorizontalGradient(
 *      0.0f to Color.Red,
 *      0.3f to Color.Green,
 *      1.0f to Color.Blue,
 *      startX = 0.0f,
 *      endX = 100.0f
 * )
 * ```
 */
@Deprecated(
    "Use Brush.horizontalGradient instead",
    ReplaceWith(
        "Brush.horizontalGradient(" +
            "colorStops, startX = startX, endX = endX, tileMode = tileMode)",
        "androidx.compose.ui.ui.graphics"
    )
)
@Stable
fun HorizontalGradient(
    vararg colorStops: ColorStop,
    startX: Float,
    endX: Float,
    tileMode: TileMode = TileMode.Clamp
) = LinearGradient(
    List<Color>(colorStops.size) { i -> colorStops[i].second },
    List<Float>(colorStops.size) { i -> colorStops[i].first },
    start = Offset(startX, 0.0f),
    end = Offset(endX, 0.0f),
    tileMode = tileMode
)

/**
 * Creates a sweep gradient with the given colors dispersed around the center with
 * offsets defined in each [ColorStop]. The sweep begins relative to 3 o'clock and continues
 * clockwise until it reaches the starting position again.
 *
 * Ex:
 * ```
 *  SweepGradient(
 *      0.0f to Color.Red,
 *      0.3f to Color.Green,
 *      1.0f to Color.Blue,
 *      center = Offset(0.0f, 100.0f)
 * )
 * ```
 */
@Deprecated(
    "Use Brush.sweepGradient instead",
    ReplaceWith(
        "Brush.sweepGradient(colorStops, center = center)",
        "androidx.compose.ui.ui.graphics"
    )
)
@Stable
fun SweepGradient(
    vararg colorStops: ColorStop,
    center: Offset
) = SweepGradient(
    center,
    List<Color>(colorStops.size) { i -> colorStops[i].second },
    List<Float>(colorStops.size) { i -> colorStops[i].first }
)

/**
 * Creates a sweep gradient with the given colors dispersed evenly around the center.
 * The sweep begins relative to 3 o'clock and continues clockwise until it reaches the starting
 * position again.
 *
 * Ex:
 * ```
 *  SweepGradient(
 *      listOf(Color.Red, Color.Green, Color.Blue),
 *      center = Offset(10.0f, 20.0f)
 * )
 * ```
 */
@Deprecated(
    "Use Brush.sweepGradient instead",
    ReplaceWith("Brush.sweepGradient(colors, center)", "androidx.compose.ui.ui.graphics")
)
@Stable
fun SweepGradient(
    colors: List<Color>,
    center: Offset
) = SweepGradient(center, colors, null)

/**
 * Brush implementation used to apply a linear gradient on a given [Paint]
 */
@Immutable
class LinearGradient internal constructor(
    private val colors: List<Color>,
    private val stops: List<Float>? = null,
    private val start: Offset,
    private val end: Offset,
    private val tileMode: TileMode = TileMode.Clamp
) : ShaderBrush() {

    override fun createShader(size: Size): Shader {
        val startX = if (start.x == Float.POSITIVE_INFINITY) size.width else start.x
        val startY = if (start.y == Float.POSITIVE_INFINITY) size.height else start.y
        val endX = if (end.x == Float.POSITIVE_INFINITY) size.width else end.x
        val endY = if (end.y == Float.POSITIVE_INFINITY) size.height else end.y
        return LinearGradientShader(
            colors = colors,
            colorStops = stops,
            from = Offset(startX, startY),
            to = Offset(endX, endY),
            tileMode = tileMode
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (nativeClass() != other?.nativeClass()) return false

        other as LinearGradient

        if (colors != other.colors) return false
        if (stops != other.stops) return false
        if (start != other.start) return false
        if (end != other.end) return false
        if (tileMode != other.tileMode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = colors.hashCode()
        result = 31 * result + (stops?.hashCode() ?: 0)
        result = 31 * result + start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + tileMode.hashCode()
        return result
    }

    override fun toString(): String {
        val startValue = if (start.isFinite) "start=$start, " else ""
        val endValue = if (end.isFinite) "end=$end, " else ""
        return "LinearGradient(colors=$colors, " +
            "stops=$stops, " +
            startValue +
            endValue +
            "tileMode=$tileMode)"
    }
}

/**
 * Brush implementation used to apply a radial gradient on a given [Paint]
 */
@Immutable
class RadialGradient internal constructor(
    private val colors: List<Color>,
    private val stops: List<Float>? = null,
    private val center: Offset,
    private val radius: Float,
    private val tileMode: TileMode = TileMode.Clamp
) : ShaderBrush() {

    override fun createShader(size: Size): Shader {
        val centerX: Float
        val centerY: Float
        if (center.isUnspecified) {
            val drawCenter = size.center()
            centerX = drawCenter.x
            centerY = drawCenter.y
        } else {
            centerX = if (center.x == Float.POSITIVE_INFINITY) size.width else center.x
            centerY = if (center.y == Float.POSITIVE_INFINITY) size.height else center.y
        }

        return RadialGradientShader(
            colors = colors,
            colorStops = stops,
            center = Offset(centerX, centerY),
            radius = if (radius == Float.POSITIVE_INFINITY) size.minDimension / 2 else radius,
            tileMode = tileMode
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (nativeClass() != other?.nativeClass()) return false

        other as RadialGradient

        if (colors != other.colors) return false
        if (stops != other.stops) return false
        if (center != other.center) return false
        if (radius != other.radius) return false
        if (tileMode != other.tileMode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = colors.hashCode()
        result = 31 * result + (stops?.hashCode() ?: 0)
        result = 31 * result + center.hashCode()
        result = 31 * result + radius.hashCode()
        result = 31 * result + tileMode.hashCode()
        return result
    }

    override fun toString(): String {
        val centerValue = if (center.isSpecified) "center=$center, " else ""
        val radiusValue = if (radius.isFinite()) "radius=$radius, " else ""
        return "RadialGradient(" +
            "colors=$colors, " +
            "stops=$stops, " +
            centerValue +
            radiusValue +
            "tileMode=$tileMode)"
    }
}

/**
 * Brush implementation used to apply a sweep gradient on a given [Paint]
 */
@Immutable
class SweepGradient internal constructor(
    private val center: Offset,
    private val colors: List<Color>,
    private val stops: List<Float>? = null
) : ShaderBrush() {

    override fun createShader(size: Size): Shader =
        SweepGradientShader(
            if (center.isUnspecified) {
                size.center()
            } else {
                Offset(
                    if (center.x == Float.POSITIVE_INFINITY) size.width else center.x,
                    if (center.y == Float.POSITIVE_INFINITY) size.height else center.y
                )
            },
            colors,
            stops
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (nativeClass() != other?.nativeClass()) return false

        other as SweepGradient

        if (center != other.center) return false
        if (colors != other.colors) return false
        if (stops != other.stops) return false

        return true
    }

    override fun hashCode(): Int {
        var result = center.hashCode()
        result = 31 * result + colors.hashCode()
        result = 31 * result + (stops?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        val centerValue = if (center.isSpecified) "center=$center, " else ""
        return "SweepGradient(" +
            centerValue +
            "colors=$colors, stops=$stops)"
    }
}

/**
 * Convenience method to create a ShaderBrush that always returns the same shader instance
 * regardless of size
 */
fun ShaderBrush(shader: Shader) = object : ShaderBrush() {

    /**
     * Create a shader based on the given size that represents the current drawing area
     */
    override fun createShader(size: Size): Shader = shader
}

/**
 * Brush implementation that wraps and applies a the provided shader to a [Paint]
 * The shader can be lazily created based on a given size, or provided directly as a parameter
 */
@Immutable
abstract class ShaderBrush() : Brush() {

    private var internalShader: Shader? = null
    private var createdSize = Size.Unspecified

    abstract fun createShader(size: Size): Shader

    final override fun applyTo(size: Size, p: Paint, alpha: Float) {
        var shader = internalShader
        if (shader == null || createdSize != size) {
            shader = createShader(size).also { internalShader = it }
            createdSize = size
        }
        if (p.color != Color.Black) p.color = Color.Black
        if (p.shader != shader) p.shader = shader
        if (p.alpha != alpha) p.alpha = alpha
    }
}