package model

import androidx.compose.ui.graphics.vector.ImageVector

data class Svg(
    val pathDecomposed: String,
    val imageVectorCode: String,
    val imageVector: ImageVector?,
    val `package`: String,
    val imports: String,
    val fileName: String?,
)
