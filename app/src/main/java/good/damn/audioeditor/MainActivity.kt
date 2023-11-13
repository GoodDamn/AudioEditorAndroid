package good.damn.audioeditor

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import good.damn.audioeditor.audio.AudioWAV
import good.damn.audioeditor.audio.factories.AudioFactory
import good.damn.audioeditor.views.SamplesView

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

        val sampleView = SamplesView(this)
        sampleView.mSamples = 5

        val seekBarSample = SeekBar(this)
        seekBarSample.max = 350

        val fileBrowser = registerForActivityResult(ActivityResultContracts
            .GetContent()) { uri: Uri? ->

            Thread {
                val audioWav = AudioFactory.createWav(uri,this);
                runOnUiThread {
                    textViewInfo.text = audioWav.toString()

                    if (audioWav?.samples == null) {
                        return@runOnUiThread
                    }

                    sampleView.mInputSamples = audioWav.samples!!
                }
            }.start()
        }

        btnSelectFile.setOnClickListener {
            fileBrowser.launch("audio/*")
        }

        seekBarSample.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?,
                                           progress: Int, p2: Boolean) {
                sampleView.mSamples = progress
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        rootLayout.addView(textViewInfo)
        rootLayout.addView(btnSelectFile)
        rootLayout.addView(sampleView, -1, 250)
        rootLayout.addView(seekBarSample,-1,250)

        setContentView(rootLayout)
    }
}