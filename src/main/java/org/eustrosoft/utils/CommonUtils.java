package org.eustrosoft.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static org.eustrosoft.Constants.EMPTY_JSON;

public final class CommonUtils {

    public static String mergeDataAndGetString(
            String originalData,
            String newData
    ) throws JsonProcessingException {
        if (StringUtils.isEmpty(originalData) || EMPTY_JSON.equalsIgnoreCase(originalData)) {
            return newData;
        }
        ObjectMapper mapper = new ObjectMapper();
        Map originalDataMap = mapper.readValue(originalData, Map.class);
        Map newDataMap = mapper.readValue(newData, Map.class);
        originalDataMap.putAll(newDataMap);

        return mapper.writeValueAsString(originalDataMap);
    }

    public static <T> List<T> iterableToList(Iterable<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        Iterator<T> iterator = list.iterator();
        List<T> returnList = new ArrayList<>();
        while (iterator.hasNext()) {
            returnList.add(iterator.next());
        }
        return returnList;
    }

    private CommonUtils() {

    }
}
