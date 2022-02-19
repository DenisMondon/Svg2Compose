import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.SvgPathParser
import domain.UnknownColors
import domain.VectorDrawableParser
import model.SvgData

@ExperimentalMaterialApi
@Composable
fun MainScreen() {
    MaterialTheme(colors = darkColors()) {
        val clipboardManager = LocalClipboardManager.current
        var currentTabIndex by remember { mutableStateOf(0) }
//        var svgFileTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
        var vectorDrawableTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
        var svgPathTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
        var pathDecomposed by remember { mutableStateOf("") }
        var imageVectorCode by remember { mutableStateOf("") }
        var imageVector by remember { mutableStateOf<ImageVector?>(null) }
        var showImageBackground by remember { mutableStateOf(false) }
        var showImageBlackBackground by remember { mutableStateOf(false) }
        var showIconNameDialog by remember { mutableStateOf(false) }
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
                        pathDecomposed = ""
                        imageVectorCode = ""
                        imageVector = null
                        currentTabIndex = 0
                    },
                    text = { Text(text = "Vector Drawable file") },
                )
                Tab(
                    selected = currentTabIndex == 1,
                    onClick = {
                        pathDecomposed = ""
                        imageVectorCode = ""
                        imageVector = null
                        currentTabIndex = 1
                    },
                    text = { Text(text = "SVG path") },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                when (currentTabIndex) {
//                0 -> {
//                    OutlinedTextField(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 16.dp),
//                        colors = TextFieldDefaults.outlinedTextFieldColors(
//                            textColor = White,
//                        ),
//                        value = svgFileTextFieldValue,
//                        onValueChange = { svgFileTextFieldValue = it },
//                        label = { Text(text = "SVG file") },
//                    )
//                }
                    0 -> {
                        OutlinedTextField(
                            modifier = Modifier
                                .weight(1F)
                                .padding(horizontal = 16.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = White,
                            ),
                            maxLines = 4,
                            value = vectorDrawableTextFieldValue,
                            onValueChange = { vectorDrawableTextFieldValue = it },
                            label = { Text(text = "Vector Drawable file") },
                        )
                    }
                    1 -> {
                        OutlinedTextField(
                            modifier = Modifier
                                .weight(1F)
                                .padding(horizontal = 16.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = White,
                            ),
                            maxLines = 4,
                            value = svgPathTextFieldValue,
                            onValueChange = { svgPathTextFieldValue = it },
                            label = { Text(text = "SVG path") },
                        )
                    }
                }
                Button(
                    onClick = {
                        val svgData = buildSvgData(
                            currentTabIndex = currentTabIndex,
                            vectorDrawableValue = vectorDrawableTextFieldValue.text,
                            svgPathValue = svgPathTextFieldValue.text,
                            onColorsNotFound = { unknownColors = it },
                        ) ?: return@Button

                        pathDecomposed = svgData.toPathDecomposed()
                        imageVectorCode = svgData.toImageVectorCode()
                        imageVector = svgData.toImageVector()
                    },
                ) {
                    Text(text = "Convert".toUpperCase(Locale.current))
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            if (pathDecomposed.isNotBlank() && imageVectorCode.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Spacer(modifier = Modifier.weight(1F))
                    Button(
                        modifier = Modifier.weight(1F).wrapContentWidth(),
                        onClick = { showIconNameDialog = true },
                    ) {
                        Text(text = "Copy code".toUpperCase(Locale.current))
                    }
                    Row(
                        modifier = Modifier.weight(1F),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Show background",
                            color = White,
                            modifier = Modifier.clickable { showImageBackground = !showImageBackground },
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
                                modifier = Modifier.clickable { showImageBlackBackground = !showImageBlackBackground },
                            )
                            Checkbox(
                                checked = showImageBlackBackground,
                                onCheckedChange = { showImageBlackBackground = it },
                            )
                        }
                    }
                }
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
                    imageVector?.let {
                        Spacer(modifier = Modifier.width(4.dp))
                        Column(modifier = Modifier.weight(1F)) {
                            var imageSizeRatio by remember { mutableStateOf(1F) }
                            Row {
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
        if (showIconNameDialog) {
            IconNameDialog(
                onValidateClick = {
                    val path = imageVectorCode.replace(
                        "[IconName]",
                        "${it.firstOrNull()?.uppercase()}${it.substring(1)}",
                    ).replace(
                        "[iconName]",
                        "${it.firstOrNull()?.lowercase()}${it.substring(1)}",
                    )
                    clipboardManager.setText(AnnotatedString(path))
                    showIconNameDialog = false
                },
                onCancelClick = { showIconNameDialog = false },
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

                        pathDecomposed = svgData.toPathDecomposed()
                        imageVectorCode = svgData.toImageVectorCode()
                        imageVector = svgData.toImageVector()
                    }
                },
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun AskForValidColorDialog(
    colorsValue: Set<String>,
    onUnknownColorsMapped: (validColors: Map<String, String>) -> Unit,
) {
    val validColorValues = mutableMapOf<String, String>()
    AlertDialog(
        modifier = Modifier.focusable(true).focusTarget(),
        title = { Text("Enter a valid color for those unknown colors") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                colorsValue.forEach { colorValue ->
                    var validColor by remember { mutableStateOf(TextFieldValue("")) }
                    Text(colorValue)
                    OutlinedTextField(
                        value = validColor,
                        onValueChange = {
                            validColor = it
                            validColorValues[colorValue] = it.text
                        },
                        label = { Text("Hexadecimal color") },
                        placeholder = { Text("#FF00FF") },
                    )
                }
            }
        },
        buttons = {
            TextButton(onClick = { onUnknownColorsMapped(validColorValues) }) {
                Text("Valid those colors")
            }
        },
        onDismissRequest = { onUnknownColorsMapped(emptyMap()) },
    )
}

@ExperimentalMaterialApi
@Composable
private fun IconNameDialog(onValidateClick: (iconName: String) -> Unit, onCancelClick: () -> Unit) {
    val iconName = remember { mutableStateOf(TextFieldValue("")) }
    AlertDialog(
        title = { Text("Choose an icon name") },
        text = {
            OutlinedTextField(
                value = iconName.value,
                onValueChange = { iconName.value = it },
                label = { Text("Icon name") },
            )
        },
        buttons = {
            TextButton(
                onClick = { onValidateClick(iconName.value.text) },
            ) {
                Text("Copy code")
            }
        },
        onDismissRequest = onCancelClick,
    )
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
