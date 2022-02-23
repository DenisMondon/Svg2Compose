import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import domain.Export
import domain.SvgPathParser
import domain.UnknownColors
import domain.VectorDrawableParser
import model.SvgData
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

data class Svg(
    val pathDecomposed: String,
    val imageVectorCode: String,
    val imageVector: ImageVector?,
    val `package`: String,
    val imports: String,
    val fileName: String?,
)

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun MainScreen() {
    MaterialTheme(colors = darkColors()) {
        val clipboardManager = LocalClipboardManager.current
        var currentTabIndex by remember { mutableStateOf(0) }
//        var svgFileTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
        var vectorDrawableTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
        var svgPathTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
        var svg: Svg? by remember { mutableStateOf(null) }
        var showImageBackground by remember { mutableStateOf(false) }
        var showImageBlackBackground by remember { mutableStateOf(false) }
        var showIconNameDialog by remember { mutableStateOf(false) }
        var showCodeCopiedDialog by remember { mutableStateOf(false) }
        var showChooseFileDialog by remember { mutableStateOf(false) }
        var showSaveFileDialog by remember { mutableStateOf(false) }
        var unknownColors by remember { mutableStateOf(emptySet<String>()) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TabRow(selectedTabIndex = currentTabIndex) {
//                Tab(
//                    selected = currentTabIndex == 0,
//                    onClick = {
//                        pathDecomposed = ""
//                        pathConverted = ""
//                        imageVector = null
//                        currentTabIndex = 0
//                    },
//                    text = { Text(text = "SVG file") },
//                )
                Tab(
                    selected = currentTabIndex == 0,
                    onClick = {
                        svg = null
                        currentTabIndex = 0
                    },
                    text = { Text(text = "Vector Drawable") },
                )
                Tab(
                    selected = currentTabIndex == 1,
                    onClick = {
                        svg = null
                        currentTabIndex = 1
                    },
                    text = { Text(text = "SVG path") },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(16.dp))
                when (currentTabIndex) {
//                0 -> {
//                    OutlinedTextField(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 16.dp),
//                        colors = TextFieldDefaults.outlinedTextFieldColors(textColor = White),
//                        value = svgFileTextFieldValue,
//                        onValueChange = { svgFileTextFieldValue = it },
//                        label = { Text(text = "SVG file") },
//                    )
//                }
                    0 -> {
                        OutlinedButton(onClick = { showChooseFileDialog = true }) {
                            Text("Pick a file")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedTextField(
                            modifier = Modifier.weight(1F),
                            colors = TextFieldDefaults.outlinedTextFieldColors(textColor = White),
                            maxLines = 4,
                            value = vectorDrawableTextFieldValue,
                            onValueChange = { vectorDrawableTextFieldValue = it },
                            label = { Text(text = "Vector Drawable") },
                        )
                    }
                    1 -> {
                        OutlinedTextField(
                            modifier = Modifier.weight(1F),
                            colors = TextFieldDefaults.outlinedTextFieldColors(textColor = White),
                            maxLines = 4,
                            value = svgPathTextFieldValue,
                            onValueChange = { svgPathTextFieldValue = it },
                            label = { Text(text = "SVG path") },
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        val svgData = buildSvgData(
                            currentTabIndex = currentTabIndex,
                            vectorDrawableValue = vectorDrawableTextFieldValue.text,
                            svgPathValue = svgPathTextFieldValue.text,
                            onColorsNotFound = { unknownColors = it },
                        ) ?: return@Button

                        svg = Svg(
                            pathDecomposed = svgData.toPathDecomposed(),
                            imageVectorCode = svgData.toImageVectorCode(),
                            imageVector = svgData.toImageVector(),
                            `package` = svgData.toPackage(),
                            imports = svgData.toImports(),
                            fileName = null,
                        )
                    },
                ) {
                    Text(text = "Convert".toUpperCase(Locale.current))
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = LightGray)
            val pathDecomposed = svg?.pathDecomposed
            val imageVectorCode = svg?.imageVectorCode
            if (!pathDecomposed.isNullOrBlank() && !imageVectorCode.isNullOrBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    backgroundColor = DarkGray,
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        var showContent by remember { mutableStateOf(false) }
                        ItemHeader(
                            modifier = Modifier.clickable { showContent = !showContent },
                            imageVector = svg?.imageVector,
                            selectedFileName = svg?.fileName,
                            onShowIconNameButtonClick = { showIconNameDialog = true },
                            onShowExportButtonClick = { showSaveFileDialog = true },
                            isExpanded = showContent,
                        )
                        if (showContent) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .verticalScroll(rememberScrollState()),
                            ) {
                                Text(
                                    modifier = Modifier
                                        .weight(1F)
                                        .wrapContentWidth(Alignment.End),
                                    text = pathDecomposed,
                                    lineHeight = 32.sp,
                                    color = White,
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = imageVectorCode, lineHeight = 32.sp, color = White)
                                svg?.imageVector?.let {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column(modifier = Modifier.weight(1F)) {
                                        var imageSizeRatio by remember { mutableStateOf(1F) }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                text = "Show background",
                                                color = White,
                                                modifier = Modifier.clickable {
                                                    showImageBackground = !showImageBackground
                                                },
                                            )
                                            Checkbox(
                                                checked = showImageBackground,
                                                onCheckedChange = { showImageBackground = it },
                                            )
                                            if (showImageBackground) {
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Text(
                                                    text = "Use black background",
                                                    color = White,
                                                    modifier = Modifier.clickable {
                                                        showImageBlackBackground = !showImageBlackBackground
                                                    },
                                                )
                                                Checkbox(
                                                    checked = showImageBlackBackground,
                                                    onCheckedChange = { showImageBlackBackground = it },
                                                )
                                            }
                                        }
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            OutlinedButton(
                                                onClick = { imageSizeRatio /= 1.5F },
                                            ) {
                                                Text("-")
                                            }
                                            OutlinedButton(
                                                onClick = { imageSizeRatio *= 1.5F },
                                            ) {
                                                Text("+")
                                            }
                                        }
                                        Image(
                                            modifier = Modifier
                                                .size(
                                                    width = it.defaultWidth * imageSizeRatio,
                                                    height = it.defaultHeight * imageSizeRatio,
                                                )
                                                .background(
                                                    if (showImageBackground) {
                                                        if (showImageBlackBackground) Black else White
                                                    } else {
                                                        Color.Unspecified
                                                    }
                                                ),
                                            imageVector = it,
                                            contentDescription = null,
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }
        if (showIconNameDialog) {
            IconNameDialog(
                onValidateClick = { iconName ->
                    svg?.let {
                        clipboardManager.setText(AnnotatedString(Export.getCodeToCopy(iconName, it)))
                    }
                    showIconNameDialog = false
                    showCodeCopiedDialog = true
                },
                onCancelClick = { showIconNameDialog = false },
            )
        } else if (showCodeCopiedDialog) {
            CodeCopiedDialog(onCloseClick = { showCodeCopiedDialog = false })
        } else if (showChooseFileDialog) {
            ChooseFileDialog(
                onCloseRequest = { directoryPath, fileName ->
                    showChooseFileDialog = false
                    if (!directoryPath.isNullOrBlank() && !fileName.isNullOrBlank()) {
                        val file = File(directoryPath, fileName)
                        vectorDrawableTextFieldValue = TextFieldValue(file.readText())
                        svgPathTextFieldValue = TextFieldValue("")
                        val svgData = buildSvgData(
                            currentTabIndex = currentTabIndex,
                            vectorDrawableValue = vectorDrawableTextFieldValue.text,
                            svgPathValue = svgPathTextFieldValue.text,
                            onColorsNotFound = { unknownColors = it },
                        ) ?: return@ChooseFileDialog

                        svg = Svg(
                            pathDecomposed = svgData.toPathDecomposed(),
                            imageVectorCode = svgData.toImageVectorCode(),
                            imageVector = svgData.toImageVector(),
                            `package` = svgData.toPackage(),
                            imports = svgData.toImports(),
                            fileName = file.nameWithoutExtension,
                        )
                    }
                },
            )
        } else if (showSaveFileDialog) {
            var svgFileName = svg?.fileName?.takeIf { it.isNotBlank() }?.let {
                "${it.first().uppercase()}${it.substring(1)}"
            } ?: "Icon.kt"
            var index = svgFileName.indexOf('_')
            while (index != -1) {
                val start = svgFileName.substring(0, index)
                val end = svgFileName.substring(index).replaceFirst("_", "")
                svgFileName = if (end.isBlank()) start
                else "$start${end.first().uppercase()}${end.substring(1)}"
                index = svgFileName.indexOf('_')
            }
            SaveFileDialog(
                onCloseRequest = { directoryPath, fileName ->
                    showSaveFileDialog = false
                    if (!directoryPath.isNullOrBlank() && !fileName.isNullOrBlank()) {
                        svg?.let { Export.exportFile(File(directoryPath, fileName).path, it) }
                            ?.takeIf { it }
                            ?.also { svg = svg?.copy(fileName = fileName.substringBeforeLast(".kt")) }
                    }
                },
                fileName = svgFileName,
            )
        } else if (unknownColors.isNotEmpty()) {
            AskForValidColorDialog(
                colorsValue = unknownColors,
                onUnknownColorsMapped = { validColors ->
                    UnknownColors.unknownColors.putAll(validColors)
                    unknownColors = emptySet()

                    if (validColors.isNotEmpty()) {
                        val svgData = buildSvgData(
                            currentTabIndex = currentTabIndex,
                            vectorDrawableValue = vectorDrawableTextFieldValue.text,
                            svgPathValue = svgPathTextFieldValue.text,
                            onColorsNotFound = { unknownColors = it },
                        ) ?: return@AskForValidColorDialog

                        svg = Svg(
                            pathDecomposed = svgData.toPathDecomposed(),
                            imageVectorCode = svgData.toImageVectorCode(),
                            imageVector = svgData.toImageVector(),
                            `package` = svgData.toPackage(),
                            imports = svgData.toImports(),
                            fileName = svg?.fileName,
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun ItemHeader(
    modifier: Modifier = Modifier,
    imageVector: ImageVector?,
    selectedFileName: String?,
    onShowIconNameButtonClick: () -> Unit,
    onShowExportButtonClick: () -> Unit,
    isExpanded: Boolean,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary)
            .defaultMinSize(minHeight = 72.dp)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        imageVector?.let {
            Image(
                modifier = Modifier.size(64.dp),
                imageVector = it,
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        selectedFileName?.let {
            Text(text = it, color = White)
        }
        Spacer(modifier = Modifier.weight(1F))
        OutlinedButton(
            onClick = onShowIconNameButtonClick,
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = Color.Unspecified,
                contentColor = White,
            ),
            border = BorderStroke(width = 1.dp, color = White),
        ) {
            Text(text = "Copy code".toUpperCase(Locale.current))
        }
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedButton(
            onClick = onShowExportButtonClick,
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = Color.Unspecified,
                contentColor = White,
            ),
            border = BorderStroke(width = 1.dp, color = White),
        ) {
            Text(text = "Export".toUpperCase(Locale.current))
        }
        Spacer(modifier = Modifier.width(24.dp))
        Icon(
            imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            tint = White,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(12.dp))
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
private fun AskForValidColorDialog(
    colorsValue: Set<String>,
    onUnknownColorsMapped: (validColors: Map<String, String>) -> Unit,
) {
    val validColorValues = remember { mutableStateMapOf<String, Pair<TextFieldValue, Boolean>>() }

    colorsValue.forEach { validColorValues[it] = TextFieldValue("") to true }

    Dialog(
        onCloseRequest = { onUnknownColorsMapped(emptyMap()) },
        title = "Enter valid colors",
        onKeyEvent = { keyEvent ->
            if (keyEvent.key == Key.Escape) onUnknownColorsMapped(emptyMap())
            true
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                validColorValues.forEach { color ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        border = BorderStroke(1.dp, LightGray),
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                            Text(text = "Unknown color \"${color.key}\"", color = White)
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(textColor = White),
                                value = color.value.first,
                                onValueChange = { validColorValues[color.key] = it to true },
                                label = { Text("Hexadecimal color") },
                                placeholder = { Text("#FF00FF") },
                                isError = !color.value.second
                            )
                        }
                    }
                }
            }
            TextButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 8.dp),
                onClick = {
                    var hasError = false
                    validColorValues.forEach { key, (textFieldValue, _) ->
                        if (UnknownColors.decodeColor(textFieldValue.text) == null) {
                            validColorValues[key] = textFieldValue to false
                            hasError = true
                        }
                    }
                    if (!hasError) {
                        onUnknownColorsMapped(
                            validColorValues.map { entry -> entry.key to entry.value.first.text }.toMap()
                        )
                    }
                },
            ) {
                Text("Confirm")
            }
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
private fun IconNameDialog(onValidateClick: (iconName: String) -> Unit, onCancelClick: () -> Unit) {
    val iconName = remember { mutableStateOf(TextFieldValue("")) }
    var hasError by remember { mutableStateOf(false) }
    Dialog(
        onCloseRequest = onCancelClick,
        title = "Choose an icon name",
        onKeyEvent = { keyEvent ->
            if (keyEvent.key == Key.Escape) onCancelClick()
            true
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(textColor = White),
                    value = iconName.value,
                    onValueChange = { iconName.value = it },
                    label = { Text("Icon name") },
                    isError = hasError,
                )
            }
            TextButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 8.dp),
                onClick = {
                    if (iconName.value.text.isNotBlank()) {
                        onValidateClick(iconName.value.text)
                    } else {
                        hasError = true
                    }
                },
            ) {
                Text("Copy code to clipboard")
            }
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
private fun CodeCopiedDialog(onCloseClick: () -> Unit) {
    Dialog(
        onCloseRequest = onCloseClick,
        title = "Code copied!",
        onKeyEvent = { keyEvent ->
            if (keyEvent.key == Key.Escape) onCloseClick()
            true
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
                    .wrapContentSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                text = "The ImageVector code has been copied to your clipboard!",
                color = White,
                textAlign = TextAlign.Center,
            )
            TextButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 8.dp),
                onClick = onCloseClick,
            ) {
                Text("OK")
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
private fun ChooseFileDialog(onCloseRequest: (directoryPath: String?, fileName: String?) -> Unit) {
    FileDialog(null as? Frame, "Choose a Vector Drawable file", FileDialog.LOAD).apply {
        file = "*.xml"
        isVisible = true
        onCloseRequest(directory, file)
        isVisible = false
    }
}

@ExperimentalComposeUiApi
@Composable
private fun SaveFileDialog(fileName: String, onCloseRequest: (directoryPath: String?, fileName: String?) -> Unit) {
    FileDialog(null as? Frame, "Choose a directory", FileDialog.SAVE).apply {
        file = "$fileName.kt"
        isVisible = true
        onCloseRequest(directory, file)
        isVisible = false
    }
}

private fun buildSvgData(
    currentTabIndex: Int,
    vectorDrawableValue: String,
    svgPathValue: String,
    onColorsNotFound: (colorValues: Set<String>) -> Unit,
): SvgData? =
//  if (currentTabIndex == 0 && svgFileTextFieldValue.text.isNotBlank()) {
//      XmlParser.model.SvgData()
//  }
    if (currentTabIndex == 0 && vectorDrawableValue.isNotBlank()) {
        VectorDrawableParser.toSvgData(vectorDrawableValue, onColorsNotFound)
    } else if (currentTabIndex == 1 && svgPathValue.isNotBlank()) {
        SvgPathParser.toSvgData(svgPath = svgPathValue)
    } else {
        null
    }
