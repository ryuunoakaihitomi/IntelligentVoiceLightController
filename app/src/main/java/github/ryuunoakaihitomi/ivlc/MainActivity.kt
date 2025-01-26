package github.ryuunoakaihitomi.ivlc

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainLayout() }
    }
}

@Preview
@Composable
fun MainLayout() {
    val labelAndIconMap = mapOf(
        "开灯" to Icons.Default.Home,
        "关灯" to Icons.Default.Close,
        "亮一点" to Icons.Default.KeyboardArrowUp,
        "暗一点" to Icons.Default.KeyboardArrowDown,
        "换颜色" to Icons.Default.Edit,
        "关于..." to Icons.Default.Info
    )
    val xCellCount = 2
    val yCellCount = 3
    val allCellCount = xCellCount * yCellCount
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        columns = GridCells.Fixed(xCellCount),
        content = {
            items(allCellCount) { index ->
                val btnLabel = labelAndIconMap.keys.elementAt(index)
                val btnIcon = labelAndIconMap.values.elementAt(index)
                val m = Modifier.height((LocalConfiguration.current.screenHeightDp / yCellCount).dp)
                if (index < allCellCount - 1) {
                    OpButton(btnLabel, btnIcon, m)
                } else {
                    AboutButton(btnLabel, btnIcon, m)
                }
            }
        }
    )
}


@Composable
fun OpButton(text: String, icon: ImageVector, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    MyButton(text, icon, modifier) {
        var tts: TextToSpeech? = null
        val speakCallback = Runnable {
            tts?.run {
                if (isSpeaking) stop()
                language = Locale.PRC
                setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {}

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        showFailurePrompt(ctx, "文字转语音引擎操作失败，错误码：$errorCode")
                    }
                })
                speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
            }
        }
        tts = TextToSpeech(ctx) {
            val success = it == TextToSpeech.SUCCESS
            println("tts init = $it, success = $success")
            if (success) speakCallback.run()
            else showFailurePrompt(ctx, "文字转语音引擎初始化失败！需要先安装引擎")
        }
    }
}

@Composable
fun AboutButton(text: String, icon: ImageVector, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    MyButton(text, icon, modifier) {
        val link = "https://github.com/ryuunoakaihitomi/IntelligentVoiceLightController"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        runCatching { ctx.startActivity(intent) }.onFailure {
            Toast.makeText(ctx, link, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun MyButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .padding(12.dp)
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icon, null, Modifier.size(64.dp))
            Text(
                text,
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

fun showFailurePrompt(ctx: Context, msg: String) {
    AlertDialog.Builder(ctx)
        .setTitle(msg)
        .setPositiveButton(android.R.string.ok, null)
        .show()
}