package domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.*
import model.SvgData

object SvgPathParser {

    fun toSvgData(svgPath: String): SvgData? = buildPathConverted(svgPath = svgPath, onColorsNotFound = {})?.let {
        SvgData(paths = listOf(it))
    }

    fun buildPathConverted(
        svgPath: String,
        pathFillType: String? = null,
        pathFillColor: String? = null,
        pathFillAlpha: String? = null,
        pathStrokeColor: String? = null,
        pathStrokeAlpha: String? = null,
        pathStrokeLineWidth: String? = null,
        pathStrokeLineCap: String? = null,
        pathStrokeLineJoin: String? = null,
        pathStrokeLineMiter: String? = null,
        onColorsNotFound: (colorValues: Set<String>) -> Unit,
    ): SvgData.PathConverted? {
        val unknownColors = mutableSetOf<String>()
        val pathConverted = SvgData.PathConverted(
            path = svgPath
                .split(Regex("(?=[MmLlHhVvCcSsQqTtAaZz])"))
                .filterNot { it.isEmpty() }
                .mapNotNull {
                    val string =
                        it.trim().takeIf { valueTrimmed -> valueTrimmed.isNotEmpty() } ?: return@mapNotNull null
                    val type = string.firstOrNull() ?: return@mapNotNull null
                    val values = string.substring(1)
                        .split(',', ' ')
                        .mapNotNull { value ->
                            value.trim().takeIf { valueTrimmed -> valueTrimmed.isNotEmpty() }?.toFloatOrNull()
                        }
                    type to values
                },
            pathFillType = PathFillType.fromString(pathFillType),
            pathFillColor = pathFillColor.toColor(onColorNotFound = { unknownColors.add(it) }),
            pathFillColorValue = pathFillColor.toColorValue(onColorNotFound = { unknownColors.add(it) }),
            pathFillAlpha = pathFillAlpha?.toFloatOrNull() ?: 1F,
            pathStrokeColor = pathStrokeColor?.toColorOrNull(onColorNotFound = { unknownColors.add(it) }),
            pathStrokeColorValue = pathStrokeColor?.toColorValueOrNull(onColorNotFound = { unknownColors.add(it) }),
            pathStrokeAlpha = pathStrokeAlpha?.toFloatOrNull() ?: 1F,
            pathStrokeLineWidth = pathStrokeLineWidth?.toFloatOrNull() ?: DefaultStrokeLineWidth,
            pathStrokeLineCap = StrokeCap.fromString(pathStrokeLineCap),
            pathStrokeLineJoin = StrokeJoin.fromString(pathStrokeLineJoin),
            pathStrokeLineMiter = pathStrokeLineMiter?.toFloatOrNull() ?: DefaultStrokeLineMiter,
        )

        return if (unknownColors.isNotEmpty()) {
            onColorsNotFound(unknownColors)
            null
        } else {
            pathConverted
        }
    }

    private fun PathFillType.Companion.fromString(value: String?): PathFillType =
        when (value?.trim()?.lowercase()) {
            NonZero.toString().lowercase() -> NonZero
            EvenOdd.toString().lowercase() -> EvenOdd
            else -> DefaultFillType
        }

    private fun StrokeCap.Companion.fromString(value: String?): StrokeCap =
        when (value?.trim()?.lowercase()) {
            Butt.toString().lowercase() -> Butt
            Round.toString().lowercase() -> Round
            Square.toString().lowercase() -> Square
            else -> DefaultStrokeLineCap
        }

    private fun StrokeJoin.Companion.fromString(value: String?): StrokeJoin =
        when (value?.trim()?.lowercase()) {
            Miter.toString().lowercase() -> Miter
            Round.toString().lowercase() -> Round
            Bevel.toString().lowercase() -> Bevel
            else -> DefaultStrokeLineJoin
        }

    private fun String.toColorOrNull(onColorNotFound: (colorValue: String) -> Unit): Color? = this
        .trim()
        .takeIf { it.firstOrNull() == '#' }
        ?.let {
            when (it.length) {
                4 -> {//#FFF format
                    val value = it.substring(1).toList().joinToString("") { char -> "$char$char" }
                    val javaColor = java.awt.Color.decode("#$value")
                    Color(javaColor.red, javaColor.green, javaColor.blue, 255)
                }
                7 -> {//#FFFFFF format
                    val javaColor = java.awt.Color.decode(it)
                    Color(javaColor.red, javaColor.green, javaColor.blue, 255)
                }
                9 -> {//#FFFFFFFF format
                    val i = java.lang.Long.decode(it).toInt()
                    val javaColor = java.awt.Color(i shr 16 and 0xFF, i shr 8 and 0xFF, i and 0xFF, i shr 24 and 0xFF)
                    Color(javaColor.red, javaColor.green, javaColor.blue, javaColor.alpha)
                }
                else -> null
            }
        } ?: let {
        if (UnknownColors.unknownColors.containsKey(it)) {
            UnknownColors.unknownColors.getValue(it).toColorOrNull {}
        } else {
            onColorNotFound(it)
            null
        }
    }

    private fun String?.toColor(onColorNotFound: (colorValue: String) -> Unit): Color {
        val color = this?.trim() ?: return Black
        return color.toColorOrNull(onColorNotFound) ?: Black
    }

    private fun String.toColorValueOrNull(onColorNotFound: (colorValue: String) -> Unit): String? = this
        .trim()
        .replace("#", "")
        .uppercase()
        .let {
            when (it.length) {
                3 -> {//#FFF format
                    val value = listOf("0xFF").plus(it.toList().joinToString("") { char -> "$char$char" })
                    value.joinToString("")
                }
                6 -> {//#FFFFFF format
                    "0xFF$it"
                }
                8 -> {//#FFFFFFFF format
                    "0x$it"
                }
                else -> null
            }
        } ?: let {
        if (UnknownColors.unknownColors.containsKey(it)) {
            UnknownColors.unknownColors.getValue(it).toColorValueOrNull {}
        } else {
            onColorNotFound(it)
            null
        }
    }

    private fun String?.toColorValue(onColorNotFound: (colorValue: String) -> Unit): String {
        val color = this?.trim() ?: return "0xFF000000"
        return color.toColorValueOrNull(onColorNotFound) ?: "0xFF000000"
    }
}
