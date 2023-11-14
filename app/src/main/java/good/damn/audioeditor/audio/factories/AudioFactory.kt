package good.damn.audioeditor.audio.factories

import android.content.Context
import android.net.Uri
import android.util.Log
import good.damn.audioeditor.audio.AudioWAV
import good.damn.audioeditor.utils.ByteUtilsLE
import java.io.File
import java.io.FileOutputStream
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
            audioWav.fileSize = ByteUtilsLE.integer(buffer4)

            inputStream.read(buffer4) // WAVE
            inputStream.read(buffer4) // "fmt " format chunk

            inputStream.read(buffer4) // format chunk size
            audioWav.formatChunkSize = ByteUtilsLE.integer(buffer4)

            inputStream.read(buffer2)
            audioWav.compressionCode = ByteUtilsLE.Short(buffer2,0)

            inputStream.read(buffer2)
            audioWav.numberOfChannels = ByteUtilsLE.Short(buffer2,0)

            inputStream.read(buffer4)
            audioWav.sampleRate = ByteUtilsLE.integer(buffer4)

            inputStream.read(buffer4)
            audioWav.bitRate = ByteUtilsLE.integer(buffer4)

            inputStream.read(buffer2)
            audioWav.blockAlign = ByteUtilsLE.Short(buffer2,0)

            inputStream.read(buffer2)
            audioWav.bitDepth = ByteUtilsLE.Short(buffer2, 0)

            inputStream.read(buffer4) // "data" tag
            inputStream.read(buffer4)
            audioWav.dataSize = ByteUtilsLE.integer(buffer4,0)

            audioWav.chunksSize = audioWav.fileSize - audioWav.dataSize

            // Read samples

            Log.d(TAG, "createWav: INFO: $audioWav")

            val sampleSize = audioWav.bitDepth / 8

            val maxAmplitude = 2.0.pow((audioWav.bitDepth - 1).toDouble()) - 1

            audioWav.samples = FloatArray(audioWav.dataSize / sampleSize)

            audioWav.fromCrop = 0
            audioWav.toCrop = audioWav.samples!!.size - 1

            for (i in 0 until audioWav.samples!!.size) {
                inputStream.read(buffer2,0,sampleSize)
                audioWav.samples!![i] = when(sampleSize) {
                    2 -> (ByteUtilsLE.Short(buffer2,0) / maxAmplitude).toFloat()
                    else -> (ByteUtilsLE.integer(buffer4) / maxAmplitude).toFloat()
                }
            }

            inputStream.close()

            return audioWav
        }

        fun saveWav(outFile: File, audioWav: AudioWAV) {
            if (!outFile.exists() && outFile.createNewFile()) {
                Log.d(TAG, "saveWav: FILE ${outFile.name} IS CREATED")
            }

            val dataSize = audioWav.toCrop - audioWav.fromCrop

            val outStream = FileOutputStream(outFile)

            val charset = Charset.forName("US-ASCII")

            outStream.write("RIFF".toByteArray(charset))

            outStream.write(ByteUtilsLE
                .integer(dataSize + audioWav.chunksSize))

            outStream.write("WAVE".toByteArray(charset))
            outStream.write("fmt ".toByteArray(charset))

            outStream.write(ByteUtilsLE
                .integer(audioWav.formatChunkSize))

            outStream.write(ByteUtilsLE
                .Short(audioWav.compressionCode),0,2)

            outStream.write(ByteUtilsLE
                .Short(audioWav.numberOfChannels),0,2)

            outStream.write(ByteUtilsLE
                .integer(audioWav.sampleRate))

            outStream.write(ByteUtilsLE
                .integer(audioWav.bitRate))

            outStream.write(ByteUtilsLE
                .Short(audioWav.blockAlign),0,2)

            outStream.write(ByteUtilsLE
                .Short(audioWav.bitDepth),0,2)

            outStream.write("data".toByteArray(charset))
            outStream.write(ByteUtilsLE
                .integer(dataSize))

            if (audioWav.samples == null) {
                return;
            }

            val sampleSize = audioWav.bitDepth / 8

            val maxAmp = 2.0.pow(audioWav.bitDepth - 1) - 1

            // ONLY FOR 16-bits !!!!
            for (sample in audioWav.fromCrop until audioWav.toCrop) {
                val digitalSample = (sample * maxAmp * audioWav.volume).toInt().toShort()
                outStream.write(ByteUtilsLE.Short(digitalSample))
            }

            outStream.close()
        }
    }

}