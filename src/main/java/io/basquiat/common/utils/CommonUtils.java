package io.basquiat.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * commmon utils
 * created by basquiat
 */
@Component
@RequiredArgsConstructor
public class CommonUtils {

    /**
     * Object convert to json String
     *
     * @param object
     * @return String
     */
    public static String convertJson(Object object) {
        String result = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * json String convert Object
     *
     * @param content
     * @param clazz
     * @return T
     * @throws Exception
     */
    public static <T> T convertObject(String content, Class<T> clazz) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        T object = (T) mapper.readValue(content, clazz);
        return object;
    }

}
