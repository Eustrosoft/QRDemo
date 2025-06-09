package org.eustrosoft;

public class Constants {

    public static class Version {
        public static final String APPLICATION_VERSION = "v0.2.6";

        public static final String APPLICATION_VERSION_TEXT = "Версия приложения: " + APPLICATION_VERSION;
    }

    // Other
    public static final String EMPTY_JSON = "{}";


    public static final Long FIRST_ZVER = 1L;
    public static final Long FIRST_ZRID = 1L;
    public static final Long FIRST_ZTOV = 0L;
    public static final Short FIRST_ZLVL = 31;
    public static final Long FIRST_ZPID = 0L;
    public static final Character FIRST_ZSTA = 'N';

    public static final Integer MAXIMUM_CHUNK_SIZE = 1024 * 1024;

    public static class Properties {
        public static final Integer DEFAULT_MAXIMUM_CHUNKS = 16;
        public static final String MAXIMUM_CHUNKS = "files.upload.chunks.maximum";
    }

    public static class Dictionary {
        public static final String NAME_CHUNK_SIZE = "CHUNK_SIZE";

        public static final String CODE_CHUNK_SIZE = "FILE_UPLOAD";
        public static final String CODE_DOWNLOAD_ALLOWED_MIME_TYPE = "DOWNLOAD_ALLOWED_MIME_TYPE";
    }

    // Sequence names
    public static final String QRDEMO = "QRDEMO"; // range key
}
