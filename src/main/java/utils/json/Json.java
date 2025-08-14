package utils.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.response.ResponseBodyExtractionOptions;

public final class Json {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private Json() {}

    public static JsonNode parse(Object bodyOrResponse) {
        String body = extractBody(bodyOrResponse);
        if (body == null || body.isBlank())
            throw new IllegalArgumentException("Response body is empty — expected JSON.");

        String head = body.stripLeading();
        if (head.startsWith("<!DOCTYPE") || head.startsWith("<html"))
            throw new IllegalArgumentException("Response looks like HTML, not JSON. Check BASE_URL and path.");

        try {
            return objectMapper.readTree(body);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to parse JSON. Body (trimmed): " + head.substring(0, Math.min(200, head.length())), e
            );
        }
    }

    public static String getAsText(Object bodyOrResponse, String fieldName) {
        JsonNode root = parse(bodyOrResponse);
        JsonNode field = root.get(fieldName);
        if (field == null || field.isNull()) {
            throw new IllegalArgumentException("JSON field '" + fieldName + "' is missing or null.");
        }
        return field.asText();
    }

    /**
     * Maps the given JSON (String, Response, etc.) to the specified DTO class.
     *
     * @param bodyOrResponse JSON source (String, Response, ValidatableResponse, etc.)
     * @param clazz DTO class to map into
     * @return mapped DTO instance
     */
    public static <T> T read(Object bodyOrResponse, Class<T> clazz) {
        String body = extractBody(bodyOrResponse);
        if (body == null || body.isBlank())
            throw new IllegalArgumentException("Response body is empty — expected JSON for DTO mapping.");

        try {
            return objectMapper.readValue(body, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to map JSON to " + clazz.getSimpleName(), e);
        }
    }

    private static String extractBody(Object input) {
        if (input == null) return null;
        if (input instanceof String) return (String) input;
        if (input instanceof Response) return ((Response) input).asString();
        if (input instanceof ValidatableResponse) return ((ValidatableResponse) input).extract().asString();
        if (input instanceof ResponseBodyExtractionOptions) return ((ResponseBodyExtractionOptions) input).asString();

        // Common mistake: Response.toString()
        String s = input.toString();
        if (s.startsWith("io.restassured.internal.RestAssuredResponseImpl@"))
            throw new IllegalArgumentException("Got a RestAssured object (toString) instead of JSON. Use .asString().");
        return s;
    }
}
