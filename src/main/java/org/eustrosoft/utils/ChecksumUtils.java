package org.eustrosoft.utils;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public final class ChecksumUtils {

    private ChecksumUtils() {

    }

    public static long getCRC32Checksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }
}
