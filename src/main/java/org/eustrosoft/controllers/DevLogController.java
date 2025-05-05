package org.eustrosoft.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Unallowed to use this endpoint"),
        @ApiResponse(code = 500, message = "Error on server")
})
@RestController
@RequestMapping("/v1/api/unsecured/dev-log")
public class DevLogController {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

    public static final String CLASSPATH_VERSIONS_RESOURCES = "devlog/*";
    public static final String SPLIT_SYMBOL = "_";
    public static final String FILE_DATE_PATTERN = "dd.MM.yyyy";

    @ApiOperation(value = "Get versions list with changes")
    @GetMapping("/versions")
    public List<VersionDto> getVersionsList() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] files = resolver.getResources(CLASSPATH_VERSIONS_RESOURCES);
            List<VersionDto> versions = new ArrayList<>();
            for (Resource f : files) {
                VersionDto dto = new VersionDto();
                String[] filePaths = f.getFilename().split(SPLIT_SYMBOL);
                if (filePaths.length != 3) {
                    continue;
                }
                try {
                    String version = filePaths[0];
                    String releaseDate = filePaths[1];
                    String author = filePaths[2];
                    dto.setVersion(version);
                    dto.setReleaseDate(releaseDate);
                    dto.setAuthor(author);
                    versions.add(dto);
                } catch (Exception e) {
                    LOGGER.log(Level.ALL, "Failed to map version file: " + f.getFilename());
                }
            }
            return versions.stream()
                    .sorted(new VersionDtoByModificationDateComparator())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @ApiOperation(value = "Get version info by version string")
    @GetMapping("/versions/{version}")
    public VersionDto getDevLog(@PathVariable("version") String version) {
        if (StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("Version is not provided");
        }

        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] files = resolver.getResources(CLASSPATH_VERSIONS_RESOURCES);
            for (Resource f : files) {
                VersionDto dto = new VersionDto();
                String[] filePaths = f.getFilename().split(SPLIT_SYMBOL);
                if (filePaths.length != 3) {
                    continue;
                }
                try {
                    String fileVersion = filePaths[0];

                    if (!fileVersion.equalsIgnoreCase(version)) {
                        continue;
                    }

                    String releaseDate = filePaths[1];
                    String author = filePaths[2];
                    dto.setVersion(fileVersion);
                    dto.setReleaseDate(releaseDate);
                    dto.setAuthor(author);
                    dto.setContent(StreamUtils.copyToString(f.getInputStream(), StandardCharsets.UTF_8));
                    return dto;
                } catch (Exception e) {
                    LOGGER.log(Level.ALL, "Failed to map version file: " + f.getFilename());
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    @Data
    class VersionDto {
        private String version;
        private String releaseDate;
        private String author;
        private String content;
    }

    class VersionDtoByModificationDateComparator implements Comparator<VersionDto> {

        @Override
        public int compare(VersionDto o1, VersionDto o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            String version1Date = o1.getReleaseDate();
            String version2Date = o2.getReleaseDate();
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FILE_DATE_PATTERN);
                Date date1 = simpleDateFormat.parse(version1Date);
                Date date2 = simpleDateFormat.parse(version2Date);

                return date2.compareTo(date1);
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
