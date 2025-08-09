package utils.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtils() {
    }

    /**
     * Parse JSON bytes into a Map<String,Object>.
     */
    public static Map<String, Object> readJson(byte[] jsonBytes) throws IOException {
        return MAPPER.readValue(jsonBytes, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Parse JSON string into a Map<String,Object>.
     */
    public static Map<String, Object> readJson(String json) throws IOException {
        return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Serialize an object to a JSON string.
     */
    public static String toJson(Object value) throws IOException {
        return MAPPER.writeValueAsString(value);
    }

    /**
     * Minimal JSON path: dot-separated keys; list indexes allowed (e.g., "data.0.id").
     */
    @SuppressWarnings("unchecked")
    public static <T> T jsonPath(Map<String, Object> root, String path, Class<T> type) {
        Object cur = root;
        for (String part : path.split("\\.")) {
            if (cur instanceof Map) {
                cur = ((Map<?, ?>) cur).get(part);
            } else if (cur instanceof List) {
                int idx = Integer.parseInt(part);
                cur = ((List<?>) cur).get(idx);
            } else {
                return null;
            }
            if (cur == null) return null;
        }
        return type.cast(cur);
    }
}
