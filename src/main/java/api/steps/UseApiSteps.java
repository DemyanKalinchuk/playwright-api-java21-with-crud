package api.steps;

import api.builders.BodyBuilder;
import api.pojo.users.Address;
import api.pojo.users.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import utils.request.HttpRequest;


/**
 * Single entry point for API “moves”.
 * - Builds POJOs (via PojoBuilders)
 * - Calls APIs (via RequestsHelper)
 * - Masks sensitive data for logs
 * - Returns either APIResponse or strongly-typed DTOs
 */

public class UseApiSteps extends HttpRequest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final BodyBuilder bodyBuilder = new BodyBuilder();

    // ===== USERS =====

    /** Create a minimal user */
    public APIResponse createUser(String first, String last, String email, String job) {
        User payload = bodyBuilder.buildNewUser(first, last, email, job);
        APIResponse res = post("/api/users", RequestOptions.create().setData(payload));
        attachMasked("POST /api/users", payload, res);
        return res;
    }

    /** Update a user (PUT full object) */
    public APIResponse updateUser(String id, String first, String last, String email, String job, Address address) {
        User payload = PojoBuilders.buildFullUser(id, first, last, email, job, address);
        APIResponse res = requests.put("/api/users/" + id, RequestOptions.create().setData(payload));
        attachMasked("PUT /api/users/{id}", payload, res);
        return res;
    }

}
