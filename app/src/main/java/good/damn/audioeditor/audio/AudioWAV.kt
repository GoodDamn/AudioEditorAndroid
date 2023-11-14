package good.damn.audioeditor.audio

class AudioWAV {
    var chunksSize: Int = 20
    var volume: Float = 1.0f
    var samples: FloatArray? = null
    var dataSize: Int = -1
    var bitDepth: Short = 8 // bits
    var blockAlign: Short = 1
    var bitRate: Int = 192000
    var sampleRate: Int = 64000
    var numberOfChannels: Short = 1
    var compressionCode: Short = 1 // PCM
    var formatChunkSize: Int = 16
    var fileSize: Int = 0
    var wavType: String = ""


    override fun toString(): String {
        return "WAV Type: $wavType\n" +
               "File size: $fileSize\n" +
               "Compression code: $compressionCode\n" +
               "Channels: $numberOfChannels\n" +
               "Sample rate: $sampleRate\n" +
               "Bit rate: $bitRate\n" +
               "Block align: $blockAlign\n" +
               "Bit depth: $bitDepth\n" +
               "Data size: $dataSize\n" +
               "Samples count: ${samples?.size}"
    }
}