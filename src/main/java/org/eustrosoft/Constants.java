package org.eustrosoft;

public class Constants {

    public static class Version {
        public static final String APPLICATION_VERSION = "v0.1.3";

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

    public static class Types {
        public static final String TYPE_PARTICIPANT = "PT";
        public static final String TYPE_ROLE = "RL";
        public static final String TYPE_QR = "QR";
        public static final String TYPE_QR_RANGE = "QRR";
        public static final String TYPE_FORM = "FM";
        public static final String TYPE_FORM_FIELD = "FF";
        public static final String TYPE_FILE = "FILE";
    }
}
