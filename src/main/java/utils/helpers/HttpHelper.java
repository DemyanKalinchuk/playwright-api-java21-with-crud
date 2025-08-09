package utils.helpers;

import com.microsoft.playwright.options.RequestOptions;
import utils.json.JsonUtils;

import java.io.IOException;
import java.util.Map;

public final class HttpHelper {
    private HttpHelper() {}

    /** Build JSON body request options with proper header. */
    public static RequestOptions json(Object body) throws IOException {
        return RequestOptions.create()
                .setHeader("Content-Type", "application/json")
                .setHeader("Accept", "*/*")
                .setHeader("x-api-key", "reqres-free-v1")
                .setData(JsonUtils.toJson(body));
    }

    /** Add/replace headers. */
    public static RequestOptions headers(Map<String, String> headers) {
        RequestOptions option = RequestOptions.create();
        headers.forEach(option::setHeader);
        return option;
    }

    /** Build query params from a map. */
    public static RequestOptions query(Map<String, ?> params) {
        RequestOptions opt = RequestOptions.create();
        params.forEach((key, value) -> {
            if (value != null) opt.setQueryParam(key, String.valueOf(value)); // choose String overload
        });
        return opt;
    }

    /** Single query param convenience. */
    public static RequestOptions query(String key, Object value) {
        return RequestOptions.create().setQueryParam(key, String.valueOf(value));
    }
}
