package org.eustrosoft;

public class Constants {

    public static class Version {
        public static final String APPLICATION_VERSION = "v1.0.1";

        public static final String APPLICATION_VERSION_TEXT = "Версия приложения: " + APPLICATION_VERSION;
    }

    // Minio Paths

    // qrs/{qrCode}/{fieldName}/{fileName}
    public static final String MINIO_FILES_PATTERN = "/qrs/%s/%s/%s";
    public static final String MINIO_FILE_DIR_PATTERN = "/qrs/%s/%s";

    // Other
    public static final String EMPTY_JSON = "{}";

    public static final Integer CODES_FOR_RANGE = 15;

    public static final Long RANGE_START = 0x01070000L;
    public static final Long RANGE_END = 0x0107FFFFL;
}
