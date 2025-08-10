package utils.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import io.restassured.response.ValidatableResponse;

import java.lang.reflect.Method;

public final class Json {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private Json() {}

    public static JsonNode parse(Object bodyOrResponse) {
        String body = extractBody(bodyOrResponse);

        if (body == null || body.isBlank())
            throw new IllegalArgumentException("Response body is empty — expected JSON.");

        String head = body.stripLeading();
        if (head.startsWith("<!DOCTYPE") || head.startsWith("<html"))
            throw new IllegalArgumentException("Response looks like HTML, not JSON. Check BASE_URL and path.");

        try {
            return MAPPER.readTree(body);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to parse JSON. Body (trimmed): " + head.substring(0, Math.min(200, head.length())), e);
        }
    }

    public static String getAsText(Object bodyOrResponse, String field) {
        JsonNode node = parse(bodyOrResponse);
        JsonNode f = node.get(field);
        if (f == null || f.isNull())
            throw new IllegalArgumentException("JSON field '" + field + "' is missing or null.");
        return f.asText();
    }

    private static String extractBody(Object input) {
        if (input == null) return null;
        if (input instanceof String) return (String) input;
        if (input instanceof Response) return ((Response) input).asString();
        if (input instanceof ValidatableResponse) return ((ValidatableResponse) input).extract().asString();
        if (input instanceof ResponseBodyExtractionOptions) return ((ResponseBodyExtractionOptions) input).asString();

        // Fallback: handle other RestAssured types via reflection
        String cls = input.getClass().getName();
        if (cls.startsWith("io.restassured")) {
            try {
                // Try direct asString()
                Method asString = input.getClass().getMethod("asString");
                Object s = asString.invoke(input);
                if (s instanceof String) return (String) s;
            } catch (Exception ignore) {}

            try {
                // Try extract().asString()
                Method extract = input.getClass().getMethod("extract");
                Object ex = extract.invoke(input);
                if (ex != null) {
                    Method asString = ex.getClass().getMethod("asString");
                    Object s = asString.invoke(ex);
                    if (s instanceof String) return (String) s;
                }
            } catch (Exception ignore) {}
        }

        // If we’re here, someone passed Response.toString()
        String s = input.toString();
        if (s.startsWith("io.restassured.internal.RestAssuredResponseImpl@")) {
            throw new IllegalArgumentException(
                    "Got a RestAssured object (toString) instead of JSON. Call response.asString() before parsing.");
        }
        return s;
    }
}