package domain

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import model.SvgData
import model.SvgData.Companion.DEFAULT_SIZE
import model.SvgData.Companion.DEFAULT_VIEWPORT_SIZE
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

object VectorDrawableParser {

    fun toSvgData(xml: String, onColorsNotFound: (colorValues: Set<String>) -> Unit): SvgData? {
        val unknownColors = mutableSetOf<String>()
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val `is` = InputSource(StringReader(xml))
            val document = builder.parse(`is`)
            val width = document.documentElement.getAttribute("android:width").toDp()
            val height = document.documentElement.getAttribute("android:height").toDp()
            val viewportWidth = document.documentElement.getAttribute("android:viewportWidth").toFloat()
            val viewportHeight = document.documentElement.getAttribute("android:viewportHeight").toFloat()
            val pathElements = document.documentElement.getElementsByTagName("path")
            val paths = mutableListOf<SvgData.PathConverted>()
            for (i in 0 until pathElements.length) {
                val item = pathElements.item(i)
                val pathData = item.getAttributeValue("android:pathData")
                val pathFillType = item.getAttributeValue("android:fillType")
                val pathFillColor = item.getAttributeValue("android:fillColor")
                val pathFillAlpha = item.getAttributeValue("android:fillAlpha")
                val pathStrokeAlpha = item.getAttributeValue("android:strokeAlpha")
                val pathStrokeLineWidth = item.getAttributeValue("android:strokeWidth")
                val pathStrokeLineCap = item.getAttributeValue("android:strokeLineCap")
                val pathStrokeLineJoin = item.getAttributeValue("android:strokeLineJoin")
                val pathStrokeLineMiter = item.getAttributeValue("android:strokeMiterLimit")
                if (pathData != null) {
                    SvgPathParser.buildPathConverted(
                        svgPath = pathData,
                        pathFillType = pathFillType,
                        pathFillColor = pathFillColor,
                        pathFillAlpha = pathFillAlpha,
                        pathStrokeAlpha = pathStrokeAlpha,
                        pathStrokeLineWidth = pathStrokeLineWidth,
                        pathStrokeLineCap = pathStrokeLineCap,
                        pathStrokeLineJoin = pathStrokeLineJoin,
                        pathStrokeLineMiter = pathStrokeLineMiter,
                        onColorsNotFound = { unknownColors.addAll(it) },
                    )?.also { paths += it }
                }
            }

            return if (unknownColors.isNotEmpty()) {
                onColorsNotFound(unknownColors)
                null
            } else {
                SvgData(
                    width = width,
                    height = height,
                    viewportWidth = viewportWidth,
                    viewportHeight = viewportHeight,
                    paths = paths,
                )
            }
        } catch (e: Exception) {
            return null
        }
    }

    private fun Node.getAttributeValue(name: String) =
        try {
            attributes.getNamedItem(name)?.nodeValue
        } catch (e: Exception) {
            ""
        }

    private fun String.toDp(): Dp = this.trim().replace("dp", "").toFloatOrNull()?.dp ?: DEFAULT_SIZE

    private fun String.toFloat(): Float = this.trim().toFloatOrNull() ?: DEFAULT_VIEWPORT_SIZE
}
