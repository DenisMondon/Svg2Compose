package model

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max

data class SvgData(
    val width: Dp = DEFAULT_SIZE,
    val height: Dp = DEFAULT_SIZE,
    val viewportWidth: Float = DEFAULT_VIEWPORT_SIZE,
    val viewportHeight: Float = DEFAULT_VIEWPORT_SIZE,
    val isMaterialIcon: Boolean = width == 24.dp && height == 24.dp && viewportWidth == 24F && viewportHeight == 24F,
    val paths: List<PathConverted> = emptyList(),
) {

    data class PathConverted(
        val path: List<Pair<Char, List<Float>>>,
        val pathFillType: PathFillType,
        val pathFillColor: Color,
        val pathFillColorValue: String,
        val pathFillAlpha: Float,
        val pathStrokeColor: Color?,
        val pathStrokeColorValue: String?,
        val pathStrokeAlpha: Float,
        val pathStrokeLineWidth: Float,
        val pathStrokeLineCap: StrokeCap,
        val pathStrokeLineJoin: StrokeJoin,
        val pathStrokeLineMiter: Float,
    ) {

        fun isPathDefault(): Boolean =
            pathFillType == DefaultFillType
                    && pathFillColor == Color.Black
                    && pathFillAlpha == 1F
                    && pathStrokeColor == null
                    && pathStrokeAlpha == 1F
                    && pathStrokeLineWidth == DefaultStrokeLineWidth
                    && pathStrokeLineCap == DefaultStrokeLineCap
                    && pathStrokeLineJoin == DefaultStrokeLineJoin
                    && pathStrokeLineMiter == DefaultStrokeLineMiter
    }

    fun toPathDecomposed(): String {
        var pathDecomposed = if (isMaterialIcon) "\n".repeat(5) else "\n".repeat(11)
        paths.forEach { pathConverted ->
            pathDecomposed += if (pathConverted.isPathDefault()) {
                if (isMaterialIcon) "\n" else ""
            } else {
                if (isMaterialIcon) {
                    "\n".repeat(10 + if (pathConverted.pathStrokeColor != null) 1 else 0)
                } else {
                    "\n".repeat(9 + if (pathConverted.pathStrokeColor != null) 1 else 0)
                }
            }
            pathConverted.path.forEach loop@{ (type, values) ->
                when (type) {
                    'M', 'm' -> {
                        val x = values.getOrNull(0) ?: return@loop
                        val y = values.getOrNull(1) ?: return@loop
                        pathDecomposed += "\n$type${x},${y}"
                    }
                    'L', 'l' -> {
                        if (values.size % 2 == 0) {
                            var i = 0
                            repeat(values.size / 2) {
                                val x = values.getOrNull(i) ?: return@loop
                                val y = values.getOrNull(i + 1) ?: return@loop
                                pathDecomposed += if (i == 0) "\n$type${x},${y}" else "\n  ${x},${y}"
                                i += 2
                            }
                        }
                    }
                    'H', 'h' -> {
                        val x = values.getOrNull(0) ?: return@loop
                        pathDecomposed += "\n$type${x}"
                    }
                    'V', 'v' -> {
                        val y = values.getOrNull(0) ?: return@loop
                        pathDecomposed += "\n$type${y}"
                    }
                    'C', 'c' -> {
                        if (values.size % 6 == 0) {
                            var i = 0
                            repeat(values.size / 6) {
                                val x1 = values.getOrNull(0) ?: return@loop
                                val y1 = values.getOrNull(1) ?: return@loop
                                val x2 = values.getOrNull(2) ?: return@loop
                                val y2 = values.getOrNull(3) ?: return@loop
                                val x3 = values.getOrNull(4) ?: return@loop
                                val y3 = values.getOrNull(5) ?: return@loop
                                pathDecomposed += if (i == 0) "\n$type${x1},${y1},${x2},${y2},${x3},${y3}"
                                else "\n  ${x1},${y1},${x2},${y2},${x3},${y3}"
                                i += 6
                            }
                        }
                    }
                    'S', 's' -> {
                        if (values.size % 4 == 0) {
                            var i = 0
                            repeat(values.size / 4) {
                                val x1 = values.getOrNull(i) ?: return@loop
                                val y1 = values.getOrNull(i + 1) ?: return@loop
                                val x2 = values.getOrNull(i + 2) ?: return@loop
                                val y2 = values.getOrNull(i + 3) ?: return@loop
                                pathDecomposed += if (i == 0) "\n$type${x1} ${y1}, $x2 $y2" else "\n  $x1 ${y1}, $x2 $y2"
                                i += 4
                            }
                        }
                    }
                    'Q', 'q' -> {
                        if (values.size % 4 == 0) {
                            var i = 0
                            repeat(values.size / 4) {
                                val x1 = values.getOrNull(0) ?: return@loop
                                val y1 = values.getOrNull(1) ?: return@loop
                                val x2 = values.getOrNull(2) ?: return@loop
                                val y2 = values.getOrNull(3) ?: return@loop
                                pathDecomposed += if (i == 0) "\n$type${x1} ${y1}, $x2 $y2" else "\n  $x1 ${y1}, $x2 $y2"
                                i += 4
                            }
                        }
                    }
                    'T', 't' -> {
                        if (values.size % 2 == 0) {
                            var i = 0
                            repeat(values.size / 2) {
                                val x = values.getOrNull(0) ?: return@loop
                                val y = values.getOrNull(1) ?: return@loop
                                pathDecomposed += if (i == 0) "\n$type${x} $y" else "\n  $x $y"
                                i += 2
                            }
                        }
                    }
                    'A', 'a' -> {
                        if (values.size % 7 == 0) {
                            var i = 0
                            repeat(values.size / 7) {
                                val rx = values.getOrNull(0) ?: return@loop
                                val ry = values.getOrNull(1) ?: return@loop
                                val xAxisRotation = values.getOrNull(2) ?: return@loop
                                val largeArcFlag = values.getOrNull(3)?.toInt() ?: return@loop
                                val sweepFlag = values.getOrNull(4)?.toInt() ?: return@loop
                                val x = values.getOrNull(5) ?: return@loop
                                val y = values.getOrNull(6) ?: return@loop
                                pathDecomposed += if (i == 0) "\n$type${rx},${ry} ${xAxisRotation},${largeArcFlag} ${sweepFlag},${x} $y"
                                else "\n  ${rx},${ry} ${xAxisRotation},${largeArcFlag} ${sweepFlag},${x} $y"
                                i += 7
                            }
                        }
                    }
                    'Z', 'z' -> {
                        pathDecomposed += "\n$type"
                    }
                }
            }
            pathDecomposed += "\n"
        }
        return pathDecomposed
    }

    fun toImageVectorCode(): String {
        var imageVectorCode = if (isMaterialIcon) {
            "val Icons.[IconName]: ImageVector\n" +
                    "    get() {\n" +
                    "        if (_[iconName] != null) {\n" +
                    "            return _[iconName]!!\n" +
                    "        }\n" +
                    "        _[iconName] = materialIcon(name = \"[IconName]\") {"
        } else {
            "val Icons.[IconName]: ImageVector\n" +
                    "    get() {\n" +
                    "        if (_[iconName] != null) {\n" +
                    "            return _[iconName]!!\n" +
                    "        }\n" +
                    "        _[iconName] = ImageVector.Builder(\n" +
                    "            name = \"[IconName]\",\n" +
                    "            defaultWidth = ${width},\n" +
                    "            defaultHeight = ${height},\n" +
                    "            viewportWidth = ${viewportWidth}F,\n" +
                    "            viewportHeight = ${viewportHeight}F,\n" +
                    "        )"
        }
        paths.forEach { path ->
            val pathFillTypeValue = "PathFillType.${path.pathFillType}"
            val pathStrokeLineCapValue = "StrokeCap.${path.pathStrokeLineCap}"
            val pathStrokeLineJoinValue = "StrokeJoin.${path.pathStrokeLineJoin}"
            imageVectorCode += if (path.isPathDefault()) {
                if (isMaterialIcon) {
                    "\n            materialPath {"
                } else {
                    ".materialPath {"
                }
            } else {
                if (isMaterialIcon) {
                    "\n            path(\n" +
                            "                fill = SolidColor(Color(${path.pathFillColorValue})),\n" +
                            "                fillAlpha = ${path.pathFillAlpha}F,\n"
                                .let {
                                    if (path.pathStrokeColorValue != null) {
                                        it + "                stroke = SolidColor(Color(${path.pathStrokeColorValue})),\n"
                                    } else {
                                        it
                                    }
                                } +
                            "                strokeAlpha = ${path.pathStrokeAlpha}F,\n" +
                            "                strokeLineWidth = ${path.pathStrokeLineWidth}F,\n" +
                            "                strokeLineCap = ${pathStrokeLineCapValue},\n" +
                            "                strokeLineJoin = ${pathStrokeLineJoinValue},\n" +
                            "                strokeLineMiter = ${path.pathStrokeLineMiter}F,\n" +
                            "                pathFillType = ${pathFillTypeValue},\n" +
                            "            ) {"
                } else {
                    ".path(\n" +
                            "            fill = SolidColor(Color(${path.pathFillColorValue})),\n" +
                            "            fillAlpha = ${path.pathFillAlpha}F,\n"
                                .let {
                                    if (path.pathStrokeColorValue != null) {
                                        it + "            stroke = SolidColor(Color(${path.pathStrokeColorValue})),\n"
                                    } else {
                                        it
                                    }
                                } +
                            "            strokeAlpha = ${path.pathStrokeAlpha}F,\n" +
                            "            strokeLineWidth = ${path.pathStrokeLineWidth}F,\n" +
                            "            strokeLineCap = ${pathStrokeLineCapValue},\n" +
                            "            strokeLineJoin = ${pathStrokeLineJoinValue},\n" +
                            "            strokeLineMiter = ${path.pathStrokeLineMiter}F,\n" +
                            "            pathFillType = ${pathFillTypeValue},\n" +
                            "        ) {"
                }
            }

            path.path.forEachIndexed loop@{ index, (type, values) ->
                when (type) {
                    'M' -> {
                        val x = values.getOrNull(0) ?: return@loop
                        val y = values.getOrNull(1) ?: return@loop
                        imageVectorCode += "\n              moveTo(${x}F, ${y}F)"
                    }
                    'm' -> {
                        val x = values.getOrNull(0) ?: return@loop
                        val y = values.getOrNull(1) ?: return@loop
                        imageVectorCode += "\n              moveToRelative(${x}F, ${y}F)"
                    }
                    'L' -> {
                        if (values.size % 2 == 0) {
                            var i = 0
                            repeat(values.size / 2) {
                                val x = values.getOrNull(i) ?: return@loop
                                val y = values.getOrNull(i + 1) ?: return@loop
                                imageVectorCode += "\n              lineTo(${x}F, ${y}F)"
                                i += 2
                            }
                        }
                    }
                    'l' -> {
                        if (values.size % 2 == 0) {
                            var i = 0
                            repeat(values.size / 2) {
                                val x = values.getOrNull(i) ?: return@loop
                                val y = values.getOrNull(i + 1) ?: return@loop
                                imageVectorCode += "\n              lineToRelative(${x}F, ${y}F)"
                                i += 2
                            }
                        }
                    }
                    'H' -> {
                        val x = values.getOrNull(0) ?: return@loop
                        imageVectorCode += "\n              horizontalLineTo(${x}F)"
                    }
                    'h' -> {
                        val x = values.getOrNull(0) ?: return@loop
                        imageVectorCode += "\n              horizontalLineToRelative(${x}F)"
                    }
                    'V' -> {
                        val y = values.getOrNull(0) ?: return@loop
                        imageVectorCode += "\n              verticalLineTo(${y}F)"
                    }
                    'v' -> {
                        val y = values.getOrNull(0) ?: return@loop
                        imageVectorCode += "\n              verticalLineToRelative(${y}F)"
                    }
                    'C' -> {
                        if (values.size % 6 == 0) {
                            var i = 0
                            repeat(values.size / 6) {
                                val x1 = values.getOrNull(i) ?: return@loop
                                val y1 = values.getOrNull(i + 1) ?: return@loop
                                val x2 = values.getOrNull(i + 2) ?: return@loop
                                val y2 = values.getOrNull(i + 3) ?: return@loop
                                val x3 = values.getOrNull(i + 4) ?: return@loop
                                val y3 = values.getOrNull(i + 5) ?: return@loop
                                imageVectorCode += "\n              curveTo(${x1}F, ${y1}F, ${x2}F, ${y2}F, ${x3}F, ${y3}F)"
                                i += 6
                            }
                        }
                    }
                    'c' -> {
                        if (values.size % 6 == 0) {
                            var i = 0
                            repeat(values.size / 6) {
                                val x1 = values.getOrNull(i) ?: return@loop
                                val y1 = values.getOrNull(i + 1) ?: return@loop
                                val x2 = values.getOrNull(i + 2) ?: return@loop
                                val y2 = values.getOrNull(i + 3) ?: return@loop
                                val x3 = values.getOrNull(i + 4) ?: return@loop
                                val y3 = values.getOrNull(i + 5) ?: return@loop
                                imageVectorCode += "\n              curveToRelative(${x1}F, ${y1}F, ${x2}F, ${y2}F, ${x3}F, ${y3}F)"
                                i += 6
                            }
                        }
                    }
                    'S' -> {
                        if (values.size % 4 == 0) {
                            var i = 0
                            repeat(values.size / 4) {
                                val x1 = values.getOrNull(i) ?: return@loop
                                val y1 = values.getOrNull(i + 1) ?: return@loop
                                val x2 = values.getOrNull(i + 2) ?: return@loop
                                val y2 = values.getOrNull(i + 3) ?: return@loop
                                imageVectorCode += "\n              reflectiveCurveTo(${x1}F, ${y1}F, ${x2}F, ${y2}F)"
                                i += 4
                            }
                        }
                    }
                    's' -> {
                        if (values.size % 4 == 0) {
                            var i = 0
                            repeat(values.size / 4) {
                                val x1 = values.getOrNull(i) ?: return@loop
                                val y1 = values.getOrNull(i + 1) ?: return@loop
                                val x2 = values.getOrNull(i + 2) ?: return@loop
                                val y2 = values.getOrNull(i + 3) ?: return@loop
                                imageVectorCode += "\n              reflectiveCurveToRelative(${x1}F, ${y1}F, ${x2}F, ${y2}F)"
                                i += 4
                            }
                        }
                    }
                    'Q' -> {
                        if (values.size % 4 == 0) {
                            var i = 0
                            repeat(values.size / 4) {
                                val x1 = values.getOrNull(i) ?: return@loop
                                val y1 = values.getOrNull(i + 1) ?: return@loop
                                val x2 = values.getOrNull(i + 2) ?: return@loop
                                val y2 = values.getOrNull(i + 3) ?: return@loop
                                imageVectorCode += "\n              quadTo(${x1}F, ${y1}F, ${x2}F, ${y2}F)"
                                i += 4
                            }
                        }
                    }
                    'q' -> {
                        if (values.size % 4 == 0) {
                            var i = 0
                            repeat(values.size / 4) {
                                val x1 = values.getOrNull(i) ?: return@loop
                                val y1 = values.getOrNull(i + 1) ?: return@loop
                                val x2 = values.getOrNull(i + 2) ?: return@loop
                                val y2 = values.getOrNull(i + 3) ?: return@loop
                                imageVectorCode += "\n              quadToRelative(${x1}F, ${y1}F, ${x2}F, ${y2}F)"
                                i += 4
                            }
                        }
                    }
                    'T' -> {
                        if (values.size % 2 == 0) {
                            var i = 0
                            repeat(values.size / 2) {
                                val x = values.getOrNull(i) ?: return@loop
                                val y = values.getOrNull(i + 1) ?: return@loop
                                imageVectorCode += "\n              reflectiveQuadTo(${x}F, ${y}F)"
                                i += 2
                            }
                        }
                    }
                    't' -> {
                        if (values.size % 2 == 0) {
                            var i = 0
                            repeat(values.size / 2) {
                                val x = values.getOrNull(i) ?: return@loop
                                val y = values.getOrNull(i + 1) ?: return@loop
                                imageVectorCode += "\n              reflectiveQuadToRelative(${x}F, ${y}F)"
                                i += 2
                            }
                        }
                    }
                    'A' -> {
                        if (values.size % 7 == 0) {
                            var i = 0
                            repeat(values.size / 7) {
                                val rx = values.getOrNull(i) ?: return@loop
                                val ry = values.getOrNull(i + 1) ?: return@loop
                                val xAxisRotation = values.getOrNull(i + 2) ?: return@loop
                                val largeArcFlag = values.getOrNull(i + 3) == 1F
                                val sweepFlag = values.getOrNull(i + 4) == 1F
                                val x = values.getOrNull(i + 5) ?: return@loop
                                val y = values.getOrNull(i + 6) ?: return@loop
                                imageVectorCode += "\n              arcTo(${rx}F, ${ry}F, ${xAxisRotation}F, ${largeArcFlag}, ${sweepFlag}, ${x}F, ${y}F)"
                                i += 7
                            }
                        }
                    }
                    'a' -> {
                        if (values.size % 7 == 0) {
                            var i = 0
                            repeat(values.size / 7) {
                                val rx = values.getOrNull(i) ?: return@loop
                                val ry = values.getOrNull(i + 1) ?: return@loop
                                val xAxisRotation = values.getOrNull(i + 2) ?: return@loop
                                val largeArcFlag = values.getOrNull(i + 3) == 1F
                                val sweepFlag = values.getOrNull(i + 4) == 1F
                                val x = values.getOrNull(i + 5) ?: return@loop
                                val y = values.getOrNull(i + 6) ?: return@loop
                                imageVectorCode += "\n              arcToRelative(${rx}F, ${ry}F, ${xAxisRotation}F, ${largeArcFlag}, ${sweepFlag}, ${x}F, ${y}F)"
                                i += 7
                            }
                        }
                    }
                    'Z', 'z' -> {
                        if (index < path.path.lastIndex) {
                            imageVectorCode += "\n"
                        }
                    }
                }
            }
            imageVectorCode += if (isMaterialIcon) {
                "\n              close()\n" +
                        "            }"
            } else {
                "\n              close()\n" +
                        "        }"
            }
        }
        imageVectorCode += if (isMaterialIcon) {
            "\n" +
                    "        }\n" +
                    "        return _[iconName]!!\n" +
                    "    }\n" +
                    "\n" +
                    "private var _[iconName]: ImageVector? = null\n" +
                    "\n" +
                    "@Preview\n" +
                    "@Composable\n" +
                    "@Suppress(\"UnusedPrivateMember\")\n" +
                    "private fun Icon[IconName]Preview() {\n" +
                    "    Image(imageVector = Icons.[IconName], contentDescription = null)\n" +
                    "}"
        } else {
            ".build()\n" +
                    "        return _[iconName]!!\n" +
                    "    }\n" +
                    "private var _[iconName]: ImageVector? = null\n" +
                    "\n" +
                    "@Preview\n" +
                    "@Composable\n" +
                    "@Suppress(\"UnusedPrivateMember\")\n" +
                    "private fun Icon[IconName]Preview() {\n" +
                    "    Image(imageVector = Icons.[IconName], contentDescription = null)\n" +
                    "}"
        }
        return imageVectorCode
    }

    fun toImageVector(): ImageVector =
        ImageVector.Builder(
            defaultWidth = if (max(width, height) < DEFAULT_SIZE) width * 2 else width,
            defaultHeight = if (max(width, height) < DEFAULT_SIZE) height * 2 else height,
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
        ).apply {
            paths.forEach {
                with(it) {
                    path(
                        fill = SolidColor(pathFillColor),
                        fillAlpha = pathFillAlpha,
                        stroke = pathStrokeColor?.let { strokeColor -> SolidColor(strokeColor) },
                        strokeAlpha = pathStrokeAlpha,
                        strokeLineWidth = pathStrokeLineWidth,
                        strokeLineCap = pathStrokeLineCap,
                        strokeLineJoin = pathStrokeLineJoin,
                        strokeLineMiter = pathStrokeLineMiter,
                        pathFillType = pathFillType,
                        pathBuilder = getPathBuilder(path),
                    )
                }
            }
        }.build()

    private fun getPathBuilder(list: List<Pair<Char, List<Float>>>): (PathBuilder.() -> Unit) = {
        list.forEach { (type, values) ->
            when (type) {
                'M' -> {
                    val x = values.getOrNull(0) ?: return@forEach
                    val y = values.getOrNull(1) ?: return@forEach
                    moveTo(x, y)
                }
                'm' -> {
                    val x = values.getOrNull(0) ?: return@forEach
                    val y = values.getOrNull(1) ?: return@forEach
                    moveToRelative(x, y)
                }
                'L' -> {
                    if (values.size % 2 == 0) {
                        var i = 0
                        repeat(values.size / 2) {
                            val x = values.getOrNull(i) ?: return@forEach
                            val y = values.getOrNull(i + 1) ?: return@forEach
                            lineTo(x, y)
                            i += 2
                        }
                    }
                }
                'l' -> {
                    if (values.size % 2 == 0) {
                        var i = 0
                        repeat(values.size / 2) {
                            val x = values.getOrNull(i) ?: return@forEach
                            val y = values.getOrNull(i + 1) ?: return@forEach
                            lineToRelative(x, y)
                            i += 2
                        }
                    }
                }
                'H' -> {
                    val x = values.getOrNull(0) ?: return@forEach
                    horizontalLineTo(x)
                }
                'h' -> {
                    val x = values.getOrNull(0) ?: return@forEach
                    horizontalLineToRelative(x)
                }
                'V' -> {
                    val y = values.getOrNull(0) ?: return@forEach
                    verticalLineTo(y)
                }
                'v' -> {
                    val y = values.getOrNull(0) ?: return@forEach
                    verticalLineToRelative(y)
                }
                'C' -> {
                    if (values.size % 6 == 0) {
                        var i = 0
                        repeat(values.size / 6) {
                            val x1 = values.getOrNull(i) ?: return@forEach
                            val y1 = values.getOrNull(i + 1) ?: return@forEach
                            val x2 = values.getOrNull(i + 2) ?: return@forEach
                            val y2 = values.getOrNull(i + 3) ?: return@forEach
                            val x3 = values.getOrNull(i + 4) ?: return@forEach
                            val y3 = values.getOrNull(i + 5) ?: return@forEach
                            curveTo(x1, y1, x2, y2, x3, y3)
                            i += 6
                        }
                    }
                }
                'c' -> {
                    if (values.size % 6 == 0) {
                        var i = 0
                        repeat(values.size / 6) {
                            val x1 = values.getOrNull(i) ?: return@forEach
                            val y1 = values.getOrNull(i + 1) ?: return@forEach
                            val x2 = values.getOrNull(i + 2) ?: return@forEach
                            val y2 = values.getOrNull(i + 3) ?: return@forEach
                            val x3 = values.getOrNull(i + 4) ?: return@forEach
                            val y3 = values.getOrNull(i + 5) ?: return@forEach
                            curveToRelative(x1, y1, x2, y2, x3, y3)
                            i += 6
                        }
                    }
                }
                'S' -> {
                    if (values.size % 4 == 0) {
                        var i = 0
                        repeat(values.size / 4) {
                            val x1 = values.getOrNull(i) ?: return@forEach
                            val y1 = values.getOrNull(i + 1) ?: return@forEach
                            val x2 = values.getOrNull(i + 2) ?: return@forEach
                            val y2 = values.getOrNull(i + 3) ?: return@forEach
                            reflectiveCurveTo(x1, y1, x2, y2)
                            i += 4
                        }
                    }
                }
                's' -> {
                    if (values.size % 4 == 0) {
                        var i = 0
                        repeat(values.size / 4) {
                            val x1 = values.getOrNull(i) ?: return@forEach
                            val y1 = values.getOrNull(i + 1) ?: return@forEach
                            val x2 = values.getOrNull(i + 2) ?: return@forEach
                            val y2 = values.getOrNull(i + 3) ?: return@forEach
                            reflectiveCurveToRelative(x1, y1, x2, y2)
                            i += 4
                        }
                    }
                }
                'Q' -> {
                    if (values.size % 4 == 0) {
                        var i = 0
                        repeat(values.size / 4) {
                            val x1 = values.getOrNull(i) ?: return@forEach
                            val y1 = values.getOrNull(i + 1) ?: return@forEach
                            val x2 = values.getOrNull(i + 2) ?: return@forEach
                            val y2 = values.getOrNull(i + 3) ?: return@forEach
                            quadTo(x1, y1, x2, y2)
                            i += 4
                        }
                    }
                }
                'q' -> {
                    if (values.size % 4 == 0) {
                        var i = 0
                        repeat(values.size / 4) {
                            val x1 = values.getOrNull(i) ?: return@forEach
                            val y1 = values.getOrNull(i + 1) ?: return@forEach
                            val x2 = values.getOrNull(i + 2) ?: return@forEach
                            val y2 = values.getOrNull(i + 3) ?: return@forEach
                            quadToRelative(x1, y1, x2, y2)
                            i += 4
                        }
                    }
                }
                'T' -> {
                    if (values.size % 2 == 0) {
                        var i = 0
                        repeat(values.size / 2) {
                            val x = values.getOrNull(i) ?: return@forEach
                            val y = values.getOrNull(i + 1) ?: return@forEach
                            reflectiveQuadTo(x, y)
                            i += 2
                        }
                    }
                }
                't' -> {
                    if (values.size % 2 == 0) {
                        var i = 0
                        repeat(values.size / 2) {
                            val x = values.getOrNull(i) ?: return@forEach
                            val y = values.getOrNull(i + 1) ?: return@forEach
                            reflectiveQuadToRelative(x, y)
                            i += 2
                        }
                    }
                }
                'A' -> {
                    if (values.size % 7 == 0) {
                        var i = 0
                        repeat(values.size / 7) {
                            val rx = values.getOrNull(i) ?: return@forEach
                            val ry = values.getOrNull(i + 1) ?: return@forEach
                            val xAxisRotation = values.getOrNull(i + 2) ?: return@forEach
                            val largeArcFlag = values.getOrNull(i + 3) == 1F
                            val sweepFlag = values.getOrNull(i + 4) == 1F
                            val x = values.getOrNull(i + 5) ?: return@forEach
                            val y = values.getOrNull(i + 6) ?: return@forEach
                            arcTo(rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x, y)
                            i += 7
                        }
                    }
                }
                'a' -> {
                    if (values.size % 7 == 0) {
                        var i = 0
                        repeat(values.size / 7) {
                            val rx = values.getOrNull(i) ?: return@forEach
                            val ry = values.getOrNull(i + 1) ?: return@forEach
                            val xAxisRotation = values.getOrNull(i + 2) ?: return@forEach
                            val largeArcFlag = values.getOrNull(i + 3) == 1F
                            val sweepFlag = values.getOrNull(i + 4) == 1F
                            val x = values.getOrNull(i + 5) ?: return@forEach
                            val y = values.getOrNull(i + 6) ?: return@forEach
                            arcToRelative(rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x, y)
                            i += 7
                        }
                    }
                }
            }
        }
        close()
    }

    companion object {

        val DEFAULT_SIZE = 200.dp
        const val DEFAULT_VIEWPORT_SIZE = 200F
    }
}
