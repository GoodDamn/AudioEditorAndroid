package good.damn.audioeditor

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import good.damn.audioeditor.audio.AudioWAV
import good.damn.audioeditor.audio.factories.AudioFactory

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this)
        rootLayout.orientation = LinearLayout.VERTICAL

        val textViewInfo = TextView(this)
        textViewInfo.text = "-------"

        val btnSelectFile = Button(this)
        btnSelectFile.text = "Select file"

        val fileBrowser = registerForActivityResult(ActivityResultContracts
            .GetContent()) { uri: Uri? ->
            val audioWav = AudioFactory.createWav(uri,this);
            textViewInfo.text = audioWav.toString()
        }

        btnSelectFile.setOnClickListener {
            fileBrowser.launch("audio/*")
        }

        rootLayout.addView(textViewInfo)
        rootLayout.addView(btnSelectFile)

        setContentView(rootLayout)
    }
}