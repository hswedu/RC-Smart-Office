package com.rcsmartoffice.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import java.io.OutputStreamWriter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.xml.sax.helpers.DefaultHandler
import javax.xml.parsers.SAXParserFactory

private val Navy = Color(0xFF08152E)
private val Blue = Color(0xFF1677E8)
private val Paper = Color(0xFFFFFEFC)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RCOfficeApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RCOfficeApp() {
    val context = LocalContext.current
    var docTitle by remember { mutableStateOf("নতুন ডকুমেন্ট") }
    var body by remember { mutableStateOf("") }
    var pageSize by remember { mutableStateOf("A4") }
    var fontName by remember { mutableStateOf("Noto Sans Bengali") }
    var fontSize by remember { mutableStateOf(16f) }
    var showPageMenu by remember { mutableStateOf(false) }
    var showFontMenu by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Offline Editor প্রস্তুত") }

    val saveLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri != null) {
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                OutputStreamWriter(stream, Charsets.UTF_8).use {
                    it.write("$docTitle\n\n$body")
                }
            }
            status = "ফাইল সেভ হয়েছে"
        }
    }

    val openLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val imported = readDocxText(context.contentResolver.openInputStream(uri)!!)
                body = imported
                docTitle = uri.lastPathSegment?.substringAfterLast('/')?.substringBeforeLast('.')
                    ?.takeIf { it.isNotBlank() } ?: "Imported document"
                status = "DOCX খোলা হয়েছে"
            } catch (e: Exception) {
                status = "DOCX খোলা যায়নি: ফাইলটি পরীক্ষা করুন"
            }
        }
    }

    MaterialTheme(colorScheme = lightColorScheme(
        primary = Blue, secondary = Navy, background = Color(0xFFF1F4F9)
    )) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("RC Smart Office", color = Color.White, fontSize = 19.sp)
                            Text("Professional Document Editor", color = Color(0xFFB9D5FF), fontSize = 11.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy),
                    actions = {
                        IconButton(onClick = { openLauncher.launch(arrayOf(
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                        )) }) {
                            Icon(Icons.Default.FolderOpen, "Open", tint = Color.White)
                        }
                        IconButton(onClick = { saveLauncher.launch("$docTitle.docx") }) {
                            Icon(Icons.Default.Save, "Save", tint = Color.White)
                        }
                    }
                )
            },
            bottomBar = {
                Surface(color = Navy) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(status, color = Color.White, fontSize = 12.sp)
                        Text("Page: $pageSize", color = Color(0xFFB9D5FF), fontSize = 12.sp)
                    }
                }
            }
        ) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = docTitle, onValueChange = { docTitle = it },
                    label = { Text("Document name") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Formatting", style = MaterialTheme.typography.titleSmall)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box {
                                OutlinedButton(onClick = { showFontMenu = true }) { Text(fontName, fontSize = 12.sp) }
                                DropdownMenu(expanded = showFontMenu, onDismissRequest = { showFontMenu = false }) {
                                    listOf("Noto Sans Bengali", "Arial", "Times New Roman", "Roboto", "Kalpurush", "SutonnyMJ*").forEach {
                                        DropdownMenuItem(text = { Text(it) }, onClick = {
                                            fontName = it; showFontMenu = false
                                            status = if (it == "SutonnyMJ*") "SutonnyMJ ফন্ট ফাইল যুক্ত হলে সক্রিয় হবে" else "ফন্ট নির্বাচন: $it"
                                        })
                                    }
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            Text("${fontSize.toInt()} pt")
                            Slider(
                                value = fontSize, onValueChange = { fontSize = it },
                                valueRange = 10f..36f, modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box {
                                OutlinedButton(onClick = { showPageMenu = true }) { Text("Page: $pageSize") }
                                DropdownMenu(expanded = showPageMenu, onDismissRequest = { showPageMenu = false }) {
                                    listOf("A4", "A3", "Letter", "Legal").forEach {
                                        DropdownMenuItem(text = { Text(it) }, onClick = {
                                            pageSize = it; showPageMenu = false
                                        })
                                    }
                                }
                            }
                            OutlinedButton(onClick = { status = "Bold toolbar পরবর্তী ধাপে যুক্ত হবে" }) { Text("B") }
                            OutlinedButton(onClick = { status = "Italic toolbar পরবর্তী ধাপে যুক্ত হবে" }) { Text("I") }
                            OutlinedButton(onClick = { status = "Underline toolbar পরবর্তী ধাপে যুক্ত হবে" }) { Text("U") }
                        }
                    }
                }

                // Paper-like editing surface. DOCX pagination/layout engine is a later milestone.
                Card(
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 560.dp),
                    colors = CardDefaults.cardColors(containerColor = Paper),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.fillMaxWidth().padding(22.dp)) {
                        Text("RC SMART OFFICE", color = Blue, fontSize = 10.sp)
                        Spacer(Modifier.height(12.dp))
                        BasicTextField(
                            value = body,
                            onValueChange = { body = it; status = "Editing…" },
                            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 480.dp),
                            textStyle = TextStyle(color = Color(0xFF202633), fontSize = fontSize.sp, lineHeight = (fontSize * 1.55f).sp),
                            decorationBox = { inner ->
                                Box {
                                    if (body.isEmpty()) Text(
                                        "এখানে লেখা শুরু করুন…\n\nবাংলা ও English লিখুন।",
                                        color = Color.LightGray, fontSize = fontSize.sp
                                    )
                                    inner()
                                }
                            }
                        )
                    }
                }
                Text(
                    "Prototype: এখন বেসিক DOCX Export ও পেজ সাইজ লেখা হয়। Import, পূর্ণ Word pagination, PDF, Bijoy conversion এবং font embedding এখনো বাকি।",
                    color = Color(0xFF6B7280), fontSize = 11.sp
                )
            }
        }
    }
}


private fun xmlEscape(value: String): String = value
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
    .replace("'", "&apos;")

private fun readDocxText(input: java.io.InputStream): String {
    val zip = java.util.zip.ZipInputStream(input)
    var documentXml: ByteArray? = null

    while (true) {
        val entry = zip.nextEntry ?: break
        if (entry.name == "word/document.xml") {
            documentXml = zip.readBytes()
            break
        }
        zip.closeEntry()
    }

    zip.close()
    val xml = documentXml ?: error("word/document.xml not found")
    val result = StringBuilder()
    var inText = false

    val factory = javax.xml.parsers.SAXParserFactory.newInstance()
    factory.isNamespaceAware = true

    factory.newSAXParser().parse(
        xml.inputStream(),
        object : org.xml.sax.helpers.DefaultHandler() {
            override fun startElement(
                uri: String?,
                localName: String?,
                qName: String?,
                attrs: org.xml.sax.Attributes?
            ) {
                val name = localName?.ifBlank { qName } ?: qName.orEmpty()
                if (name == "t") inText = true
                if (name == "p" && result.isNotEmpty() &&
                    !result.endsWith("\n")
                ) {
                    result.append('\n')
                }
            }

            override fun characters(ch: CharArray, start: Int, length: Int) {
                if (inText) result.append(ch, start, length)
            }

            override fun endElement(
                uri: String?,
                localName: String?,
                qName: String?
            ) {
                val name = localName?.ifBlank { qName } ?: qName.orEmpty()
                if (name == "t") inText = false
                if (name == "p") result.append('\n')
            }
        }
    )

    return result.toString().trimEnd()
}
