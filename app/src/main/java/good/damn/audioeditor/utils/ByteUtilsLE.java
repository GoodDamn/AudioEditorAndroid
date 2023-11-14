package good.damn.audioeditor.utils;

import android.util.Log;

import java.util.Arrays;

public class ByteUtilsLE {

    private static final String TAG = "ByteUtilsLE";

    public static byte[] integer(int val) {
        return integer(val, new byte[4]);
    }

    public static byte[] integer(int val, byte[] buffer) {
        buffer[0] = (byte) (val & 0xff);
        buffer[1] = (byte) ((val >> 8) & 0xff);
        buffer[2] = (byte) ((val >> 16) & 0xff);
        buffer[3] = (byte) ((val >> 24) & 0xff);
        return buffer;
    }

    public static int integer(byte[] in, int offset) {
        return (in[offset+3] & 0xff) << 24 |
                (in[offset+2] & 0xff) << 16 |
                (in[offset+1] & 0xff) << 8 |
                in[offset] & 0xff;
    }

    public static int integer(byte[] in) {
        return integer(in,0);
    }

    public static byte[] Short(short val) {
        return Short(val, new byte[2]);
    }

    public static byte[] Short(short val, byte[] buffer) {
        buffer[0] = (byte) (val & 0xff);
        buffer[1] = (byte) ((val >> 8) & 0xff);
        return buffer;
    }

    public static short Short(byte[] in, int offset) {
        return (short) ((in[offset+1] & 0xff) << 8 |
                in[offset] & 0xff);
    }

}
