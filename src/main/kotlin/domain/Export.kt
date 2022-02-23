package domain

import Svg
import java.io.File
import java.io.FileWriter
import java.io.IOException

object Export {

    fun exportFile(filePath: String, svg: Svg): Boolean {
        try {
            with(
                File(filePath).apply {
                    if (!parentFile.canWrite() && !parentFile.setWritable(true)) return false
                    createNewFile()
                }
            ) {
                FileWriter(this).apply {
                    write(
                        svg.`package` +
                            svg.imports +
                                svg.imageVectorCode.replace(
                                    "[IconName]",
                                    "${this@with.nameWithoutExtension.firstOrNull()?.uppercase()}" +
                                            "${this@with.nameWithoutExtension.substring(1)}",
                                ).replace(
                                    "[iconName]",
                                    "${this@with.nameWithoutExtension.firstOrNull()?.lowercase()}" +
                                            "${this@with.nameWithoutExtension.substring(1)}",
                                )
                    )
                    close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun getCodeToCopy(iconName: String, svg: Svg): String =
        svg.imports +
                svg.imageVectorCode.replace(
                    "[IconName]",
                    "${iconName.firstOrNull()?.uppercase()}${iconName.substring(1)}",
                ).replace(
                    "[iconName]",
                    "${iconName.firstOrNull()?.lowercase()}${iconName.substring(1)}",
                )
}
