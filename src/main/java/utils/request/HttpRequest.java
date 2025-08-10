package utils.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.Config;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.internal.collections.Pair;
import utils.request.path.IPath;
import utils.request.exception.HttpsException;

import java.io.File;
import java.util.*;
import java.util.function.Supplier;

import static io.restassured.RestAssured.given;
import static utils.AllureUtils.*;

public class HttpRequest {

    private static final Set<Integer> SUCCESS_CODES = Set.of(200, 201, 202, 204, 205);
    private static final Set<Integer> RETRYABLE_CODES = Set.of(409, 410, 429, 500, 502);

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";

    private final String getResponseType = "GET";
    private final String postResponseType = "POST";
    private final String putResponseType = "PUT";
    private final String deleteResponseType = "DELETE";
    private final String patchResponseType = "PATCH";

    private String baseApiUrl = Config.baseApiUrl();
    private String filesApiUrl = Config.baseFilesApiUrl();
    private boolean consoleLog = Config.consoleLog();

    public HttpRequest() {
        RestAssured.config = RestAssured.config().sslConfig(SSLConfig.sslConfig().allowAllHostnames());
    }

    /** Allow overriding base URLs & console logging at runtime */
    public HttpRequest configure(String baseUrl, String filesUrl, boolean console) {
        if (baseUrl != null && !baseUrl.isBlank()) this.baseApiUrl = baseUrl;
        if (filesUrl != null && !filesUrl.isBlank()) this.filesApiUrl = filesUrl;
        this.consoleLog = console;
        return this;
    }

    // ---------- Public simple API (String responses) ----------

    public String getRequest(Headers headers, IPath path, String... params) {
        return sendRequest(getResponseType, baseApiUrl, headers, null, path, params);
    }
    public String getBodyRequest(Headers headers, Object body, IPath path, String... params) {
        return sendRequest(getResponseType, baseApiUrl, headers, body, path, params);
    }

    public String postRequest(Headers headers, Object body, IPath path, String... params) {
        return sendRequest(postResponseType, baseApiUrl, headers, body, path, params);
    }

    public String putRequest(Headers headers, Object body, IPath path, String... params) {
        return sendRequest(putResponseType, baseApiUrl, headers, body, path, params);
    }

    public String deleteRequest(Headers headers, IPath path, String... params) {
        return sendRequest(deleteResponseType, baseApiUrl, headers, null, path, params);
    }

    /** OPTIONS (kept as in your note) */
    public Response baseOptionsRequestWithAuthorizedUser(final String bearerToken, final String endpoint) {
        RequestSpecification spec = given()
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                .header(HEADER_AUTHORIZATION, bearerToken);
        if (consoleLog) spec.log().all();
        return spec.when().options(endpoint).then().extract().response();
    }

    // ---------- Multipart upload ----------

    public String postRequestForUploadFile(
            final String fileToken,
            final List<Pair<String, File>> filePairsList,
            final List<Pair<String, String>> stringPairsList,
            String endpoint
    ) {
        var req = given()
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                .header(HEADER_AUTHORIZATION, "Bearer " + fileToken)
                .contentType("multipart/form-data");

        if (filePairsList != null) filePairsList.forEach(p -> req.multiPart(p.first(), p.second()));
        if (stringPairsList != null) stringPairsList.forEach(p -> req.multiPart(p.first(), p.second()));

        if (consoleLog) req.log().all();

        Response response = req.when().post(filesApiUrl + endpoint);
        String responseAsString = response.then().extract().asString();

        attach("POST multipart " + endpoint, null, response, responseAsString);

        if (!SUCCESS_CODES.contains(response.statusCode())) {
            throw new HttpsException("Bad request: expected = " + SUCCESS_CODES + ", actual = "
                    + response.statusCode() + "\nError message:\n" + responseAsString);
        }
        return responseAsString;
    }

    // ---------- Core sendRequest with retry/backoff & Allure hook ----------

    private String sendRequest(String method, String baseUrl, Headers headers, Object body, IPath rawPath, String... params) {
        String path = formatPath(rawPath, params);

        RequestSpecification req = given()
                .config(RestAssured.config().sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation()))
                .headers(mergedHeaders(headers))
                .contentType(CONTENT_TYPE_JSON);

        if (body != null) req.body(body);
        if (consoleLog) req.log().all();

        Supplier<Response> call = () -> invoke(method, req, baseUrl + path);

        int attempts = 0;
        int max = Math.max(0, Config.retryMax());
        long delay = 500;
        Response resp;

        while (true) {
            attempts++;
            resp = call.get();

            if (consoleLog) System.out.println(method + " " + rawPath.getDescription() + " -> " + resp.statusCode());

            if (!RETRYABLE_CODES.contains(resp.statusCode()) || attempts > max) break;

            sleep(delay);
            delay = Math.min(delay * 2, 4000);
        }

        String bodyStr = safeString(body);
        String response = resp.then().extract().asString();
        attach(method + " " + path, bodyStr, resp, response);

        if (!SUCCESS_CODES.contains(resp.statusCode())) {
            throw new HttpsException("Bad request: expected status_code = " + SUCCESS_CODES +
                    ", actual = " + resp.statusCode() + "\nError message:\n" + response);
        }
        return response;
    }

    private Map<String, Object> mergedHeaders(Headers headers) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(HEADER_ACCEPT_LANGUAGE, Config.acceptLang());
        map.put(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        String token = Config.bearer();
        if (!token.isBlank()) map.put(HEADER_AUTHORIZATION, "Bearer " + token);

        if (headers != null && headers.getSize() != 0) {
            String[] kv = headers.getHeader();
            map.put(kv[0], kv[1]);
        }
        return map;
    }

    private Response invoke(String method, RequestSpecification req, String url) {
        return switch (method) {
            case postResponseType -> req.post(url);
            case putResponseType  -> req.put(url);
            case patchResponseType -> req.patch(url);
            case deleteResponseType -> req.delete(url);
            default     -> req.get(url);
        };
    }

    private static String formatPath(IPath path, String... params) {
        String result = path.url();
        for (String p : params) result = result.replaceFirst("%s", p);
        return result;
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new RuntimeException(e); }
    }

    private static String safeString(Object o) {
        return (o == null) ? "(no body)" : String.valueOf(o);
    }

    private void attach(String title, String requestBody, Response resp, String respBody) {
        try {
            // mask basic sensitive artifacts
            String reqMasked = Sensitive.mask(requestBody);
            String respMasked = Sensitive.mask(respBody);
            addAttachmentToReport("HTTP: " + title, getAllureReportMessage(resp, respMasked, reqMasked, title));
        } catch (Throwable ignored) {
            // no-op if Allure helper isn't present
        }
    }

    /** Minimal built-in masking to avoid leaking tokens/emails if AllureHelper not used elsewhere */
    private static final class Sensitive {
        private static String mask(String s) {
            if (s == null) return null;
            return s.replaceAll("(?i)Bearer\\s+[A-Za-z0-9._-]+", "Bearer ****")
                    .replaceAll("([\\w.%+-])([\\w.%+-]*)(@[^\\s\"']+)", "$1***$3");
        }
    }
}