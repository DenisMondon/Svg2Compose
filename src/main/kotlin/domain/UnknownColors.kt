package domain

import androidx.compose.ui.graphics.Color

object UnknownColors {

    val unknownColors = mutableMapOf<String, String>()

    fun decodeColor(color: String): Color? =
        when (color.length) {
            4 -> {//#FFF format
                try {
                    val value = color.substring(1).toList().joinToString("") { char -> "$char$char" }
                    val javaColor = java.awt.Color.decode("#$value")
                    Color(javaColor.red, javaColor.green, javaColor.blue, 255)
                } catch (e: NumberFormatException) {
                    null
                }
            }
            7 -> {//#FFFFFF format
                try {
                    val javaColor = java.awt.Color.decode(color)
                    Color(javaColor.red, javaColor.green, javaColor.blue, 255)
                } catch (e: NumberFormatException) {
                    null
                }
            }
            9 -> {//#FFFFFFFF format
                try {
                    val i = java.lang.Long.decode(color).toInt()
                    val javaColor = java.awt.Color(
                        i shr 16 and 0xFF,
                        i shr 8 and 0xFF,
                        i and 0xFF,
                        i shr 24 and 0xFF,
                    )
                    Color(javaColor.red, javaColor.green, javaColor.blue, javaColor.alpha)
                } catch (e: NumberFormatException) {
                    null
                }
            }
            else -> null
        }
}
