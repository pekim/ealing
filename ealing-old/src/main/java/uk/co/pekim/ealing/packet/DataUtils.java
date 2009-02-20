package uk.co.pekim.ealing.packet;

import org.apache.commons.io.EndianUtils;
import org.apache.commons.lang.ArrayUtils;

public class DataUtils {
    public static short getU8(byte[] data, int offset) {
        return (short) (data[offset] & 0xff);
    }
    
    public static int getU16(byte[] data, int offset) {
        return EndianUtils.readSwappedUnsignedShort(data, offset);
    }
    
    public static short getS16(byte[] data, int offset) {
        return EndianUtils.readSwappedShort(data, offset);
    }
    
    public static long getU32(byte[] data, int offset) {
        return EndianUtils.readSwappedUnsignedInteger(data, offset);
    }

    public static float getF32(byte[] data, int offset) {
        return EndianUtils.readSwappedFloat(data, offset);
    }

    public static void putU16(byte[] data, int u16, int offset) {
        EndianUtils.writeSwappedShort(data, offset, (short) u16);
    }

    public static String getNullTerminatedString(byte[] data, int offset) {
        int index = ArrayUtils.indexOf(data, (byte) 0, offset);
        if (index == ArrayUtils.INDEX_NOT_FOUND) {
            return null;
        }
        int length = index - offset;
        return new String(data, offset, length);
    }
}
