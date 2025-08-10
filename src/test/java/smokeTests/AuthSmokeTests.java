package smokeTests;

import api.steps.UseApiSteps;
import utils.base.BaseApi;
import org.testng.annotations.Test;
import utils.json.Json;
import utils.request.exception.HttpsException;

import static core.TestStepLogger.logStep;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.fail;

public class AuthSmokeTests extends BaseApi {

  private final UseApiSteps steps = new UseApiSteps();

  @Test(description = "Try to auth with positive data")
  public void loginAndReturnTokenTest() {
    logStep("Try to auth with cred's");
    String body = steps.login("eve.holt@reqres.in", "cityslicka");

    logStep("Check token");
    String token = Json.getAsText(body, "token");

    logStep("Check errors");
    assertNotNull(token, "Token must be present for successful login");
    assertFalse(token.isBlank(), "Token should not be blank");
  }

  @Test(description = "Try to auth with negative data")
  public void loginWithEmptyPassAndCheckAnErrorTest() {
    try {
      logStep("Try to auth without pass");
      steps.login("peter@klaven", "");

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
