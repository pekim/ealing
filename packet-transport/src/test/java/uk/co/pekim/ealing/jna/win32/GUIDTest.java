package uk.co.pekim.ealing.jna.win32;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.co.pekim.ealing.jna.win32.GUID;

public class GUIDTest {
    @Test
    public void testToStringReal() {
        GUID guid = new GUID(0x2c9c45c2L, 0x8e7d, 0x4c08, 0xa1, 0x2d, 0x81, 0x6b, 0xba, 0xe7, 0x22, 0xc0);
        assertEquals("2C9C45C2-8E7D-4C08-A12D-816BBAE722C0", guid.toString());
    }

    @Test
    public void testToStringZeroes() {
        GUID guid = new GUID(0x00000000L, 0x0000, 0x0000, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00);
        assertEquals("00000000-0000-0000-0000-000000000000", guid.toString());
    }

    @Test
    public void testToStringFs() {
        GUID guid = new GUID(0xffffffffL, 0xffff, 0xffff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff);
        assertEquals("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", guid.toString());
    }

    @Test
    public void testToStringAlternateBits10() {
        GUID guid = new GUID(0xaaaaaaaaL, 0xaaaa, 0xaaaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa);
        assertEquals("AAAAAAAA-AAAA-AAAA-AAAA-AAAAAAAAAAAA", guid.toString());
    }

    @Test
    public void testToStringAlternateBits01() {
        GUID guid = new GUID(0x55555555L, 0x5555, 0x5555, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55);
        assertEquals("55555555-5555-5555-5555-555555555555", guid.toString());
    }
}
