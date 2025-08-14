package smokeTests;

import api.pojo.auth.dto.NegativeAuthFlow;
import api.pojo.auth.dto.RegisterResponse;
import api.steps.UseApiSteps;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import utils.request.exception.HttpsException;

import static core.TestStepLogger.logStep;
import static org.testng.Assert.*;

public class AuthRegisterTests {
    private final UseApiSteps steps = new UseApiSteps();

    @Test(description = "Try to register with positive data")
    public void registerTestAndReturnIdAndTokenDto() {
        RegisterResponse resp = steps.registerDto("eve.holt@reqres.in", "pistol");
        assertNotNull(resp.getId(), "id should be present");
        assertNotNull(resp.getToken(), "token should be present");
        assertTrue(resp.getId() > 0, "id should be positive");
        assertFalse(resp.getToken().isBlank(), "token should not be blank");
    }

    @Test(description = "Try to register with negative data")
    public void registerWithoutPassTest() {
        try {
            logStep("Try to register without pass");
            steps.registerDto("sydney@fife", null);

            logStep("Check an error");
            fail("Expected HttpsException (400) for missing password");

        } catch (HttpsException e) {
            String msg = e.getMessage();
            assertTrue(msg.contains("400"), "Should be 400 Bad Request");
            assertTrue(msg.toLowerCase().contains("missing password"),
                    "Error body should mention 'Missing password'. Actual: " + msg);
        }
    }

    @Test
    public void registerFlowWithMissingPasswordAndChecking400StatusCodeTest() {
        Response resp = steps.registerRaw("sydney@fife", null); // missing password case from ReqRes docs
        assertEquals(resp.statusCode(), 400, "Expected 400 Bad Request");

        NegativeAuthFlow errorDto = steps.registerErrorDto("sydney@fife", null);
        assertNotNull(errorDto.getError(), "Error message should be present");
        assertTrue(errorDto.getError().toLowerCase().contains("missing password"),
                "Unexpected error: " + errorDto.getError());
    }

    @Test
    public void registerFlowWithMissingEmailAndChecking400StatusCodeTest() {
        Response resp = steps.registerRaw(null, "pistol"); // missing email/username
        assertEquals(resp.statusCode(), 400, "Expected 400 Bad Request");

        NegativeAuthFlow errorDto = steps.registerErrorDto(null, "pistol");
        assertNotNull(errorDto.getError(), "Error message should be present");
        // ReqRes typically: "Missing email or username"
        assertTrue(errorDto.getError().toLowerCase().contains("missing"),
                "Unexpected error: " + errorDto.getError());
    }
}
