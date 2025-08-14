package utils.request;

import config.Config;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.internal.collections.Pair;
import utils.request.exception.HttpsException;
import utils.request.path.IPath;

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

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_PATCH = "PATCH";

    private String baseApiUrl = Config.baseApiUrl();
    private String filesApiUrl = Config.baseFilesApiUrl();
    private boolean consoleLogEnabled = Config.consoleLog();

    public HttpRequest() {
        RestAssured.config = RestAssured.config().sslConfig(SSLConfig.sslConfig().allowAllHostnames());
    }

    /** Allow overriding base URLs & console logging at runtime */
    public HttpRequest configure(String baseUrl, String filesUrl, boolean console) {
        if (baseUrl != null && !baseUrl.isBlank()) this.baseApiUrl = baseUrl;
        if (filesUrl != null && !filesUrl.isBlank()) this.filesApiUrl = filesUrl;
        this.consoleLogEnabled = console;
        return this;
    }

    // ---------- Public simple API (String responses) ----------

    public String getRequest(Headers headers, IPath path, String... pathParams) {
        return sendRequest(METHOD_GET, baseApiUrl, headers, null, path, pathParams);
    }

    public String getBodyRequest(Headers headers, Object requestBody, IPath path, String... pathParams) {
        return sendRequest(METHOD_GET, baseApiUrl, headers, requestBody, path, pathParams);
    }

    public String postRequest(Headers headers, Object requestBody, IPath path, String... pathParams) {
        return sendRequest(METHOD_POST, baseApiUrl, headers, requestBody, path, pathParams);
    }

    public String putRequest(Headers headers, Object requestBody, IPath path, String... pathParams) {
        return sendRequest(METHOD_PUT, baseApiUrl, headers, requestBody, path, pathParams);
    }

    public String deleteRequest(Headers headers, IPath path, String... pathParams) {
        return sendRequest(METHOD_DELETE, baseApiUrl, headers, null, path, pathParams);
    }

    public Response baseOptionsRequestWithAuthorizedUser(final String bearerToken, final String endpoint) {
        RequestSpecification requestSpec = given()
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                .header(HEADER_AUTHORIZATION, bearerToken);
        if (consoleLogEnabled) requestSpec.log().all();
        return requestSpec.when().options(endpoint).then().extract().response();
    }

    // ---------- Multipart upload ----------

    public String postRequestForUploadFile(
            final String fileToken,
            final List<Pair<String, File>> filePairsList,
            final List<Pair<String, String>> stringPairsList,
            String endpoint
    ) {
        RequestSpecification requestSpec = given()
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                .header(HEADER_AUTHORIZATION, "Bearer " + fileToken)
                .contentType("multipart/form-data");

        if (filePairsList != null) {
            filePairsList.forEach(pair -> requestSpec.multiPart(pair.first(), pair.second()));
        }

        if (stringPairsList != null) {
            stringPairsList.forEach(pair -> requestSpec.multiPart(pair.first(), pair.second()));
        }

        if (consoleLogEnabled) requestSpec.log().all();

        Response response = requestSpec.when().post(filesApiUrl + endpoint);
        String responseBody = response.then().extract().asString();

        attach("POST multipart " + endpoint, null, response, responseBody);

        if (!SUCCESS_CODES.contains(response.statusCode())) {
            throw new HttpsException("Bad request: expected = " + SUCCESS_CODES + ", actual = "
                    + response.statusCode() + "\nError message:\n" + responseBody);
        }
        return responseBody;
    }

    // ---------- Core sendRequest with retry/backoff & Allure hook ----------

    private String sendRequest(String httpMethod,
                               String baseUrl,
                               Headers headers,
                               Object requestBody,
                               IPath pathTemplate,
                               String... pathParams) {

        String path = formatPath(pathTemplate, pathParams);

        RequestSpecification requestSpec = given()
                .config(RestAssured.config().sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation()))
                .headers(mergedHeaders(headers))
                .contentType(CONTENT_TYPE_JSON);

        if (requestBody != null) requestSpec.body(requestBody);
        if (consoleLogEnabled) requestSpec.log().all();

        Supplier<Response> invokeSupplier = () -> invoke(httpMethod, requestSpec, baseUrl + path);

        int attempt = 0;
        int maxAttempts = Math.max(0, Config.retryMax());
        long backoffMillis = 500;
        Response response;

        while (true) {
            attempt++;
            response = invokeSupplier.get();

            if (consoleLogEnabled) {
                System.out.println(httpMethod + " " + pathTemplate.getDescription() + " -> " + response.statusCode());
            }

            if (!RETRYABLE_CODES.contains(response.statusCode()) || attempt > maxAttempts) {
                break;
            }

            sleep(backoffMillis);
            backoffMillis = Math.min(backoffMillis * 2, 4000);
        }

        String requestBodyMasked = safeString(requestBody);
        String responseBody = response.then().extract().asString();
        attach(httpMethod + " " + path, requestBodyMasked, response, responseBody);

        String contentType = Optional.ofNullable(response.getHeader("Content-Type")).orElse("");
        boolean looksLikeHtml = contentType.contains("text/html") || responseBody.startsWith("<!DOCTYPE");
        String hint = looksLikeHtml
                ? "\nHint: Response is HTML — check BASE_URL vs endpoint (e.g., /posts is JSONPlaceholder, not ReqRes)."
                : "";

        if (!SUCCESS_CODES.contains(response.statusCode())) {
            throw new HttpsException("Bad request: expected status_code = " + SUCCESS_CODES +
                    ", actual = " + response.statusCode() + "\nError message:\n" + responseBody + hint);
        }

        return responseBody;
    }

    /** Raw POST for negative tests (no success check; returns Response). */
    public Response sendRequestDto(Headers headers, Object requestBody, IPath path, String... pathParams) {
        String url = baseApiUrl + formatPath(path, pathParams);

        RequestSpecification requestSpec = given()
                .config(RestAssured.config().sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation()))
                .headers(mergedHeaders(headers))
                .contentType(CONTENT_TYPE_JSON);

        if (requestBody != null) requestSpec.body(requestBody);
        if (consoleLogEnabled) requestSpec.log().all();

        Response response = requestSpec.post(url);

        try {
            String responseBody = response.then().extract().asString();
            attach("POST " + url,
                    (requestBody == null ? "(no body)" : String.valueOf(requestBody)),
                    response,
                    responseBody);
        } catch (Throwable ignored) {
            // no-op if Allure helper isn't present
        }

        return response;
    }

    private Map<String, Object> mergedHeaders(Headers customHeaders) {
        Map<String, Object> merged = new LinkedHashMap<>();

        merged.put(HEADER_ACCEPT_LANGUAGE, Config.acceptLang());
        merged.put(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        String bearerToken = Config.bearer();
        if (bearerToken != null && !bearerToken.isBlank()) {
            merged.put(HEADER_AUTHORIZATION, "Bearer " + bearerToken);
        }

        if (customHeaders != null && customHeaders.getSize() != 0) {
            String[] headerKeyValue = customHeaders.getHeader();
            merged.put(headerKeyValue[0], headerKeyValue[1]);
        }
        return merged;
    }

    private Response invoke(String httpMethod, RequestSpecification requestSpec, String url) {
        return switch (httpMethod) {
            case METHOD_POST   -> requestSpec.post(url);
            case METHOD_PUT    -> requestSpec.put(url);
            case METHOD_PATCH  -> requestSpec.patch(url);
            case METHOD_DELETE -> requestSpec.delete(url);
            default            -> requestSpec.get(url);
        };
    }

    private static String formatPath(IPath path, String... pathParams) {
        String formatted = path.url();
        for (String param : pathParams) {
            formatted = formatted.replaceFirst("%s", param);
        }
        return formatted;
    }

    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private static String safeString(Object object) {
        return (object == null) ? "(no body)" : String.valueOf(object);
    }

    private void attach(String title, String requestBody, Response response, String responseBody) {
        try {
            String maskedRequest = Sensitive.mask(requestBody);
            String maskedResponse = Sensitive.mask(responseBody);
            addAttachmentToReport("HTTP: " + title, getAllureReportMessage(response, maskedResponse, maskedRequest, title));
        } catch (Throwable ignored) {
            // no-op if Allure helper isn't present
        }
    }

    /** Minimal built-in masking to avoid leaking tokens/emails if AllureHelper not used elsewhere */
    private static final class Sensitive {
        private static String mask(String text) {
            if (text == null) return null;
            return text.replaceAll("(?i)Bearer\\s+[A-Za-z0-9._-]+", "Bearer ****")
                    .replaceAll("([\\w.%+-])([\\w.%+-]*)(@[^\\s\"']+)", "$1***$3");
        }
    }
}