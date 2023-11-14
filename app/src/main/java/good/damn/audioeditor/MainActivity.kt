package good.damn.audioeditor

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import good.damn.audioeditor.audio.AudioWAV
import good.damn.audioeditor.audio.factories.AudioFactory
import good.damn.audioeditor.views.SamplesView
import java.io.File

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private var mAudioWav: AudioWAV? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val metrics = resources.displayMetrics

        val rootLayout = LinearLayout(this)
        rootLayout.orientation = LinearLayout.VERTICAL

        val textViewInfo = TextView(this)
        textViewInfo.text = "-------"

        val btnSelectFile = Button(this)
        btnSelectFile.text = "Select file"

        val btnSaveFile = Button(this)
        btnSaveFile.text = "Save file"
        btnSaveFile.isEnabled = false

        val sampleView = SamplesView(this)
        sampleView.mSamples = 5

        val fileBrowser = registerForActivityResult(ActivityResultContracts
            .GetContent()) { uri: Uri? ->
            Thread {
                mAudioWav = AudioFactory.createWav(uri,this);
                runOnUiThread {
                    textViewInfo.text = mAudioWav.toString()

                    if (mAudioWav == null || mAudioWav!!.samples == null) {
                        return@runOnUiThread
                    }

                    sampleView.mInputSamples = mAudioWav!!.samples!!
                    btnSaveFile.isEnabled = true
                }
            }.start()
        }

        btnSaveFile.setOnClickListener {
            if (mAudioWav == null) {
                return@setOnClickListener
            }

            btnSaveFile.isEnabled = false

            // Push changes

            mAudioWav!!.volume = sampleView.mAmplitude

            val samples = mAudioWav!!.samples!!

            mAudioWav!!.fromCrop = (sampleView.getCropLeftFraction()
                    * samples.size).toInt()

            mAudioWav!!.toCrop = (sampleView.getCropRightFraction()
                * samples.size).toInt()

            val dir = Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_DOCUMENTS)
            val outFile = File(dir, "fromAE.wav")

            AudioFactory.saveWav(outFile,mAudioWav!!)

            textViewInfo.text = "READY TO LISTEN"
        }

        btnSelectFile.setOnClickListener {
            fileBrowser.launch("audio/*")
        }

        rootLayout.addView(btnSelectFile)
        rootLayout.addView(textViewInfo)
        rootLayout.addView(btnSaveFile)
        rootLayout.addView(sampleView, -1,
            (metrics.heightPixels * 0.45f).toInt())

        setContentView(rootLayout)
    }
}