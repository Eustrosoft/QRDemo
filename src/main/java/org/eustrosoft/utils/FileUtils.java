package org.eustrosoft.utils;

import org.eustrosoft.entitites.File;

import java.util.List;

public final class FileUtils {

    private FileUtils() {

    }

    public static int getFileIndex(Long fileId, List<File> files) {
        if (fileId == null || files == null) {
            throw new IllegalArgumentException("FileId and files can not be null");
        }
        for (int i = 0; i < files.size(); i++) {
            if (fileId.equals(files.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }
}
