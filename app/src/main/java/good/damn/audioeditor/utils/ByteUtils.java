package good.damn.audioeditor.utils;

import android.util.Log;

public class ByteUtils {

    public static byte[] fixedPointNumber(float val) {
        short v = (short) (val * 1000f);
        return new byte[] {
                (byte) ((v >> 8) & 0xff),
                (byte) (v & 0xff)
        };
    }

    public static float fixedPointNumber(byte[] in) {
        short v = (short) ((in[0] & 0xff) << 8 | (in[1] & 0xff));
        return v / 1000f;
    }

    public static byte[] integer(int val) {
        return new byte[] {
                (byte) ((val >> 24) & 0xff),
                (byte) ((val >> 16) & 0xff),
                (byte) ((val >> 8) & 0xff),
                (byte) (val & 0xff)
        };
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
        return new byte[] {
                (byte) ((val >> 8) & 0xff),
                (byte) (val & 0xff)
        };
    }

    public static short Short(byte[] in, int offset) {
        return (short) ((in[offset+1] & 0xff) << 8 |
                in[offset] & 0xff);
    }

    public static byte[] Long(long val) {
        return new byte[] {
                (byte) ((val >> 56) & 0xff),
                (byte) ((val >> 48) & 0xff),
                (byte) ((val >> 40) & 0xff),
                (byte) ((val >> 32) & 0xff),
                (byte) ((val >> 24) & 0xff),
                (byte) ((val >> 16) & 0xff),
                (byte) ((val >> 8) & 0xff),
                (byte) (val & 0xff)
        };
    }

    public static long Long(byte[] in, int offset) {
        return (long) (in[offset] & 0xff) << 56 |
                (long) (in[offset + 1] & 0xff) << 48 |
                (long) (in[offset + 2] & 0xff) << 40 |
                (long) (in[offset + 3] & 0xff) << 32 |
                (long) (in[offset + 4] & 0xff) << 24 |
                (in[offset+5] & 0xff) << 16 |
                (in[offset+6] & 0xff) << 8 |
                in[offset+7] & 0xff;
    }

}
