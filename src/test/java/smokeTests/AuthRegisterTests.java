package smokeTests;

import api.steps.UseApiSteps;
import org.testng.annotations.Test;
import utils.json.Json;
import utils.request.exception.HttpsException;

import static core.TestStepLogger.logStep;
import static org.testng.Assert.*;

public class AuthRegisterTests {
    private final UseApiSteps steps = new UseApiSteps();

    @Test(description = "Try to register with positive data")
    public void registerTest() {
        logStep("Try to register with cred's");
        String body = steps.register("eve.holt@reqres.in", "pistol");

        logStep("Get created user");
        String id = Json.getAsText(body, "id");
        String token = Json.getAsText(body, "token");

        logStep("Check an errors");
        assertNotNull(id, "id should be present on successful registration");
        assertNotNull(token, "token should be present on successful registration");
    }

    @Test(description = "Try to register with negative data")
    public void registerWithoutPassTest() {
        try {
            logStep("Try to register without pass");
            steps.register("sydney@fife", null);

            logStep("Check an error");
            fail("Expected HttpsException (400) for missing password");

        } catch (HttpsException e) {
            String msg = e.getMessage();
            assertTrue(msg.contains("400"), "Should be 400 Bad Request");
            assertTrue(msg.toLowerCase().contains("missing password"),
                    "Error body should mention 'Missing password'. Actual: " + msg);
        }
    }
}
