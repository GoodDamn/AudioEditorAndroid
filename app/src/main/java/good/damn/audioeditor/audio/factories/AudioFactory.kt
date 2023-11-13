package good.damn.audioeditor.audio.factories

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import good.damn.audioeditor.audio.AudioWAV
import good.damn.audioeditor.utils.ByteUtils
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.math.pow

class AudioFactory {

    companion object {

        private const val TAG = "AudioFactory"

        fun createWav(
            fromUri: Uri?,
            context: Context
        ): AudioWAV? {
            if (fromUri == null) {
                return null
            }

            val resolver = context.contentResolver ?:return null

            return createWav(resolver.openInputStream(fromUri))
        }

        fun createWav(
            inputStream: InputStream?
        ): AudioWAV? {

            if (inputStream == null) {
                return null;
            }

            val buffer4 = ByteArray(4)
            val buffer2 = ByteArray(2)

            val audioWav = AudioWAV()

            inputStream.read(buffer4) // RIFF
            audioWav.wavType = String(buffer4, Charset.forName("US-ASCII"))

            inputStream.read(buffer4)
            audioWav.fileSize = ByteUtils.integer(buffer4)

            inputStream.read(buffer4) // WAVE
            inputStream.read(buffer4) // "fmt " format chunk

            inputStream.read(buffer4) // format chunk size
            audioWav.formatChunkSize = ByteUtils.integer(buffer4)

            inputStream.read(buffer2)
            audioWav.compressionCode = ByteUtils.Short(buffer2,0)

            inputStream.read(buffer2)
            audioWav.numberOfChannels = ByteUtils.Short(buffer2,0)

            inputStream.read(buffer4)
            audioWav.sampleRate = ByteUtils.integer(buffer4)

            inputStream.read(buffer4)
            audioWav.bitRate = ByteUtils.integer(buffer4)

            inputStream.read(buffer2)
            audioWav.blockAlign = ByteUtils.Short(buffer2,0)

            inputStream.read(buffer2)
            audioWav.bitDepth = ByteUtils.Short(buffer2, 0)

            inputStream.read(buffer4) // "data" tag
            inputStream.read(buffer4)
            audioWav.dataSize = ByteUtils.integer(buffer4,0)

            // Read samples

            val sampleSize = audioWav.bitDepth / 8

            val bufferSample = ByteArray(sampleSize)

            val maxAmplitude = 2.0.pow((audioWav.bitDepth - 1).toDouble()) - 1

            audioWav.samples = FloatArray(audioWav.dataSize / sampleSize)

            for (i in 0 until audioWav.samples!!.size) {
                inputStream.read(bufferSample)
                audioWav.samples!![i] = when(sampleSize) {
                    4 -> (ByteUtils.integer(bufferSample) / maxAmplitude).toFloat()
                    else -> (ByteUtils.Short(bufferSample,0) / maxAmplitude).toFloat()
                }
            }

            inputStream.close()

            return audioWav
        }
    }

}