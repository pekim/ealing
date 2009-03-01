package uk.co.pekim.ealing.jna.win32;

import java.util.Formatter;

import com.sun.jna.Structure;

public class GUID extends Structure {
    public int data1;
    public short data2;
    public short data3;
    public byte[] data4;

    public GUID(long data1, int data2, int data3, int data4_0, int data4_1, int data4_2, int data4_3, int data4_4, int data4_5,
            int data4_6, int data4_7) {
        this();

        this.data1 = (int) data1;
        this.data2 = (short) data2;
        this.data3 = (short) data3;
        this.data4[0] = (byte) data4_0;
        this.data4[1] = (byte) data4_1;
        this.data4[2] = (byte) data4_2;
        this.data4[3] = (byte) data4_3;
        this.data4[4] = (byte) data4_4;
        this.data4[5] = (byte) data4_5;
        this.data4[6] = (byte) data4_6;
        this.data4[7] = (byte) data4_7;
    }

    public GUID() {
        this.data4 = new byte[8];
    }

    
    /**
     * Format the GUID according to Microsofts preferred format of
     * XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX.
     * 
     * @return the formatted GUID.
     */
    @Override
    public String toString() {
        Long group1 = new Long(data1 & 0xffffffffL);
        Integer group2 = new Integer(data2 & 0xffff);
        Integer group3 = new Integer(data3 & 0xffff);
        Integer group4_1 = new Integer(
                (((data4[0] & 0xff) << 8) + (data4[1] & 0xff)) & 0xffff
                );
        long group4_2_temp = 0;
        for(int b = 2; b < 8; b++) {
            group4_2_temp <<= 8;
            group4_2_temp += data4[b] & 0xffL;
        }
        Long group4_2 = new Long(group4_2_temp);

        Appendable buffer = new StringBuffer();
        Formatter formatter = new Formatter(buffer);
        formatter.format("%08X-%04X-%04X-%04X-%012X", group1, group2, group3, group4_1, group4_2);

        return buffer.toString();
    }
}
