package smokeTests;

import api.steps.UseApiSteps;
import org.testng.annotations.Test;
import utils.base.BaseApi;

import static core.TestStepLogger.logStep;
import static org.testng.Assert.*;

public class ApiSmokeTests extends BaseApi{
  private final UseApiSteps steps = new UseApiSteps();

    @Test
    public void userCrudTest() {

      logStep("Create and check is user user created correctly");
      String createdUser = steps.createUserAndGetId("Morpheus", "Smith", "morph@example.com", "leader");
      assertNotNull(createdUser, "Created user must have id");

      logStep("Update user");
      String updated = steps.updateUser(createdUser, "Morpheus", "Smith", "morph@example.com", "zion resident", getToken());
      assertTrue(updated.contains("updatedAt"));

      logStep("Get updated user");
      String userId = "2";
      String fetched = steps.getUser(userId, getToken());
      assertTrue(fetched.contains(userId));

      logStep("Delete created user");
      String deleted = steps.deleteUser(userId);
      assertNotNull(deleted);
    }
  }